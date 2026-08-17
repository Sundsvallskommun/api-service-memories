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
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.time.Month.MARCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link AudioSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class AudioSpecificationTest {

	@Autowired
	private AudioRepository audioRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		audioRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM OCM").executeUpdate();
		audioRepository.flush();
	}

	private AudioEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return audioRepository.saveAndFlush(AudioEntity.create()
			.withId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<AudioEntity> specification) {
		return audioRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(AudioEntity::getId)
			.sorted()
			.toList();
	}

	private TopographyEntity persistTopography(final int id, final String name) {
		final var topography = TopographyEntity.create().withId(id).withName(name);
		entityManager.persist(topography);
		entityManager.flush();
		return topography;
	}

	private OcmEntity persistSubject(final int id, final String text) {
		final var subject = OcmEntity.create().withId(id).withText(text);
		entityManager.persist(subject);
		entityManager.flush();
		return subject;
	}

	@Test
	void publishedMatchesRowsWithBitFourSet() {
		persist(1, 4, "published", null);
		persist(2, 0, "unpublished", null);

		assertThat(findIds(AudioSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(AudioSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(AudioSpecification.published())).isEmpty();
	}

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		audioRepository.flush();

		assertThat(findIds(AudioSpecification.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		audioRepository.flush();

		assertThat(findIds(AudioSpecification.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(AudioSpecification.published(), AudioSpecification.notDeleted()))).isEmpty();
	}

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(AudioSpecification.hasId(2))).containsExactly(2);
	}

	@Test
	void matchesLocationFindsThePlaceThroughTheAssociation() {
		persist(1, 4, "a", null).setTopography(persistTopography(500, "Sundsvall"));
		persist(2, 4, "b", null).setTopography(persistTopography(501, "Timrå"));
		audioRepository.flush();

		assertThat(findIds(AudioSpecification.matchesLocation("sundsvall"))).containsExactly(1);
	}

	/**
	 * TOPNAMN is the primary place name, but a row can carry only PLATS. Both are searched, which is what the native
	 * query this replaces did.
	 */
	@Test
	void matchesLocationAlsoSearchesThePlaceColumn() {
		final var topography = TopographyEntity.create().withId(500).withPlace("Njurunda");
		entityManager.persist(topography);
		entityManager.flush();
		persist(1, 4, "a", null).setTopography(topography);
		audioRepository.flush();

		assertThat(findIds(AudioSpecification.matchesLocation("njurunda"))).containsExactly(1);
	}

	/**
	 * A recording without topography still has its free-text place, so the association is joined with a left join.
	 */
	@Test
	void matchesLocationFallsBackToTheFreeTextPlace() {
		persist(1, 4, "a", null).setLocationText("Alnö");
		audioRepository.flush();

		assertThat(findIds(AudioSpecification.matchesLocation("alnö"))).containsExactly(1);
	}

	@Test
	void matchesLocationMatchesEverythingWhenBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(AudioSpecification.matchesLocation("   "))).containsExactly(1, 2);
	}

	@Test
	void yearFiltersKeepRowsInsideTheRange() {
		persistDated(1, "1969-12-31");
		persistDated(2, "1970-05-01");
		persistDated(3, "1990-01-01");

		assertThat(findIds(AudioSpecification.yearAtLeast(1970))).containsExactly(2, 3);
		assertThat(findIds(AudioSpecification.yearAtMost(1970))).containsExactly(1, 2);
		assertThat(findIds(Specification.allOf(AudioSpecification.yearAtLeast(1970), AudioSpecification.yearAtMost(1980)))).containsExactly(2);
	}

	/**
	 * DATUM is free text: it holds blanks and words as well as dates. Such a row has no year at all, so it must fall
	 * outside every range — including an upper bound, which it would satisfy if the value were read as year zero.
	 */
	@Test
	void yearFiltersExcludeRowsWithoutAParsableYear() {
		persistDated(1, "1970");
		persistDated(2, "okänt");
		persistDated(3, "");
		persistDated(4, null);

		assertThat(findIds(AudioSpecification.yearAtMost(2000))).containsExactly(1);
		assertThat(findIds(AudioSpecification.yearAtLeast(1900))).containsExactly(1);
	}

	@Test
	void yearFiltersMatchEverythingWhenTheBoundIsNull() {
		persistDated(1, "1970");
		persistDated(2, "okänt");

		assertThat(findIds(AudioSpecification.yearAtLeast(null))).containsExactly(1, 2);
		assertThat(findIds(AudioSpecification.yearAtMost(null))).containsExactly(1, 2);
	}

	/**
	 * The location filter reuses the join the fetch already created rather than adding a second one. Composed with the
	 * fetch it must still match, still count right, and never return the same row twice.
	 */
	@Test
	void matchesLocationComposesWithTheFetchJoin() {
		persist(1, 4, "a", null).setTopography(persistTopography(500, "Sundsvall"));
		persist(2, 4, "b", null).setTopography(persistTopography(501, "Timrå"));
		audioRepository.flush();

		final var page = audioRepository.findAll(
			Specification.allOf(AudioSpecification.fetchTopography(), AudioSpecification.matchesLocation("sundsvall")),
			Pageable.unpaged());

		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent()).singleElement()
			.extracting(audio -> audio.getTopography().getDisplayName())
			.isEqualTo("Sundsvall");
	}

	private void persistDated(final Integer id, final String date) {
		persist(id, 4, "dated " + id, null).setDate(date);
		audioRepository.flush();
	}

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		audioRepository.flush();
		entityManager.clear();

		final var audios = audioRepository.findAll(AudioSpecification.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(audios).hasSize(1);
		assertThat(audios.getFirst().getTopography().getId()).isEqualTo(500);
		assertThat(audios.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null);
		danglingForeignKey(1);

		final var audio = audioRepository.findOne(Specification.allOf(
			AudioSpecification.fetchTopography(),
			AudioSpecification.hasId(1),
			AudioSpecification.notDeleted())).orElseThrow();

		assertThat(audio.getTopography()).isNull();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		persist(2, 4, "b", null).setTopography(topography);
		audioRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = audioRepository.findAll(
			Specification.allOf(AudioSpecification.fetchTopography(), AudioSpecification.published()),
			Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}

	/**
	 * Points an audio row's {@code LJUD_T_ID} at a TOPOGRAFI row that does not exist. The legacy schema declares no
	 * foreign key constraints, so this state is representable — and without a fetch join it would produce a lazy proxy
	 * that throws {@code EntityNotFoundException} when the mapper reads the place name.
	 */
	private void danglingForeignKey(final int audioId) {
		entityManager.createNativeQuery("UPDATE LJUD SET LJUD_T_ID = 999 WHERE LJUD_ID = :id")
			.setParameter("id", audioId)
			.executeUpdate();
		entityManager.clear();

		// The association is only interesting if the raw column really points somewhere unresolvable.
		assertThat(entityManager.createNativeQuery("SELECT LJUD_T_ID FROM LJUD WHERE LJUD_ID = :id")
			.setParameter("id", audioId)
			.getSingleResult())
			.asInstanceOf(LONG)
			.isEqualTo(999L);
		assertThat(entityManager.find(TopographyEntity.class, 999)).isNull();
	}

	@Test
	void fetchSubjectResolvesTheAssociation() {
		final var subject = persistSubject(700, "Intervju");
		persist(1, 4, "a", null).setSubject(subject);
		audioRepository.flush();
		entityManager.clear();

		final var rows = audioRepository.findAll(AudioSpecification.fetchSubject(), Pageable.unpaged()).getContent();

		assertThat(rows).hasSize(1);
		assertThat(rows.getFirst().getSubject().getId()).isEqualTo(700);
		assertThat(rows.getFirst().getSubject().getDisplayName()).isEqualTo("Intervju");
	}

	@Test
	void fetchSubjectLeavesTheAssociationNullWhenTheForeignKeyDangles() {
		persist(1, 4, "a", null);
		entityManager.createNativeQuery("UPDATE LJUD SET LJUD_O_ID = 999 WHERE LJUD_ID = 1").executeUpdate();
		entityManager.clear();
		assertThat(entityManager.find(OcmEntity.class, 999)).isNull();

		final var row = audioRepository.findOne(
			AudioSpecification.fetchSubject().and(AudioSpecification.hasId(1))).orElseThrow();

		assertThat(row.getSubject()).isNull();
	}

	@Test
	void matchesFindsSubstringInTitle() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(AudioSpecification.matches("intervju"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Inspelad vid hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(AudioSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "INTERVJU", null);

		assertThat(findIds(AudioSpecification.matches("intervju"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 4, "Intervju i Timrå", null);

		assertThat(findIds(AudioSpecification.matches("intervju sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Intervju", "Inspelad i Sundsvall");
		persist(2, 4, "Intervju", "Inspelad i Timrå");

		assertThat(findIds(AudioSpecification.matches("intervju sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% ljud", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(AudioSpecification.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(AudioSpecification.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(AudioSpecification.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(AudioSpecification.matches(null))).containsExactly(1, 2);
		assertThat(findIds(AudioSpecification.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null);

		assertThat(findIds(AudioSpecification.matches("intervju"))).isEmpty();
	}

	@Test
	void findAllByParametersHidesUnpublishedAndDeletedRows() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 0, "Intervju opublicerad", null);
		persist(3, 4, "Intervju raderad", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		persist(4, 4, "Storgatan", null);
		audioRepository.flush();

		final var page = audioRepository.findAllByParameters(AudioParameters.create().withQuery("intervju"), Pageable.unpaged());

		assertThat(page.getContent()).extracting(AudioEntity::getId).containsExactly(1);
	}

	@Test
	void findAllByParametersWithoutAQueryReturnsEveryVisibleRow() {
		persist(1, 4, "a", null);
		persist(2, 0, "b", null);

		final var page = audioRepository.findAllByParameters(AudioParameters.create(), Pageable.unpaged());

		assertThat(page.getContent()).extracting(AudioEntity::getId).containsExactly(1);
	}

	@Test
	void findVisibleByIdSkipsADeletedRowButKeepsAnUnpublishedOne() {
		persist(1, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		persist(2, 0, "unpublished", null);
		audioRepository.flush();

		assertThat(audioRepository.findVisibleById(1)).isEmpty();
		assertThat(audioRepository.findVisibleById(2)).isPresent();
		assertThat(audioRepository.findVisibleById(999)).isEmpty();
	}

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 0, "Intervju i Sundsvall", null);
		persist(3, 4, "Intervju i Sundsvall", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		persist(4, 4, "Storgatan", null);
		audioRepository.flush();

		final var specification = Specification.allOf(
			AudioSpecification.notDeleted(),
			AudioSpecification.published(),
			AudioSpecification.matches("intervju"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 4, "Intervju i Timrå", null);
		persist(3, 0, "Intervju i Härnösand", null);

		final var specification = Specification.allOf(
			AudioSpecification.published(),
			AudioSpecification.matches("intervju"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = audioRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
