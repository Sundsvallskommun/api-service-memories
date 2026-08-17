package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Exercises {@link CombinedObjectSpecification} against a real MariaDB instance (Testcontainers). The subject is the
 * {@code VW_MEMORY_OBJECTS} view, so the rows are set up in the underlying tables and read back through the view —
 * which
 * also pins that the view still projects what the specification expects.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class CombinedObjectSpecificationTest {

	private static final Integer PUBLISHED = 4;

	@Autowired
	private CombinedObjectRepository combinedObjectRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private AudioRepository audioRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		photoRepository.deleteAll();
		audioRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		photoRepository.flush();
	}

	private void persistPhoto(final Integer id, final String title, final String comment, final String earliest, final TopographyEntity topography) {
		photoRepository.saveAndFlush(PhotoEntity.create()
			.withId(id)
			.withOptions(PUBLISHED)
			.withObjectType("Foto")
			.withDocumentTitle(title)
			.withComment(comment)
			.withEarliest(earliest)
			.withTopography(topography));
	}

	private void persistAudio(final Integer id, final String title, final String date, final String locationText) {
		audioRepository.saveAndFlush(AudioEntity.create()
			.withId(id)
			.withOptions(PUBLISHED)
			.withDocumentTitle(title)
			.withDate(date)
			.withLocationText(locationText));
	}

	private TopographyEntity persistTopography(final int id, final String name) {
		final var topography = TopographyEntity.create().withId(id).withName(name);
		entityManager.persist(topography);
		entityManager.flush();
		return topography;
	}

	private List<String> findKeys(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.findAllByParameters(parameters, Pageable.unpaged()).getContent().stream()
			.map(CombinedObjectEntity::getObjectKey)
			.sorted()
			.toList();
	}

	/**
	 * The view concatenates title and comment, so a word that appears only in the comment still matches.
	 */
	@Test
	void matchesSearchesTitleAndComment() {
		persistPhoto(1, "Stadsvy", "Fotograf okänd", "1920", null);
		persistPhoto(2, "Hamnen", "Timrå", "1930", null);

		assertThat(findKeys(CombinedObjectParameters.create().withQuery("stadsvy"))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withQuery("fotograf"))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withQuery("   "))).containsExactly("foto-1", "foto-2");
	}

	@Test
	void matchesLocationFindsThePlaceThroughTheAssociationOrTheFreeText() {
		persistPhoto(1, "Stadsvy", null, "1920", persistTopography(500, "Sundsvall"));
		persistAudio(2, "Intervju", "1975", "Alnö");

		assertThat(findKeys(CombinedObjectParameters.create().withLocation("sundsvall"))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withLocation("alnö"))).containsExactly("ljud-2");
	}

	/**
	 * The view has already normalised an unreadable date to {@code NULL}, so a row without a year falls outside every
	 * range rather than satisfying an upper bound as year zero.
	 */
	@Test
	void yearFiltersKeepRowsInsideTheRangeAndExcludeUndatedOnes() {
		persistPhoto(1, "a", null, "1920", null);
		persistPhoto(2, "b", null, "1980", null);
		persistPhoto(3, "c", null, "okänt", null);

		assertThat(findKeys(CombinedObjectParameters.create().withYearFrom(1950))).containsExactly("foto-2");
		assertThat(findKeys(CombinedObjectParameters.create().withYearTo(1950))).containsExactly("foto-1");
	}

	@Test
	void searchSpansEveryObjectType() {
		persistPhoto(1, "Sundsvall stadsvy", null, "1920", null);
		persistAudio(2, "Sundsvall intervju", "1975", null);

		assertThat(findKeys(CombinedObjectParameters.create().withQuery("sundsvall"))).containsExactly("foto-1", "ljud-2");
	}

	/**
	 * The counters must see exactly the filters the search sees, otherwise a chip claims more hits than the list can
	 * show.
	 */
	@Test
	void countByTypeGroupsTheSameRowsTheSearchReturns() {
		persistPhoto(1, "Sundsvall stadsvy", null, "1920", null);
		persistPhoto(2, "Sundsvall hamnen", null, "1930", null);
		persistAudio(3, "Sundsvall intervju", "1975", null);
		persistAudio(4, "Timrå intervju", "1975", null);

		final var parameters = CombinedObjectParameters.create().withQuery("sundsvall");

		assertThat(combinedObjectRepository.countByType(parameters))
			.containsExactly(entry("Foto", 2L), entry("Ljud", 1L));
		assertThat(findKeys(parameters)).hasSize(3);
	}

	@Test
	void countByTypeIsEmptyWhenNothingMatches() {
		persistPhoto(1, "Stadsvy", null, "1920", null);

		assertThat(combinedObjectRepository.countByType(CombinedObjectParameters.create().withQuery("saknas"))).isEmpty();
	}

	@Test
	void findAllByParametersResolvesTopographyAndSurvivesPaging() {
		final var sundsvall = persistTopography(500, "Sundsvall");
		persistPhoto(1, "a", null, "1920", sundsvall);
		persistPhoto(2, "b", null, "1930", sundsvall);
		entityManager.clear();

		// The fetch join is invalid in the count projection, so the specification must skip it there.
		final var page = combinedObjectRepository.findAllByParameters(CombinedObjectParameters.create(), Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).singleElement()
			.extracting(object -> object.getTopography().getDisplayName())
			.isEqualTo("Sundsvall");
	}
}
