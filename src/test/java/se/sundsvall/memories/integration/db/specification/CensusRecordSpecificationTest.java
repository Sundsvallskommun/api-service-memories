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
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link CensusRecordSpecification} against a real MariaDB instance (Testcontainers), because the behaviour
 * under test — {@code LIKE} escaping, the collation-driven case insensitivity and how a dirty {@code FODAR} value
 * compares — is database behaviour, not Java behaviour.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class CensusRecordSpecificationTest {

	@Autowired
	private CensusRecordRepository censusRecordRepository;

	@BeforeEach
	void clearTable() {
		censusRecordRepository.deleteAll();
		censusRecordRepository.flush();
	}

	private CensusRecordEntity persist(final Integer id, final String lastName, final String firstName, final String gender, final String birthYear) {
		return censusRecordRepository.saveAndFlush(CensusRecordEntity.create()
			.withSource("1845")
			.withId(id)
			.withLastName(lastName)
			.withFirstName(firstName)
			.withGender(gender)
			.withBirthYear(birthYear));
	}

	private List<Integer> findIds(final Specification<CensusRecordEntity> specification) {
		return censusRecordRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(CensusRecordEntity::getId)
			.sorted()
			.toList();
	}

	@Test
	void hasLastNameMatchesASubstringRegardlessOfCase() {
		persist(1, "Nordin", "Anton", "man", "1850");
		persist(2, "Lindberg", "Anna", "kvinna", "1860");

		assertThat(findIds(CensusRecordSpecification.hasLastName("ordi"))).containsExactly(1);
		assertThat(findIds(CensusRecordSpecification.hasLastName("NORDIN"))).containsExactly(1);
	}

	@Test
	void hasLastNameTrimsAndTreatsBlankAsNoFilter() {
		persist(1, "Nordin", "Anton", "man", "1850");
		persist(2, "Lindberg", "Anna", "kvinna", "1860");

		assertThat(findIds(CensusRecordSpecification.hasLastName("  Nordin  "))).containsExactly(1);
		assertThat(findIds(CensusRecordSpecification.hasLastName("   "))).containsExactly(1, 2);
		assertThat(findIds(CensusRecordSpecification.hasLastName(null))).containsExactly(1, 2);
	}

	/**
	 * A name containing {@code %} must be matched literally, otherwise a user searching for it gets every row.
	 */
	@Test
	void hasLastNameEscapesWildcards() {
		persist(1, "Nordin", "Anton", "man", "1850");
		persist(2, "100%", "Anna", "kvinna", "1860");

		assertThat(findIds(CensusRecordSpecification.hasLastName("%"))).containsExactly(2);
	}

	@Test
	void hasFirstNameMatchesASubstring() {
		persist(1, "Nordin", "Anton", "man", "1850");
		persist(2, "Lindberg", "Anna", "kvinna", "1860");

		assertThat(findIds(CensusRecordSpecification.hasFirstName("ann"))).containsExactly(2);
	}

	/**
	 * The filter takes the canonical label and matches every spelling the register stores for it — words in any casing
	 * and the ISO 5218 codes — while the stray values match no label at all.
	 */
	@Test
	void hasGenderMatchesEverySpellingOfTheLabelRegardlessOfCase() {
		persist(1, "Nordin", "Anton", "man", "1850");
		persist(2, "Trolle", "Isidor", "1", "1840");
		persist(3, "Lindberg", "Anna", "Kvinna", "1860");
		persist(4, "Berg", "Anna", "2", "1870");
		persist(5, "Piga", "Brita", "1830-06-12", "1830");
		persist(6, "Okänd", null, "0", "1800");

		assertThat(findIds(CensusRecordSpecification.hasGender("MAN"))).containsExactly(1, 2);
		assertThat(findIds(CensusRecordSpecification.hasGender(" kvinna "))).containsExactly(3, 4);
		assertThat(findIds(CensusRecordSpecification.hasGender("Okänt"))).isEmpty();
		// A substring of the label is not a match — unlike the name filters, this one is exact.
		assertThat(findIds(CensusRecordSpecification.hasGender("kvinn"))).isEmpty();
		// Nor is a stored code accepted as a filter value: the API speaks labels only.
		assertThat(findIds(CensusRecordSpecification.hasGender("1"))).isEmpty();
		assertThat(findIds(CensusRecordSpecification.hasGender("   "))).containsExactly(1, 2, 3, 4, 5, 6);
		assertThat(findIds(CensusRecordSpecification.hasGender(null))).containsExactly(1, 2, 3, 4, 5, 6);
	}

	@Test
	void bornFiltersKeepRowsInsideTheRange() {
		persist(1, "a", null, null, "1849");
		persist(2, "b", null, null, "1850");
		persist(3, "c", null, null, "1900");

		assertThat(findIds(CensusRecordSpecification.bornFrom(1850))).containsExactly(2, 3);
		assertThat(findIds(CensusRecordSpecification.bornUntil(1850))).containsExactly(1, 2);
		assertThat(findIds(Specification.allOf(CensusRecordSpecification.bornFrom(1850), CensusRecordSpecification.bornUntil(1860)))).containsExactly(2);
	}

	/**
	 * {@code FODAR} is dirty free text. A row without a readable year has no birth year at all, so it must fall outside
	 * every range — including an upper bound, which it would satisfy if the value were read as year zero.
	 */
	@Test
	void bornFiltersExcludeRowsWithoutAParsableYear() {
		persist(1, "a", null, null, "1850");
		persist(2, "b", null, null, "okänt");
		persist(3, "c", null, null, "");
		persist(4, "d", null, null, null);

		assertThat(findIds(CensusRecordSpecification.bornUntil(2000))).containsExactly(1);
		assertThat(findIds(CensusRecordSpecification.bornFrom(1000))).containsExactly(1);
	}

	@Test
	void bornFiltersMatchEverythingWhenTheBoundIsNull() {
		persist(1, "a", null, null, "1850");
		persist(2, "b", null, null, "okänt");

		assertThat(findIds(CensusRecordSpecification.bornFrom(null))).containsExactly(1, 2);
		assertThat(findIds(CensusRecordSpecification.bornUntil(null))).containsExactly(1, 2);
	}

	@Test
	void findAllByParametersCombinesEveryFilter() {
		persist(1, "Nordin", "Anton", "man", "1850");
		persist(2, "Nordin", "Anna", "kvinna", "1850");
		persist(3, "Nordin", "Anton", "man", "1900");

		final var parameters = CensusRecordParameters.create()
			.withLastName("nordin")
			.withFirstName("anton")
			.withGender("man")
			.withYearFrom(1840)
			.withYearTo(1860);

		final var page = censusRecordRepository.findAllByParameters(parameters, Pageable.unpaged());

		assertThat(page.getContent()).extracting(CensusRecordEntity::getId).containsExactly(1);
	}
}
