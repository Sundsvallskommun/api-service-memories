package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link FilmSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class FilmSpecificationTest {

	@Autowired
	private FilmRepository filmRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTable() {
		filmRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		filmRepository.flush();
	}

	private FilmEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return filmRepository.saveAndFlush(FilmEntity.create()
			.withId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<FilmEntity> specification) {
		return filmRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(FilmEntity::getId)
			.sorted()
			.toList();
	}

	private TopographyEntity persistTopography(final int id, final String name) {
		final var topography = TopographyEntity.create().withId(id).withName(name);
		entityManager.persist(topography);
		entityManager.flush();
		return topography;
	}

	// ---------------------------------------------------------------------------------------------
	// published()
	// ---------------------------------------------------------------------------------------------

	@Test
	void publishedMatchesRowsWithBitFourSet() {
		persist(1, 4, "published", null);
		persist(2, 0, "unpublished", null);

		assertThat(findIds(FilmSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(FilmSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(FilmSpecification.published())).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// notDeleted()
	// ---------------------------------------------------------------------------------------------

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		filmRepository.flush();

		assertThat(findIds(FilmSpecification.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		// FILM currently has no such rows in production, unlike FOTO, TEXT and PUBL — but deletion works the same way
		// here, so the filter belongs on this service too.
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		filmRepository.flush();

		assertThat(findIds(FilmSpecification.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(FilmSpecification.published(), FilmSpecification.notDeleted()))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// hasId()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(FilmSpecification.hasId(2))).containsExactly(2);
	}

	// ---------------------------------------------------------------------------------------------
	// fetchTopography()
	// ---------------------------------------------------------------------------------------------

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		filmRepository.flush();
		entityManager.clear();

		final var films = filmRepository.findAll(FilmSpecification.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(films).hasSize(1);
		assertThat(films.getFirst().getTopography().getId()).isEqualTo(500);
		assertThat(films.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null);
		danglingForeignKey(1);

		final var film = filmRepository.findOne(Specification.allOf(
			FilmSpecification.fetchTopography(),
			FilmSpecification.hasId(1),
			FilmSpecification.notDeleted())).orElseThrow();

		assertThat(film.getTopography()).isNull();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		persist(2, 4, "b", null).setTopography(topography);
		filmRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = filmRepository.findAll(
			Specification.allOf(FilmSpecification.fetchTopography(), FilmSpecification.published()),
			Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}

	/**
	 * Points a film's {@code FILM_T_ID} at a TOPOGRAFI row that does not exist. The legacy schema declares no foreign
	 * key constraints, so this state is representable — and without a fetch join it would produce a lazy proxy that
	 * throws {@code EntityNotFoundException} when the mapper reads the place name.
	 */
	private void danglingForeignKey(final int filmId) {
		entityManager.createNativeQuery("UPDATE FILM SET FILM_T_ID = 999 WHERE FILM_ID = :id")
			.setParameter("id", filmId)
			.executeUpdate();
		entityManager.clear();

		// The association is only interesting if the raw column really points somewhere unresolvable.
		assertThat(entityManager.createNativeQuery("SELECT FILM_T_ID FROM FILM WHERE FILM_ID = :id")
			.setParameter("id", filmId)
			.getSingleResult())
			.asInstanceOf(LONG)
			.isEqualTo(999L);
		assertThat(entityManager.find(TopographyEntity.class, 999)).isNull();
	}

	// ---------------------------------------------------------------------------------------------
	// matches()
	// ---------------------------------------------------------------------------------------------

	@Test
	void matchesFindsSubstringInTitle() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(FilmSpecification.matches("midsommar"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Filmad vid hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(FilmSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "MIDSOMMAR", null);

		assertThat(findIds(FilmSpecification.matches("midsommar"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 4, "Midsommar i Timrå", null);

		assertThat(findIds(FilmSpecification.matches("midsommar sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Midsommar", "Filmad i Sundsvall");
		persist(2, 4, "Midsommar", "Filmad i Timrå");

		assertThat(findIds(FilmSpecification.matches("midsommar sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% film", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(FilmSpecification.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(FilmSpecification.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(FilmSpecification.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(FilmSpecification.matches(null))).containsExactly(1, 2);
		assertThat(findIds(FilmSpecification.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null);

		assertThat(findIds(FilmSpecification.matches("midsommar"))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// Composition — the repository methods the service calls
	// ---------------------------------------------------------------------------------------------

	@Test
	void findAllByParametersHidesUnpublishedAndDeletedRows() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 0, "Midsommar opublicerad", null);
		persist(3, 4, "Midsommar raderad", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null);
		filmRepository.flush();

		final var page = filmRepository.findAllByParameters(FilmParameters.create().withQuery("midsommar"), Pageable.unpaged());

		assertThat(page.getContent()).extracting(FilmEntity::getId).containsExactly(1);
	}

	@Test
	void findAllByParametersWithoutAQueryReturnsEveryVisibleRow() {
		persist(1, 4, "a", null);
		persist(2, 0, "b", null);

		final var page = filmRepository.findAllByParameters(FilmParameters.create(), Pageable.unpaged());

		assertThat(page.getContent()).extracting(FilmEntity::getId).containsExactly(1);
	}

	@Test
	void findVisibleByIdSkipsADeletedRowButKeepsAnUnpublishedOne() {
		persist(1, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(2, 0, "unpublished", null);
		filmRepository.flush();

		assertThat(filmRepository.findVisibleById(1)).isEmpty();
		assertThat(filmRepository.findVisibleById(2)).isPresent();
		assertThat(filmRepository.findVisibleById(999)).isEmpty();
	}

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 0, "Midsommar i Sundsvall", null);
		persist(3, 4, "Midsommar i Sundsvall", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null);
		filmRepository.flush();

		final var specification = Specification.allOf(
			FilmSpecification.notDeleted(),
			FilmSpecification.published(),
			FilmSpecification.matches("midsommar"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void allOfAcceptsUnrestrictedFiltersWithoutNarrowingTheResult() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 0, "Storgatan", null);

		final var specification = Specification.allOf(
			FilmSpecification.notDeleted(),
			FilmSpecification.published(),
			FilmSpecification.matches(null));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Midsommar i Sundsvall", null);
		persist(2, 4, "Midsommar i Timrå", null);
		persist(3, 0, "Midsommar i Härnösand", null);

		final var specification = Specification.allOf(
			FilmSpecification.published(),
			FilmSpecification.matches("midsommar"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = filmRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
