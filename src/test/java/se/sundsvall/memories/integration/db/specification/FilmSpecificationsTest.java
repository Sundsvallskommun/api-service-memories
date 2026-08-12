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
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link FilmSpecifications} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class FilmSpecificationsTest {

	@Autowired
	private FilmRepository filmRepository;

	@BeforeEach
	void clearTable() {
		filmRepository.deleteAll();
		filmRepository.flush();
	}

	private FilmEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return filmRepository.saveAndFlush(FilmEntity.create()
			.withFilmId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<FilmEntity> specification) {
		return filmRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(FilmEntity::getFilmId)
			.sorted()
			.toList();
	}

	// ---------------------------------------------------------------------------------------------
	// published()
	// ---------------------------------------------------------------------------------------------

	@Test
	void publishedMatchesRowsWithBitFourSet() {
		persist(1, 4, "published", null);
		persist(2, 0, "unpublished", null);

		assertThat(findIds(FilmSpecifications.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(FilmSpecifications.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(FilmSpecifications.published())).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// notDeleted()
	// ---------------------------------------------------------------------------------------------

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		filmRepository.flush();

		assertThat(findIds(FilmSpecifications.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		// FILM currently has no such rows in production, unlike FOTO, TEXT and PUBL — but deletion works the same way
		// here, so the filter belongs on this service too.
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		filmRepository.flush();

		assertThat(findIds(FilmSpecifications.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(FilmSpecifications.published(), FilmSpecifications.notDeleted()))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// hasId()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(FilmSpecifications.hasId(2))).containsExactly(2);
	}

	// ---------------------------------------------------------------------------------------------
	// matches()
	// ---------------------------------------------------------------------------------------------

	@Test
	void matchesFindsSubstringInTitle() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(FilmSpecifications.matches("midsommar"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Filmad vid hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(FilmSpecifications.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "MIDSOMMAR", null);

		assertThat(findIds(FilmSpecifications.matches("midsommar"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 4, "Midsommar i Timrå", null);

		assertThat(findIds(FilmSpecifications.matches("midsommar sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Midsommar", "Filmad i Sundsvall");
		persist(2, 4, "Midsommar", "Filmad i Timrå");

		assertThat(findIds(FilmSpecifications.matches("midsommar sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% film", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(FilmSpecifications.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(FilmSpecifications.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(FilmSpecifications.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(FilmSpecifications.matches(null))).containsExactly(1, 2);
		assertThat(findIds(FilmSpecifications.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null);

		assertThat(findIds(FilmSpecifications.matches("midsommar"))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// Composition — the shape the service will use
	// ---------------------------------------------------------------------------------------------

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 0, "Midsommar i Sundsvall", null);
		persist(3, 4, "Midsommar i Sundsvall", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null);
		filmRepository.flush();

		final var specification = Specification.allOf(
			FilmSpecifications.notDeleted(),
			FilmSpecifications.published(),
			FilmSpecifications.matches("midsommar"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void allOfAcceptsUnrestrictedFiltersWithoutNarrowingTheResult() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 0, "Storgatan", null);

		final var specification = Specification.allOf(
			FilmSpecifications.notDeleted(),
			FilmSpecifications.published(),
			FilmSpecifications.matches(null));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 4, "Midsommar i Timrå", null);
		persist(3, 0, "Midsommar i Härnösand", null);

		final var specification = Specification.allOf(
			FilmSpecifications.published(),
			FilmSpecifications.matches("midsommar"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = filmRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
