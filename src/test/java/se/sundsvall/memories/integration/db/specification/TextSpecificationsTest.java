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
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link TextSpecifications} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class TextSpecificationsTest {

	@Autowired
	private TextRepository textRepository;

	@Autowired
	private TopographyRepository topographyRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		textRepository.deleteAll();
		topographyRepository.deleteAll();
		textRepository.flush();
	}

	private TextEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return textRepository.saveAndFlush(TextEntity.create()
			.withTextId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<TextEntity> specification) {
		return textRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(TextEntity::getTextId)
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

		assertThat(findIds(TextSpecifications.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(TextSpecifications.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(TextSpecifications.published())).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// notDeleted()
	// ---------------------------------------------------------------------------------------------

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		textRepository.flush();

		assertThat(findIds(TextSpecifications.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		// TEXT has 33 such rows in production — deletion sets DELETEDDATE but leaves bit 4 set.
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		textRepository.flush();

		assertThat(findIds(TextSpecifications.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(TextSpecifications.published(), TextSpecifications.notDeleted()))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// hasId()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(TextSpecifications.hasId(2))).containsExactly(2);
	}

	// ---------------------------------------------------------------------------------------------
	// fetchTopography()
	// ---------------------------------------------------------------------------------------------

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = topographyRepository.saveAndFlush(
			TopographyEntity.create().withTId(500).withName("Sundsvall"));
		persist(1, 4, "a", null).setTopography(topography);
		textRepository.flush();
		entityManager.clear();

		final var texts = textRepository.findAll(TextSpecifications.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(texts).hasSize(1);
		assertThat(texts.getFirst().getTopography().getTId()).isEqualTo(500);
		assertThat(texts.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null);
		danglingForeignKey(1);

		final var text = textRepository.findOne(Specification.allOf(
			TextSpecifications.fetchTopography(),
			TextSpecifications.hasId(1),
			TextSpecifications.notDeleted())).orElseThrow();

		assertThat(text.getTopography()).isNull();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = topographyRepository.saveAndFlush(
			TopographyEntity.create().withTId(500).withName("Sundsvall"));
		persist(1, 4, "a", null).setTopography(topography);
		persist(2, 4, "b", null).setTopography(topography);
		textRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = textRepository.findAll(
			Specification.allOf(TextSpecifications.fetchTopography(), TextSpecifications.published()),
			Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}

	/**
	 * Points a text row's {@code D_T_ID} at a TOPOGRAFI row that does not exist. The legacy schema declares no foreign
	 * key constraints, so this state is representable — and without a fetch join it would produce a lazy proxy that
	 * throws {@code EntityNotFoundException} when the mapper reads the place name.
	 */
	private void danglingForeignKey(final int textId) {
		entityManager.createNativeQuery("UPDATE TEXT SET D_T_ID = 999 WHERE ID_ID = :id")
			.setParameter("id", textId)
			.executeUpdate();
		entityManager.clear();

		// The association is only interesting if the raw column really points somewhere unresolvable.
		assertThat(entityManager.createNativeQuery("SELECT D_T_ID FROM TEXT WHERE ID_ID = :id")
			.setParameter("id", textId)
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
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(TextSpecifications.matches("protokoll"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Handling rörande hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(TextSpecifications.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesDoesNotSearchTheDocumentBody() {
		// XMLTEXT is excluded from the searchable columns — it holds zero bytes across all production rows, and a LIKE
		// over a longtext column cannot use an index.
		persist(1, 4, "Protokoll", null).setXmltext("hemligheten står i brödtexten");
		textRepository.flush();

		assertThat(findIds(TextSpecifications.matches("hemligheten"))).isEmpty();
		assertThat(findIds(TextSpecifications.matches("protokoll"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "PROTOKOLL", null);

		assertThat(findIds(TextSpecifications.matches("protokoll"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 4, "Protokoll från Timrå", null);

		assertThat(findIds(TextSpecifications.matches("protokoll sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Protokoll", "Upprättat i Sundsvall");
		persist(2, 4, "Protokoll", "Upprättat i Timrå");

		assertThat(findIds(TextSpecifications.matches("protokoll sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% avskrift", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(TextSpecifications.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(TextSpecifications.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(TextSpecifications.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(TextSpecifications.matches(null))).containsExactly(1, 2);
		assertThat(findIds(TextSpecifications.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null);

		assertThat(findIds(TextSpecifications.matches("protokoll"))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// Composition — the shape the service uses
	// ---------------------------------------------------------------------------------------------

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 0, "Protokoll från Sundsvall", null);
		persist(3, 4, "Protokoll från Sundsvall", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null);
		textRepository.flush();

		final var specification = Specification.allOf(
			TextSpecifications.notDeleted(),
			TextSpecifications.published(),
			TextSpecifications.matches("protokoll"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 4, "Protokoll från Timrå", null);
		persist(3, 0, "Protokoll från Härnösand", null);

		final var specification = Specification.allOf(
			TextSpecifications.published(),
			TextSpecifications.matches("protokoll"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = textRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
