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
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.time.Month.MARCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link TextSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class TextSpecificationTest {

	@Autowired
	private TextRepository textRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		textRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM OCM").executeUpdate();
		textRepository.flush();
	}

	private TextEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return textRepository.saveAndFlush(TextEntity.create()
			.withId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<TextEntity> specification) {
		return textRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(TextEntity::getId)
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

		assertThat(findIds(TextSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(TextSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(TextSpecification.published())).isEmpty();
	}

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		textRepository.flush();

		assertThat(findIds(TextSpecification.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		// TEXT has 33 such rows in production — deletion sets DELETEDDATE but leaves bit 4 set.
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		textRepository.flush();

		assertThat(findIds(TextSpecification.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(TextSpecification.published(), TextSpecification.notDeleted()))).isEmpty();
	}

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(TextSpecification.hasId(2))).containsExactly(2);
	}

	@Test
	void matchesLocationFindsThePlaceThroughTheAssociation() {
		persist(1, 4, "a", null).setTopography(persistTopography(500, "Sundsvall"));
		persist(2, 4, "b", null).setTopography(persistTopography(501, "Timrå"));
		textRepository.flush();

		assertThat(findIds(TextSpecification.matchesLocation("sundsvall"))).containsExactly(1);
	}

	/**
	 * A document without topography still has its free-text place, so the association is joined with a left join.
	 */
	@Test
	void matchesLocationFallsBackToTheFreeTextPlace() {
		persist(1, 4, "a", null).setLocationText("Alnö");
		textRepository.flush();

		assertThat(findIds(TextSpecification.matchesLocation("alnö"))).containsExactly(1);
	}

	/**
	 * A document covers a period from DOKDATUM to DOKDATUM_SLUT, and the filters keep the documents whose period
	 * overlaps the requested range.
	 */
	@Test
	void yearFiltersKeepDocumentsWhosePeriodOverlapsTheRange() {
		persistPeriod(1, "1950", "1955");
		persistPeriod(2, "1958", "1965");
		persistPeriod(3, "1970", "1975");

		assertThat(findIds(TextSpecification.yearAtLeast(1960))).containsExactly(2, 3);
		assertThat(findIds(TextSpecification.yearAtMost(1960))).containsExactly(1, 2);
		assertThat(findIds(Specification.allOf(TextSpecification.yearAtLeast(1958), TextSpecification.yearAtMost(1965)))).containsExactly(2);
	}

	/**
	 * DOKDATUM_SLUT is empty for a document with a single date, so the period ends where it starts.
	 */
	@Test
	void yearAtLeastFallsBackToTheStartOfThePeriod() {
		persistPeriod(1, "1958", null);
		persistPeriod(2, "1958", "");

		assertThat(findIds(TextSpecification.yearAtLeast(1958))).containsExactly(1, 2);
		assertThat(findIds(TextSpecification.yearAtLeast(1959))).isEmpty();
	}

	/**
	 * DOKDATUM is free text: it holds blanks and words as well as dates. Such a document has no period at all, so it
	 * must fall outside every range — including an upper bound, which it would satisfy if the value were read as year
	 * zero.
	 */
	@Test
	void yearFiltersExcludeRowsWithoutAParsableYear() {
		persistPeriod(1, "1958", "1965");
		persistPeriod(2, "okänt", null);
		persistPeriod(3, "", null);
		persistPeriod(4, null, null);

		assertThat(findIds(TextSpecification.yearAtMost(2000))).containsExactly(1);
		assertThat(findIds(TextSpecification.yearAtLeast(1900))).containsExactly(1);
	}

	private void persistPeriod(final Integer id, final String documentDate, final String documentEndDate) {
		final var text = persist(id, 4, "dated " + id, null);
		text.setDocumentDate(documentDate);
		text.setDocumentEndDate(documentEndDate);
		textRepository.flush();
	}

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		textRepository.flush();
		entityManager.clear();

		final var texts = textRepository.findAll(TextSpecification.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(texts).hasSize(1);
		assertThat(texts.getFirst().getTopography().getId()).isEqualTo(500);
		assertThat(texts.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null);
		danglingForeignKey(1);

		final var text = textRepository.findOne(Specification.allOf(
			TextSpecification.fetchTopography(),
			TextSpecification.hasId(1),
			TextSpecification.notDeleted())).orElseThrow();

		assertThat(text.getTopography()).isNull();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		persist(2, 4, "b", null).setTopography(topography);
		textRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = textRepository.findAll(
			Specification.allOf(TextSpecification.fetchTopography(), TextSpecification.published()),
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
		assertThat(entityManager.find(TopographyEntity.class, 999)).isNull();
	}

	@Test
	void fetchSubjectResolvesTheAssociation() {
		final var subject = persistSubject(700, "Musik");
		persist(1, 4, "a", null).setSubject(subject);
		textRepository.flush();
		entityManager.clear();

		final var rows = textRepository.findAll(TextSpecification.fetchSubject(), Pageable.unpaged()).getContent();

		assertThat(rows).hasSize(1);
		assertThat(rows.getFirst().getSubject().getId()).isEqualTo(700);
		assertThat(rows.getFirst().getSubject().getDisplayName()).isEqualTo("Musik");
	}

	@Test
	void fetchSubjectLeavesTheAssociationNullWhenTheForeignKeyDangles() {
		persist(1, 4, "a", null);
		entityManager.createNativeQuery("UPDATE TEXT SET D_O_ID = 999 WHERE ID_ID = 1").executeUpdate();
		entityManager.clear();
		assertThat(entityManager.find(OcmEntity.class, 999)).isNull();

		final var row = textRepository.findOne(
			TextSpecification.fetchSubject().and(TextSpecification.hasId(1))).orElseThrow();

		assertThat(row.getSubject()).isNull();
	}

	@Test
	void matchesFindsSubstringInTitle() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(TextSpecification.matches("protokoll"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Handling rörande hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(TextSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesDoesNotSearchTheDocumentBody() {
		// XMLTEXT is excluded from the searchable columns — it holds zero bytes across all production rows, and a LIKE
		// over a longtext column cannot use an index.
		persist(1, 4, "Protokoll", null).setXmltext("hemligheten står i brödtexten");
		textRepository.flush();

		assertThat(findIds(TextSpecification.matches("hemligheten"))).isEmpty();
		assertThat(findIds(TextSpecification.matches("protokoll"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "PROTOKOLL", null);

		assertThat(findIds(TextSpecification.matches("protokoll"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 4, "Protokoll från Timrå", null);

		assertThat(findIds(TextSpecification.matches("protokoll sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Protokoll", "Upprättat i Sundsvall");
		persist(2, 4, "Protokoll", "Upprättat i Timrå");

		assertThat(findIds(TextSpecification.matches("protokoll sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% avskrift", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(TextSpecification.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(TextSpecification.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(TextSpecification.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(TextSpecification.matches(null))).containsExactly(1, 2);
		assertThat(findIds(TextSpecification.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null);

		assertThat(findIds(TextSpecification.matches("protokoll"))).isEmpty();
	}

	@Test
	void findAllByParametersHidesUnpublishedAndDeletedRows() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 0, "Protokoll opublicerat", null);
		persist(3, 4, "Protokoll raderat", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		persist(4, 4, "Storgatan", null);
		textRepository.flush();

		final var page = textRepository.findAllByParameters(TextParameters.create().withQuery("protokoll"), Pageable.unpaged());

		assertThat(page.getContent()).extracting(TextEntity::getId).containsExactly(1);
	}

	@Test
	void findAllByParametersWithoutAQueryReturnsEveryVisibleRow() {
		persist(1, 4, "a", null);
		persist(2, 0, "b", null);

		final var page = textRepository.findAllByParameters(TextParameters.create(), Pageable.unpaged());

		assertThat(page.getContent()).extracting(TextEntity::getId).containsExactly(1);
	}

	@Test
	void findVisibleByIdSkipsADeletedRowButKeepsAnUnpublishedOne() {
		persist(1, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		persist(2, 0, "unpublished", null);
		textRepository.flush();

		assertThat(textRepository.findVisibleById(1)).isEmpty();
		assertThat(textRepository.findVisibleById(2)).isPresent();
		assertThat(textRepository.findVisibleById(999)).isEmpty();
	}

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 0, "Protokoll från Sundsvall", null);
		persist(3, 4, "Protokoll från Sundsvall", null).setDeletedDate(LocalDate.of(2024, MARCH, 1));
		persist(4, 4, "Storgatan", null);
		textRepository.flush();

		final var specification = Specification.allOf(
			TextSpecification.notDeleted(),
			TextSpecification.published(),
			TextSpecification.matches("protokoll"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Protokoll från Sundsvall", null);
		persist(2, 4, "Protokoll från Timrå", null);
		persist(3, 0, "Protokoll från Härnösand", null);

		final var specification = Specification.allOf(
			TextSpecification.published(),
			TextSpecification.matches("protokoll"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = textRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
