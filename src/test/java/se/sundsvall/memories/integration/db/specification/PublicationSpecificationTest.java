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
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link PublicationSpecification} against a real MariaDB instance (Testcontainers), because the behaviour
 * under test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity
 * — is database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory
 * database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class PublicationSpecificationTest {

	@Autowired
	private PublicationRepository publicationRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		publicationRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		publicationRepository.flush();
	}

	private PublicationEntity persist(final Integer id, final Integer options, final String title, final String comment) {
		return publicationRepository.saveAndFlush(PublicationEntity.create()
			.withPublicationId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment));
	}

	private List<Integer> findIds(final Specification<PublicationEntity> specification) {
		return publicationRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(PublicationEntity::getPublicationId)
			.sorted()
			.toList();
	}

	private TopographyEntity persistTopography(final int id, final String name) {
		final var topography = TopographyEntity.create().withTId(id).withName(name);
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

		assertThat(findIds(PublicationSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null);
		persist(2, 2, "bit 2 only", null);

		assertThat(findIds(PublicationSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null);

		assertThat(findIds(PublicationSpecification.published())).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// notDeleted()
	// ---------------------------------------------------------------------------------------------

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null);
		persist(2, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		publicationRepository.flush();

		assertThat(findIds(PublicationSpecification.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		// PUBL has 4 such rows in production — deletion sets DELETEDDATE but leaves bit 4 set.
		persist(1, 4, "deleted but still published", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		publicationRepository.flush();

		assertThat(findIds(PublicationSpecification.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(PublicationSpecification.published(), PublicationSpecification.notDeleted()))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// hasId()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(PublicationSpecification.hasId(2))).containsExactly(2);
	}

	// ---------------------------------------------------------------------------------------------
	// fetchTopography()
	// ---------------------------------------------------------------------------------------------

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		publicationRepository.flush();
		entityManager.clear();

		final var publications = publicationRepository.findAll(PublicationSpecification.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(publications).hasSize(1);
		assertThat(publications.getFirst().getTopography().getTId()).isEqualTo(500);
		assertThat(publications.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null);
		danglingForeignKey(1);

		final var publication = publicationRepository.findOne(Specification.allOf(
			PublicationSpecification.fetchTopography(),
			PublicationSpecification.hasId(1),
			PublicationSpecification.notDeleted())).orElseThrow();

		assertThat(publication.getTopography()).isNull();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = persistTopography(500, "Sundsvall");
		persist(1, 4, "a", null).setTopography(topography);
		persist(2, 4, "b", null).setTopography(topography);
		publicationRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = publicationRepository.findAll(
			Specification.allOf(PublicationSpecification.fetchTopography(), PublicationSpecification.published()),
			Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}

	/**
	 * Points a publication's {@code P_T_ID} at a TOPOGRAFI row that does not exist. The legacy schema declares no
	 * foreign key constraints, so this state is representable — and without a fetch join it would produce a lazy proxy
	 * that throws {@code EntityNotFoundException} when the mapper reads the place name.
	 */
	private void danglingForeignKey(final int publicationId) {
		entityManager.createNativeQuery("UPDATE PUBL SET P_T_ID = 999 WHERE P_ID = :id")
			.setParameter("id", publicationId)
			.executeUpdate();
		entityManager.clear();

		// The association is only interesting if the raw column really points somewhere unresolvable.
		assertThat(entityManager.createNativeQuery("SELECT P_T_ID FROM PUBL WHERE P_ID = :id")
			.setParameter("id", publicationId)
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
		persist(1, 4, "Sundsvalls Tidning", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(PublicationSpecification.matches("tidning"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Artikel om hamnen");
		persist(2, 4, "Storgatan", "Inget av intresse");

		assertThat(findIds(PublicationSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesSearchesTheDocumentBody() {
		// Unlike TEXT, XMLTEXT is part of the searchable columns here — PUBL actually holds digitised text, and a word
		// that only appears in the body must still be findable.
		persist(1, 4, "Sundsvalls Tidning", null).setXmltext("notis om branden på Storgatan");
		persist(2, 4, "Annan tidning", null).setXmltext("inget av intresse");
		publicationRepository.flush();

		assertThat(findIds(PublicationSpecification.matches("branden"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "TIDNING", null);

		assertThat(findIds(PublicationSpecification.matches("tidning"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Tidning från Sundsvall", null);
		persist(2, 4, "Tidning från Timrå", null);

		assertThat(findIds(PublicationSpecification.matches("tidning sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Tidning", null).setXmltext("utgiven i Sundsvall");
		persist(2, 4, "Tidning", null).setXmltext("utgiven i Timrå");
		publicationRepository.flush();

		assertThat(findIds(PublicationSpecification.matches("tidning sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% återgivet", null);
		persist(2, 4, "Storgatan", null);

		assertThat(findIds(PublicationSpecification.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null);
		persist(2, 4, "filXnamn", null);

		assertThat(findIds(PublicationSpecification.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null);
		persist(2, 4, "Vilken tur", null);

		assertThat(findIds(PublicationSpecification.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null);
		persist(2, 4, "b", null);

		assertThat(findIds(PublicationSpecification.matches(null))).containsExactly(1, 2);
		assertThat(findIds(PublicationSpecification.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereEveryColumnIsNull() {
		persist(1, 4, null, null);

		assertThat(findIds(PublicationSpecification.matches("tidning"))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// Composition — the repository methods the service calls
	// ---------------------------------------------------------------------------------------------

	@Test
	void findAllByParametersHidesUnpublishedAndDeletedRows() {
		persist(1, 4, "Sundsvalls Tidning", null);
		persist(2, 0, "Tidning opublicerad", null);
		persist(3, 4, "Tidning raderad", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null);
		publicationRepository.flush();

		final var page = publicationRepository.findAllByParameters(PublicationParameters.create().withQuery("tidning"), Pageable.unpaged());

		assertThat(page.getContent()).extracting(PublicationEntity::getPublicationId).containsExactly(1);
	}

	@Test
	void findAllByParametersWithoutAQueryReturnsEveryVisibleRow() {
		persist(1, 4, "a", null);
		persist(2, 0, "b", null);

		final var page = publicationRepository.findAllByParameters(PublicationParameters.create(), Pageable.unpaged());

		assertThat(page.getContent()).extracting(PublicationEntity::getPublicationId).containsExactly(1);
	}

	@Test
	void findVisibleByIdSkipsADeletedRowButKeepsAnUnpublishedOne() {
		persist(1, 4, "deleted", null).setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(2, 0, "unpublished", null);
		publicationRepository.flush();

		assertThat(publicationRepository.findVisibleById(1)).isEmpty();
		assertThat(publicationRepository.findVisibleById(2)).isPresent();
		assertThat(publicationRepository.findVisibleById(999)).isEmpty();
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Tidning från Sundsvall", null);
		persist(2, 4, "Tidning från Timrå", null);
		persist(3, 0, "Tidning från Härnösand", null);

		final var specification = Specification.allOf(
			PublicationSpecification.published(),
			PublicationSpecification.matches("tidning"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = publicationRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
