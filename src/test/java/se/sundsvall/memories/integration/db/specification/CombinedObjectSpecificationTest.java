package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import se.sundsvall.memories.integration.db.CombinedObjectRepository.TypeCount;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static se.sundsvall.memories.service.util.StringUtil.trimToNull;

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
		entityManager.createNativeQuery("DELETE FROM PERSON").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM JURPERS").executeUpdate();
		photoRepository.flush();
	}

	/**
	 * Only the object branches of the view carry an originator, so filtering on one also leaves out the register types
	 * — a person is not created by anyone.
	 */
	@Test
	void matchesCreatorFindsObjectsByTheirOriginator() {
		final var person = PersonEntity.create().withPersonId(5).withFirstName("Anton").withLastName("Nordin");
		final var legalEntity = LegalEntityEntity.create().withLegalEntityId(20).withName("Nödhjälpskommittén 1888-1889").withAlternativeNames("Kommittén");
		entityManager.persist(person);
		entityManager.persist(legalEntity);
		entityManager.flush();

		persistPhoto(1, "Av personen", null, "1900", null);
		persistPhoto(2, "Av bolaget", null, "1900", null);
		photoRepository.findById(1).ifPresent(photo -> photo.setCreatorPerson(person));
		photoRepository.findById(2).ifPresent(photo -> photo.setCreatorLegalEntity(legalEntity));
		photoRepository.flush();
		entityManager.clear();

		final var byPersonName = CombinedObjectParameters.create();
		byPersonName.setCreator("Nordin");
		final var byLegalEntityName = CombinedObjectParameters.create();
		byLegalEntityName.setCreator("kommitté");
		final var byPersonId = CombinedObjectParameters.create();
		byPersonId.setCreatorPersonId(5);
		final var byLegalEntityId = CombinedObjectParameters.create();
		byLegalEntityId.setCreatorLegalEntityId(20);

		assertThat(findKeys(byPersonName)).containsExactly("foto-1");
		assertThat(findKeys(byLegalEntityName)).containsExactly("foto-2");
		assertThat(findKeys(byPersonId)).containsExactly("foto-1");
		assertThat(findKeys(byLegalEntityId)).containsExactly("foto-2");
	}

	/**
	 * The counters are a handwritten query, so they have to agree with the specifications on the originator filters as
	 * well — otherwise the per-type chips would contradict the result they label.
	 */
	@Test
	void countByTypeAgreesWithTheSearchOnTheOriginatorFilters() {
		final var person = PersonEntity.create().withPersonId(5).withFirstName("Anton").withLastName("Nordin");
		entityManager.persist(person);
		entityManager.flush();

		persistPhoto(1, "Av personen", null, "1900", null);
		persistPhoto(2, "Utan upphovsman", null, "1900", null);
		photoRepository.findById(1).ifPresent(photo -> photo.setCreatorPerson(person));
		photoRepository.flush();
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create();
		parameters.setCreator("Nordin");

		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByType(parameters)).containsExactly(entry("Foto", 1L));
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
	 * The counters are the one query left in handwritten SQL, so its filter is written twice: once as SQL and once as
	 * the specification the search uses. These tests are what keeps the two honest — a chip that disagrees with the
	 * list claims more hits than the list can show.
	 */
	@Test
	void countByTypeGroupsTheSameRowsTheSearchReturns() {
		persistPhoto(1, "Sundsvall stadsvy", null, "1920", null);
		persistPhoto(2, "Sundsvall hamnen", null, "1930", null);
		persistAudio(3, "Sundsvall intervju", "1975", null);
		persistAudio(4, "Timrå intervju", "1975", null);

		final var parameters = CombinedObjectParameters.create().withQuery("sundsvall");

		assertThat(countByType(parameters)).containsExactly(entry("Foto", 2L), entry("Ljud", 1L));
		assertThat(findKeys(parameters)).hasSize(3);
	}

	/**
	 * Every filter, on both paths, over rows that differ in exactly one respect each.
	 */
	@Test
	void countByTypeAgreesWithTheSearchOnEveryFilter() {
		final var sundsvall = persistTopography(500, "Sundsvall");
		persistPhoto(1, "Stadsvy", "Sundsvall", "1920", sundsvall);
		persistPhoto(2, "Stadsvy", "Sundsvall", "1990", sundsvall);
		persistPhoto(3, "Hamnen", "Timrå", "1920", null);
		persistAudio(4, "Stadsvy intervju", "1920", "Sundsvall");

		final var parameters = CombinedObjectParameters.create()
			.withQuery("stadsvy")
			.withLocation("sundsvall")
			.withYearFrom(1900)
			.withYearTo(1950);

		assertThat(findKeys(parameters)).containsExactly("foto-1", "ljud-4");
		assertThat(countByType(parameters)).containsExactly(entry("Foto", 1L), entry("Ljud", 1L));
	}

	/**
	 * A blank parameter means "no filter" on both paths. The specifications trim; the native query is handed trimmed
	 * values by the service, which is the seam this pins.
	 */
	@Test
	void countByTypeAgreesWithTheSearchOnBlankAndUntrimmedInput() {
		persistPhoto(1, "Sundsvall stadsvy", null, "1920", null);
		persistAudio(2, "Timrå intervju", "1975", null);

		final var blank = CombinedObjectParameters.create().withQuery("   ").withLocation("");
		assertThat(findKeys(blank)).containsExactly("foto-1", "ljud-2");
		assertThat(countByType(blank)).containsExactly(entry("Foto", 1L), entry("Ljud", 1L));

		final var untrimmed = CombinedObjectParameters.create().withQuery("  sundsvall  ");
		assertThat(findKeys(untrimmed)).containsExactly("foto-1");
		assertThat(countByType(untrimmed)).containsExactly(entry("Foto", 1L));
	}

	@Test
	void countByTypeIsEmptyWhenNothingMatches() {
		persistPhoto(1, "Stadsvy", null, "1920", null);

		assertThat(countByType(CombinedObjectParameters.create().withQuery("saknas"))).isEmpty();
	}

	/**
	 * Calls the counters the way the service does, so the trimming it applies is part of what these tests exercise.
	 */
	private Map<String, Long> countByType(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.countByType(
			trimToNull(parameters.getQuery()),
			parameters.getYearFrom(),
			parameters.getYearTo(),
			trimToNull(parameters.getLocation()),
			trimToNull(parameters.getCreator()),
			parameters.getCreatorPersonId(),
			parameters.getCreatorLegalEntityId()).stream()
			.collect(toMap(TypeCount::getObjectType, TypeCount::getTotal, (first, _) -> first, LinkedHashMap::new));
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
