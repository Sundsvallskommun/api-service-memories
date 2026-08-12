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
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link AudioSpecifications} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class AudioSpecificationsTest {

	@Autowired
	private AudioRepository audioRepository;

	@Autowired
	private TopographyRepository topographyRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		audioRepository.deleteAll();
		topographyRepository.deleteAll();
		audioRepository.flush();
	}

	private AudioEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return audioRepository.saveAndFlush(AudioEntity.create()
			.withAudioId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<AudioEntity> specification) {
		return audioRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(AudioEntity::getAudioId)
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

		assertThat(findIds(AudioSpecifications.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(AudioSpecifications.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(AudioSpecifications.published())).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// notDeleted()
	// ---------------------------------------------------------------------------------------------

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		audioRepository.flush();

		assertThat(findIds(AudioSpecifications.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		audioRepository.flush();

		assertThat(findIds(AudioSpecifications.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(AudioSpecifications.published(), AudioSpecifications.notDeleted()))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// hasId()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(AudioSpecifications.hasId(2))).containsExactly(2);
	}

	// ---------------------------------------------------------------------------------------------
	// fetchTopography()
	// ---------------------------------------------------------------------------------------------

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = topographyRepository.saveAndFlush(
			TopographyEntity.create().withTId(500).withName("Sundsvall"));
		persist(1, 4, "a", null).setTopography(topography);
		audioRepository.flush();
		entityManager.clear();

		final var audios = audioRepository.findAll(AudioSpecifications.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(audios).hasSize(1);
		assertThat(audios.getFirst().getTopography().getTId()).isEqualTo(500);
		assertThat(audios.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null);
		danglingForeignKey(1);

		final var audio = audioRepository.findOne(Specification.allOf(
			AudioSpecifications.fetchTopography(),
			AudioSpecifications.hasId(1),
			AudioSpecifications.notDeleted())).orElseThrow();

		assertThat(audio.getTopography()).isNull();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = topographyRepository.saveAndFlush(
			TopographyEntity.create().withTId(500).withName("Sundsvall"));
		persist(1, 4, "a", null).setTopography(topography);
		persist(2, 4, "b", null).setTopography(topography);
		audioRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = audioRepository.findAll(
			Specification.allOf(AudioSpecifications.fetchTopography(), AudioSpecifications.published()),
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
		assertThat(topographyRepository.findById(999)).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// matches()
	// ---------------------------------------------------------------------------------------------

	@Test
	void matchesFindsSubstringInTitle() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(AudioSpecifications.matches("intervju"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Inspelad vid hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(AudioSpecifications.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "INTERVJU", null);

		assertThat(findIds(AudioSpecifications.matches("intervju"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 4, "Intervju i Timrå", null);

		assertThat(findIds(AudioSpecifications.matches("intervju sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Intervju", "Inspelad i Sundsvall");
		persist(2, 4, "Intervju", "Inspelad i Timrå");

		assertThat(findIds(AudioSpecifications.matches("intervju sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% ljud", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(AudioSpecifications.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(AudioSpecifications.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(AudioSpecifications.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(AudioSpecifications.matches(null))).containsExactly(1, 2);
		assertThat(findIds(AudioSpecifications.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null);

		assertThat(findIds(AudioSpecifications.matches("intervju"))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// Composition — the shape the service uses
	// ---------------------------------------------------------------------------------------------

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 0, "Intervju i Sundsvall", null);
		persist(3, 4, "Intervju i Sundsvall", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null);
		audioRepository.flush();

		final var specification = Specification.allOf(
			AudioSpecifications.notDeleted(),
			AudioSpecifications.published(),
			AudioSpecifications.matches("intervju"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Intervju i Sundsvall", null);
		persist(2, 4, "Intervju i Timrå", null);
		persist(3, 0, "Intervju i Härnösand", null);

		final var specification = Specification.allOf(
			AudioSpecifications.published(),
			AudioSpecifications.matches("intervju"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = audioRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
