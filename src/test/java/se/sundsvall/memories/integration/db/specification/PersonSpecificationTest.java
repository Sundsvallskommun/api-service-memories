package se.sundsvall.memories.integration.db.specification;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.PersonParameters;
import se.sundsvall.memories.integration.db.PersonRepository;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link PersonSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and how a dirty {@code FODDAT} value compares — is
 * database behaviour, not Java behaviour.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class PersonSpecificationTest {

	private static final Integer PUBLISHED = 4;

	@Autowired
	private PersonRepository personRepository;

	@BeforeEach
	void clearTable() {
		personRepository.deleteAll();
		personRepository.flush();
	}

	private PersonEntity persist(final Integer id, final Integer options, final String lastName) {
		return personRepository.saveAndFlush(PersonEntity.create()
			.withPersonId(id)
			.withOptions(options)
			.withLastName(lastName));
	}

	private List<Integer> findIds(final Specification<PersonEntity> specification) {
		return personRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(PersonEntity::getPersonId)
			.sorted()
			.toList();
	}

	@Test
	void publishedMatchesRowsWithBitFourSet() {
		persist(1, PUBLISHED, "published");
		persist(2, 0, "unpublished");
		persist(3, 6, "bit 2 and bit 4");

		assertThat(findIds(PersonSpecification.published())).containsExactly(1, 3);
	}

	/**
	 * {@code P_ID = 0} is the sentinel other tables point at to mean "no person linked". It carries
	 * {@code OPTIONS = 6}, so it is flagged as published and the published filter alone does not hide it.
	 */
	@Test
	void notPlaceholderExcludesTheSentinelRowThePublishedFilterKeeps() {
		persist(0, 6, "ingen person");
		persist(1, PUBLISHED, "Nordin");

		assertThat(findIds(PersonSpecification.published())).containsExactly(0, 1);
		assertThat(findIds(PersonSpecification.notPlaceholder())).containsExactly(1);
	}

	/**
	 * Deletion sets {@code DELETEDDATE} but leaves the published bit set, so the published filter alone keeps the row —
	 * which is what let a deleted person stay findable through both the search and get-by-id.
	 */
	@Test
	void notDeletedExcludesTheSoftDeletedRowThePublishedFilterKeeps() {
		persist(1, PUBLISHED, "Nordin");
		persist(2, PUBLISHED, "Nordin raderad").setDeletedDate(LocalDate.of(2026, JANUARY, 1));
		personRepository.flush();

		assertThat(findIds(PersonSpecification.published())).containsExactly(1, 2);
		assertThat(findIds(PersonSpecification.notDeleted())).containsExactly(1);
		assertThat(personRepository.findAllByParameters(PersonParameters.create(), Pageable.unpaged()).getContent())
			.extracting(PersonEntity::getPersonId).containsExactly(1);
		assertThat(personRepository.findVisibleById(2)).isEmpty();
	}

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, PUBLISHED, "a");
		persist(2, PUBLISHED, "b");

		assertThat(findIds(PersonSpecification.hasId(2))).containsExactly(2);
	}

	@Test
	void nameAndParishFiltersMatchASubstringRegardlessOfCase() {
		personRepository.saveAndFlush(PersonEntity.create().withPersonId(1).withOptions(PUBLISHED)
			.withLastName("Nordin").withFirstName("Anton").withBirthParish("Sundsvall").withGender("man"));
		personRepository.saveAndFlush(PersonEntity.create().withPersonId(2).withOptions(PUBLISHED)
			.withLastName("Lindberg").withFirstName("Anna").withBirthParish("Timrå").withGender("kvinna"));

		assertThat(findIds(PersonSpecification.hasLastName("ORDI"))).containsExactly(1);
		assertThat(findIds(PersonSpecification.hasFirstName("ann"))).containsExactly(2);
		assertThat(findIds(PersonSpecification.hasBirthParish("timrå"))).containsExactly(2);
		assertThat(findIds(PersonSpecification.hasGender("MAN"))).containsExactly(1);
	}

	@Test
	void filtersTreatBlankAsNoFilter() {
		persist(1, PUBLISHED, "Nordin");
		persist(2, PUBLISHED, "Lindberg");

		assertThat(findIds(PersonSpecification.hasLastName("   "))).containsExactly(1, 2);
		assertThat(findIds(PersonSpecification.hasGender("   "))).containsExactly(1, 2);
		assertThat(findIds(PersonSpecification.hasBirthParish(null))).containsExactly(1, 2);
	}

	@Test
	void bornFiltersKeepRowsInsideTheRange() {
		persistBorn(1, "1849-05-01");
		persistBorn(2, "1850-05-01");
		persistBorn(3, "1900");

		assertThat(findIds(PersonSpecification.bornFrom(1850))).containsExactly(2, 3);
		assertThat(findIds(PersonSpecification.bornUntil(1850))).containsExactly(1, 2);
	}

	/**
	 * {@code FODDAT} is dirty free text. A row without a readable year has no birth date at all, so it must fall outside
	 * every range — including an upper bound, which it would satisfy if the value were read as year zero.
	 */
	@Test
	void bornFiltersExcludeRowsWithoutAParsableYear() {
		persistBorn(1, "1850");
		persistBorn(2, "okänt");
		persistBorn(3, "");
		persistBorn(4, null);

		assertThat(findIds(PersonSpecification.bornUntil(2000))).containsExactly(1);
		assertThat(findIds(PersonSpecification.bornFrom(1000))).containsExactly(1);
	}

	@Test
	void findAllByParametersCombinesEveryFilter() {
		persistSearchable(0, "Nordin", "Anton", "man", "1850");
		persistSearchable(1, "Nordin", "Anton", "man", "1850");
		persistSearchable(2, "Nordin", "Anna", "kvinna", "1850");
		persistSearchable(3, "Nordin", "Anton", "man", "1900");
		personRepository.saveAndFlush(PersonEntity.create().withPersonId(4).withOptions(0)
			.withLastName("Nordin").withFirstName("Anton").withGender("man").withBirthDate("1850"));

		final var parameters = PersonParameters.create()
			.withLastName("nordin")
			.withFirstName("anton")
			.withGender("man")
			.withYearFrom(1840)
			.withYearTo(1860);

		final var page = personRepository.findAllByParameters(parameters, Pageable.unpaged());

		// Row 0 is the placeholder, row 2 has another first name, row 3 another year, row 4 is unpublished.
		assertThat(page.getContent()).extracting(PersonEntity::getPersonId).containsExactly(1);
	}

	/**
	 * Get-by-id deliberately keeps unpublished persons reachable — only the placeholder row is hidden.
	 */
	@Test
	void findVisibleByIdKeepsUnpublishedButHidesThePlaceholder() {
		persist(0, 6, "ingen person");
		persist(1, 0, "unpublished");

		assertThat(personRepository.findVisibleById(1)).isPresent();
		assertThat(personRepository.findVisibleById(0)).isEmpty();
	}

	private void persistBorn(final Integer id, final String birthDate) {
		personRepository.saveAndFlush(PersonEntity.create()
			.withPersonId(id)
			.withOptions(PUBLISHED)
			.withBirthDate(birthDate));
	}

	private void persistSearchable(final Integer id, final String lastName, final String firstName, final String gender, final String birthDate) {
		personRepository.saveAndFlush(PersonEntity.create()
			.withPersonId(id)
			.withOptions(PUBLISHED)
			.withLastName(lastName)
			.withFirstName(firstName)
			.withGender(gender)
			.withBirthDate(birthDate));
	}
}
