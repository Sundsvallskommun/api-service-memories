package se.sundsvall.memories.integration.db.specification;

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
import se.sundsvall.memories.api.model.SeamanParameters;
import se.sundsvall.memories.integration.db.SeamanRepository;
import se.sundsvall.memories.integration.db.model.SeamanEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link SeamanSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — {@code LIKE} escaping, the collation-driven case insensitivity and how a dirty {@code FODDAT} value compares —
 * is database behaviour, not Java behaviour.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class SeamanSpecificationTest {

	@Autowired
	private SeamanRepository seamanRepository;

	@BeforeEach
	void clearTable() {
		seamanRepository.deleteAll();
		seamanRepository.flush();
	}

	private SeamanEntity persist(final Integer id, final String lastName1, final String lastName2, final String firstName) {
		return seamanRepository.saveAndFlush(SeamanEntity.create()
			.withId(id)
			.withLastName1(lastName1)
			.withLastName2(lastName2)
			.withFirstName(firstName));
	}

	private List<Integer> findIds(final Specification<SeamanEntity> specification) {
		return seamanRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(SeamanEntity::getId)
			.sorted()
			.toList();
	}

	/**
	 * The register is inconsistent about which of the two surname columns is used, so the filter has to match either.
	 */
	@Test
	void hasLastNameMatchesEitherSurnameColumn() {
		persist(1, "Nordin", null, "Anton");
		persist(2, null, "Lindberg", "Anna");
		persist(3, "Ek", "Sjöberg", "Erik");

		assertThat(findIds(SeamanSpecification.hasLastName("nordin"))).containsExactly(1);
		assertThat(findIds(SeamanSpecification.hasLastName("lindberg"))).containsExactly(2);
		assertThat(findIds(SeamanSpecification.hasLastName("sjöberg"))).containsExactly(3);
	}

	@Test
	void hasLastNameTrimsAndTreatsBlankAsNoFilter() {
		persist(1, "Nordin", null, "Anton");
		persist(2, "Lindberg", null, "Anna");

		assertThat(findIds(SeamanSpecification.hasLastName("  Nordin  "))).containsExactly(1);
		assertThat(findIds(SeamanSpecification.hasLastName("   "))).containsExactly(1, 2);
		assertThat(findIds(SeamanSpecification.hasLastName(null))).containsExactly(1, 2);
	}

	@Test
	void hasFirstNameAndBirthParishMatchASubstring() {
		seamanRepository.saveAndFlush(SeamanEntity.create().withId(1).withFirstName("Anton").withBirthParish("Sundsvall"));
		seamanRepository.saveAndFlush(SeamanEntity.create().withId(2).withFirstName("Anna").withBirthParish("Timrå"));

		assertThat(findIds(SeamanSpecification.hasFirstName("anto"))).containsExactly(1);
		assertThat(findIds(SeamanSpecification.hasBirthParish("timrå"))).containsExactly(2);
	}

	@Test
	void bornFiltersKeepRowsInsideTheRange() {
		persistBorn(1, "1849-05-01");
		persistBorn(2, "1850-05-01");
		persistBorn(3, "1900");

		assertThat(findIds(SeamanSpecification.bornFrom(1850))).containsExactly(2, 3);
		assertThat(findIds(SeamanSpecification.bornUntil(1850))).containsExactly(1, 2);
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

		assertThat(findIds(SeamanSpecification.bornUntil(2000))).containsExactly(1);
		assertThat(findIds(SeamanSpecification.bornFrom(1000))).containsExactly(1);
	}

	@Test
	void findAllByParametersCombinesEveryFilter() {
		seamanRepository.saveAndFlush(SeamanEntity.create().withId(1).withLastName1("Nordin").withFirstName("Anton")
			.withBirthParish("Sundsvall").withBirthDate("1850"));
		seamanRepository.saveAndFlush(SeamanEntity.create().withId(2).withLastName2("Nordin").withFirstName("Anna")
			.withBirthParish("Sundsvall").withBirthDate("1850"));
		seamanRepository.saveAndFlush(SeamanEntity.create().withId(3).withLastName1("Nordin").withFirstName("Anton")
			.withBirthParish("Sundsvall").withBirthDate("1900"));

		final var parameters = SeamanParameters.create()
			.withLastName("nordin")
			.withFirstName("anton")
			.withBirthParish("sundsvall")
			.withYearFrom(1840)
			.withYearTo(1860);

		final var page = seamanRepository.findAllByParameters(parameters, Pageable.unpaged());

		assertThat(page.getContent()).extracting(SeamanEntity::getId).containsExactly(1);
	}

	private void persistBorn(final Integer id, final String birthDate) {
		seamanRepository.saveAndFlush(SeamanEntity.create()
			.withId(id)
			.withBirthDate(birthDate));
	}
}
