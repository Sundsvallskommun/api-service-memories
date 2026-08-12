package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.integration.db.OcmRepository;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

/**
 * Exercises {@link PhotoSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — the {@code bitand} bitmask function, {@code LIKE} escaping and the collation-driven case insensitivity — is
 * database behaviour, not Java behaviour, and would be assumed rather than verified against an in-memory database.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class PhotoSpecificationTest {

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private TopographyRepository topographyRepository;

	@Autowired
	private OcmRepository ocmRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		photoRepository.deleteAll();
		topographyRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM FOTO_OCM").executeUpdate();
		ocmRepository.deleteAll();
		photoRepository.flush();
	}

	private PhotoEntity persist(final Integer id, final Integer options, final String title, final String comment, final String objectType) {
		return photoRepository.saveAndFlush(PhotoEntity.create()
			.withPhotoId(id)
			.withOptions(options)
			.withDocumentTitle(title)
			.withComment(comment)
			.withObjectType(objectType));
	}

	private List<Integer> findIds(final Specification<PhotoEntity> specification) {
		return photoRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(PhotoEntity::getPhotoId)
			.sorted()
			.toList();
	}

	// ---------------------------------------------------------------------------------------------
	// published()
	// ---------------------------------------------------------------------------------------------

	@Test
	void publishedMatchesRowsWithBitFourSet() {
		persist(1, 4, "published", null, "Foto");
		persist(2, 0, "unpublished", null, "Foto");

		assertThat(findIds(PhotoSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedMatchesWhenOtherBitsAreSetSimultaneously() {
		persist(1, 6, "bit 2 and bit 4", null, "Foto");
		persist(2, 2, "bit 2 only", null, "Foto");

		assertThat(findIds(PhotoSpecification.published())).containsExactly(1);
	}

	@Test
	void publishedExcludesRowsWithNullOptions() {
		persist(1, null, "no options", null, "Foto");

		assertThat(findIds(PhotoSpecification.published())).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// notDeleted()
	// ---------------------------------------------------------------------------------------------

	@Test
	void notDeletedExcludesRowsWithADeletedDate() {
		persist(1, 4, "kept", null, "Foto");
		persist(2, 4, "deleted", null, "Foto").setDeletedDate(LocalDate.of(2024, 3, 1));
		photoRepository.flush();

		assertThat(findIds(PhotoSpecification.notDeleted())).containsExactly(1);
	}

	@Test
	void notDeletedIsIndependentOfThePublishedBit() {
		// Deleting a row sets DELETEDDATE but leaves bit 4 set, which is why published() alone does not hide it.
		persist(1, 4, "deleted but still published", null, "Foto").setDeletedDate(LocalDate.of(2024, 3, 1));
		photoRepository.flush();

		assertThat(findIds(PhotoSpecification.published())).containsExactly(1);
		assertThat(findIds(Specification.allOf(PhotoSpecification.published(), PhotoSpecification.notDeleted()))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// hasId()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasIdMatchesTheSingleRow() {
		persist(1, 4, "a", null, "Foto");
		persist(2, 4, "b", null, "Foto");

		assertThat(findIds(PhotoSpecification.hasId(2))).containsExactly(2);
	}

	@Test
	void findOneByIdSkipsADeletedRow() {
		persist(1, 4, "deleted", null, "Foto").setDeletedDate(LocalDate.of(2024, 3, 1));
		photoRepository.flush();

		final var specification = Specification.allOf(
			PhotoSpecification.fetchTopography(),
			PhotoSpecification.hasId(1),
			PhotoSpecification.notDeleted());

		assertThat(photoRepository.findOne(specification)).isEmpty();
	}

	@Test
	void findOneByIdReturnsAnUnpublishedRow() {
		// Unpublished photos stay reachable by id — a planned administrative interface needs them.
		persist(1, 0, "unpublished", null, "Foto");

		final var specification = Specification.allOf(
			PhotoSpecification.fetchTopography(),
			PhotoSpecification.hasId(1),
			PhotoSpecification.notDeleted());

		assertThat(photoRepository.findOne(specification)).isPresent();
	}

	// ---------------------------------------------------------------------------------------------
	// hasObjectType()
	// ---------------------------------------------------------------------------------------------

	@Test
	void hasObjectTypeFiltersOnExactValue() {
		persist(1, 4, "a", null, "Foto");
		persist(2, 4, "b", null, "Föremål");

		assertThat(findIds(PhotoSpecification.hasObjectType("Föremål"))).containsExactly(2);
	}

	@Test
	void hasObjectTypeIsUnrestrictedWhenNull() {
		persist(1, 4, "a", null, "Foto");
		persist(2, 4, "b", null, "Föremål");

		assertThat(findIds(PhotoSpecification.hasObjectType(null))).containsExactly(1, 2);
	}

	// ---------------------------------------------------------------------------------------------
	// FOTO_OCM subjects
	// ---------------------------------------------------------------------------------------------

	@Test
	void subjectsAreReadThroughTheJunctionTableInIdOrder() {
		ocmRepository.saveAndFlush(OcmEntity.create().withId(20).withText("Musik"));
		ocmRepository.saveAndFlush(OcmEntity.create().withId(1).withText("Allmänt"));
		persist(1, 4, "a", null, "Foto");
		// Junction rows inserted with the higher OCM id first, so the assertion below really tests @OrderBy("id")
		// rather than insertion order.
		linkSubject(1, 1, 20);
		linkSubject(2, 1, 1);
		entityManager.clear();

		final var photo = photoRepository.findVisibleById(1).orElseThrow();

		assertThat(photo.getSubjects()).extracting(OcmEntity::getText).containsExactly("Allmänt", "Musik");
	}

	@Test
	void subjectsIgnoreAJunctionRowPointingAtAMissingOcmEntry() {
		ocmRepository.saveAndFlush(OcmEntity.create().withId(1).withText("Allmänt"));
		persist(1, 4, "a", null, "Foto");
		linkSubject(1, 1, 1);
		linkSubject(2, 1, 999);
		entityManager.clear();
		assertThat(ocmRepository.findById(999)).isEmpty();

		final var photo = photoRepository.findVisibleById(1).orElseThrow();

		// The junction row survives, but the missing OCM entry simply does not appear — the same outcome the old
		// lookup produced by filtering out unresolved ids.
		assertThat(photo.getSubjects()).extracting(OcmEntity::getText).containsExactly("Allmänt");
	}

	@Test
	void aPhotoWithoutJunctionRowsHasNoSubjects() {
		persist(1, 4, "a", null, "Foto");
		entityManager.clear();

		assertThat(photoRepository.findVisibleById(1).orElseThrow().getSubjects()).isEmpty();
	}

	private void linkSubject(final int junctionId, final int photoId, final int ocmId) {
		entityManager.createNativeQuery("INSERT INTO FOTO_OCM (ID, F_ID, O_ID) VALUES (:junctionId, :photoId, :ocmId)")
			.setParameter("junctionId", junctionId)
			.setParameter("photoId", photoId)
			.setParameter("ocmId", ocmId)
			.executeUpdate();
	}

	// ---------------------------------------------------------------------------------------------
	// matches()
	// ---------------------------------------------------------------------------------------------

	@Test
	void matchesFindsSubstringInTitle() {
		persist(1, 4, "Hamnen i Sundsvall", null, "Foto");
		persist(2, 4, "Storgatan", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesFindsSubstringInComment() {
		persist(1, 4, "Utan titel", "Taget vid hamnen", "Foto");
		persist(2, 4, "Storgatan", "Inget av intresse", "Foto");

		assertThat(findIds(PhotoSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesIsCaseInsensitiveViaCollation() {
		persist(1, 4, "HAMNEN", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("hamnen"))).containsExactly(1);
	}

	@Test
	void matchesRequiresEveryWordButNotAdjacency() {
		persist(1, 4, "Hamnen i Sundsvall", null, "Foto");
		persist(2, 4, "Hamnen i Timrå", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("hamnen sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesAllowsWordsToComeFromDifferentColumns() {
		persist(1, 4, "Hamnen", "Fotograferad i Sundsvall", "Foto");
		persist(2, 4, "Hamnen", "Fotograferad i Timrå", "Foto");

		assertThat(findIds(PhotoSpecification.matches("hamnen sundsvall"))).containsExactly(1);
	}

	@Test
	void matchesEscapesPercentWildcard() {
		persist(1, 4, "100% ull", null, "Foto");
		persist(2, 4, "Storgatan", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("%"))).containsExactly(1);
	}

	@Test
	void matchesEscapesUnderscoreWildcard() {
		persist(1, 4, "fil_namn", null, "Foto");
		persist(2, 4, "filXnamn", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("fil_namn"))).containsExactly(1);
	}

	@Test
	void matchesEscapesTheEscapeCharacterItself() {
		persist(1, 4, "Vilken tur!", null, "Foto");
		persist(2, 4, "Vilken tur", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("tur!"))).containsExactly(1);
	}

	@Test
	void matchesIsUnrestrictedWhenQueryIsNullOrBlank() {
		persist(1, 4, "a", null, "Foto");
		persist(2, 4, "b", null, "Foto");

		assertThat(findIds(PhotoSpecification.matches(null))).containsExactly(1, 2);
		assertThat(findIds(PhotoSpecification.matches("   "))).containsExactly(1, 2);
	}

	@Test
	void matchesIgnoresRowsWhereBothColumnsAreNull() {
		persist(1, 4, null, null, "Foto");

		assertThat(findIds(PhotoSpecification.matches("hamnen"))).isEmpty();
	}

	// ---------------------------------------------------------------------------------------------
	// Composition — the repository methods the service calls
	// ---------------------------------------------------------------------------------------------

	@Test
	void findAllByParametersHidesUnpublishedAndDeletedRows() {
		persist(1, 4, "Hamnen i Sundsvall", null, "Foto");
		persist(2, 0, "Hamnen opublicerad", null, "Foto");
		persist(3, 4, "Hamnen raderad", null, "Foto").setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(4, 4, "Storgatan", null, "Foto");
		photoRepository.flush();

		final var page = photoRepository.findAllByParameters(PhotoParameters.create().withQuery("hamnen"), Pageable.unpaged());

		assertThat(page.getContent()).extracting(PhotoEntity::getPhotoId).containsExactly(1);
	}

	@Test
	void findAllByParametersAppliesTheObjectTypeFilterAndIgnoresItWhenBlank() {
		persist(1, 4, "a", null, "Foto");
		persist(2, 4, "b", null, "Föremål");

		assertThat(photoRepository.findAllByParameters(PhotoParameters.create().withObjectType("Föremål"), Pageable.unpaged())
			.getContent()).extracting(PhotoEntity::getPhotoId).containsExactly(2);
		// A blank object type means "no filter", so the request parameter can be passed through untrimmed.
		assertThat(photoRepository.findAllByParameters(PhotoParameters.create().withObjectType("   "), Pageable.unpaged())
			.getContent()).extracting(PhotoEntity::getPhotoId).containsExactlyInAnyOrder(1, 2);
	}

	@Test
	void findVisibleByIdSkipsADeletedRowButKeepsAnUnpublishedOne() {
		persist(1, 4, "deleted", null, "Foto").setDeletedDate(LocalDate.of(2024, 3, 1));
		persist(2, 0, "unpublished", null, "Foto");
		photoRepository.flush();

		assertThat(photoRepository.findVisibleById(1)).isEmpty();
		assertThat(photoRepository.findVisibleById(2)).isPresent();
		assertThat(photoRepository.findVisibleById(999)).isEmpty();
	}

	@Test
	void allOfCombinesEveryFilter() {
		persist(1, 4, "Hamnen i Sundsvall", null, "Foto");
		persist(2, 4, "Hamnen i Sundsvall", null, "Föremål");
		persist(3, 0, "Hamnen i Sundsvall", null, "Foto");
		persist(4, 4, "Storgatan", null, "Foto");

		final var specification = Specification.allOf(
			PhotoSpecification.published(),
			PhotoSpecification.matches("hamnen"),
			PhotoSpecification.hasObjectType("Foto"));

		assertThat(findIds(specification)).containsExactly(1);
	}

	@Test
	void allOfAcceptsUnrestrictedFiltersWithoutNarrowingTheResult() {
		persist(1, 4, "Hamnen i Sundsvall", null, "Foto");
		persist(2, 0, "Storgatan", null, "Föremål");

		final var specification = Specification.allOf(
			PhotoSpecification.published(),
			PhotoSpecification.matches(null),
			PhotoSpecification.hasObjectType(null));

		assertThat(findIds(specification)).containsExactly(1);
	}

	// ---------------------------------------------------------------------------------------------
	// fetchTopography()
	// ---------------------------------------------------------------------------------------------

	@Test
	void fetchTopographyResolvesTheAssociation() {
		final var topography = topographyRepository.saveAndFlush(
			TopographyEntity.create().withTId(500).withName("Sundsvall"));
		persist(1, 4, "a", null, "Foto").setTopography(topography);
		photoRepository.flush();
		entityManager.clear();

		final var photos = photoRepository.findAll(PhotoSpecification.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(photos).hasSize(1);
		assertThat(photos.getFirst().getTopography().getTId()).isEqualTo(500);
		assertThat(photos.getFirst().getTopography().getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void fetchTopographyLeavesTheAssociationNullWhenTheForeignKeyDangles() {
		persist(1, 4, "a", null, "Foto");
		danglingForeignKey(1);

		final var photos = photoRepository.findAll(PhotoSpecification.fetchTopography(), Pageable.unpaged()).getContent();

		assertThat(photos).hasSize(1);
		assertThat(photos.getFirst().getTopography()).isNull();
	}

	@Test
	void findOneWithFetchToleratesADanglingForeignKey() {
		persist(1, 4, "a", null, "Foto");
		danglingForeignKey(1);

		final var photo = photoRepository.findOne(Specification.allOf(
			PhotoSpecification.fetchTopography(),
			PhotoSpecification.hasId(1),
			PhotoSpecification.notDeleted())).orElseThrow();

		assertThat(photo.getTopography()).isNull();
	}

	@Test
	void findByIdWithoutFetchFailsOnADanglingForeignKey() {
		persist(1, 4, "a", null, "Foto");
		danglingForeignKey(1);

		// Documents why reads by id go through a specification with fetchTopography() rather than findById: the plain
		// lookup hands back a lazy proxy, which blows up the moment the mapper asks for the place name.
		final var photo = photoRepository.findById(1).orElseThrow();

		assertThatThrownBy(() -> photo.getTopography().getDisplayName())
			.isInstanceOf(EntityNotFoundException.class);
	}

	/**
	 * Points a photo's {@code F_T_ID} at a TOPOGRAFI row that does not exist. The legacy schema declares no foreign key
	 * constraints, so this state is representable — and without a fetch join it would produce a lazy proxy that throws
	 * {@code EntityNotFoundException} when the mapper reads the place name.
	 */
	private void danglingForeignKey(final int photoId) {
		entityManager.createNativeQuery("UPDATE FOTO SET F_T_ID = 999 WHERE F_ID = :id")
			.setParameter("id", photoId)
			.executeUpdate();
		entityManager.clear();

		// The association is only interesting if the raw column really points somewhere unresolvable.
		assertThat(entityManager.createNativeQuery("SELECT F_T_ID FROM FOTO WHERE F_ID = :id")
			.setParameter("id", photoId)
			.getSingleResult())
			.asInstanceOf(LONG)
			.isEqualTo(999L);
		assertThat(topographyRepository.findById(999)).isEmpty();
	}

	@Test
	void fetchTopographyDoesNotBreakPagingOrCounting() {
		final var topography = topographyRepository.saveAndFlush(
			TopographyEntity.create().withTId(500).withName("Sundsvall"));
		persist(1, 4, "a", null, "Foto").setTopography(topography);
		persist(2, 4, "b", null, "Foto").setTopography(topography);
		photoRepository.flush();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = photoRepository.findAll(
			Specification.allOf(PhotoSpecification.fetchTopography(), PhotoSpecification.published()),
			Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}

	@Test
	void countQuerySurvivesTheSameSpecification() {
		persist(1, 4, "Hamnen i Sundsvall", null, "Foto");
		persist(2, 4, "Hamnen i Timrå", null, "Foto");
		persist(3, 0, "Hamnen i Härnösand", null, "Foto");

		final var specification = Specification.allOf(
			PhotoSpecification.published(),
			PhotoSpecification.matches("hamnen"));

		// Spring Data reuses the specification for the count projection — a page request exercises both.
		final var page = photoRepository.findAll(specification, Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
