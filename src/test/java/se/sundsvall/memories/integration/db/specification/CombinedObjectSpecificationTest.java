package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.GenderCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;
import se.sundsvall.memories.service.util.Pageables;

import static java.time.Month.JANUARY;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Exercises {@link CombinedObjectSpecification} against a real MariaDB instance (Testcontainers). Rows are set up in
 * the underlying tables and read back through the {@code VW_MEMORY_OBJECTS} view. Each test is rolled back.
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
		entityManager.createNativeQuery("DELETE FROM MANTAL").executeUpdate();
		photoRepository.flush();
	}

	/** The point of the tiebreak: consecutive pages must not overlap, and together cover every row exactly once. */
	@Test
	void pagingCoversEveryRowExactlyOnce() {
		persistPhoto(1, "A", null, "1900", null);
		persistPhoto(2, "B", null, "1900", null);
		persistPhoto(3, "C", null, "1900", null);

		final var parameters = CombinedObjectParameters.create();
		final var first = combinedObjectRepository.findAllByParameters(parameters, Pageables.of(parameters.withPage(1).withLimit(2), "objectKey"));
		final var second = combinedObjectRepository.findAllByParameters(parameters, Pageables.of(parameters.withPage(2).withLimit(2), "objectKey"));

		assertThat(keysOf(first)).containsExactly("foto-1", "foto-2");
		assertThat(keysOf(second)).containsExactly("foto-3");
	}

	private static List<String> keysOf(final Page<CombinedObjectEntity> page) {
		return page.getContent().stream()
			.map(CombinedObjectEntity::getObjectKey)
			.toList();
	}

	/** Only the object branches carry an originator, so this filter also leaves out the register types. */
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
		byLegalEntityId.setCreatorLegalEntityId(List.of(20));

		assertThat(findKeys(byPersonName)).containsExactly("foto-1");
		assertThat(findKeys(byLegalEntityName)).containsExactly("foto-2");
		assertThat(findKeys(byPersonId)).containsExactly("foto-1");
		assertThat(findKeys(byLegalEntityId)).containsExactly("foto-2");
	}

	/** The counters must agree with the search on the originator filters too. */
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

		final var fullName = CombinedObjectParameters.create();
		fullName.setCreator("Anton Nordin");

		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByType(parameters)).containsExactly(entry("Foto", 1L));
		// a full name is in neither name column on its own
		assertThat(findKeys(fullName)).containsExactly("foto-1");
		assertThat(countByType(fullName)).containsExactly(entry("Foto", 1L));
	}

	/** A deleted register record is not served by its own endpoint, so it must not select objects here either. */
	@Test
	void creatorFiltersSkipADeletedRegisterRecord() {
		final var person = PersonEntity.create().withPersonId(5).withFirstName("Anton").withLastName("Nordin")
			.withDeletedDate(LocalDate.of(2026, JANUARY, 1));
		entityManager.persist(person);
		entityManager.flush();

		persistPhoto(1, "Av personen", null, "1900", null);
		photoRepository.findById(1).ifPresent(photo -> photo.setCreatorPerson(person));
		photoRepository.flush();
		entityManager.clear();

		final var byName = CombinedObjectParameters.create();
		byName.setCreator("Nordin");
		final var byId = CombinedObjectParameters.create();
		byId.setCreatorPersonId(5);

		assertThat(findKeys(byName)).isEmpty();
		assertThat(findKeys(byId)).isEmpty();
		assertThat(countByType(byName)).isEmpty();
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

	/** The keys in the order the query returned them, unlike {@link #findKeys(CombinedObjectParameters)}, which sorts. */
	private List<String> rankedKeys(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.findAllByParameters(parameters, Pageable.unpaged()).getContent().stream()
			.map(CombinedObjectEntity::getObjectKey)
			.toList();
	}

	private PersonEntity persistPerson(final Integer id, final String firstName, final String lastName, final String comment) {
		final var person = PersonEntity.create()
			.withPersonId(id)
			.withOptions(PUBLISHED)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withComment(comment);
		entityManager.persist(person);
		entityManager.flush();
		return person;
	}

	/** Every word has to occur, in any order and not necessarily in the same column. */
	@Test
	void matchesRequiresEveryWordSomewhereInTheText() {
		persistPhoto(1, "Stadsvy", "Fotograf Nordin", "1920", null);
		persistPhoto(2, "Hamnen", "Timrå", "1930", null);

		assertThat(findKeys(CombinedObjectParameters.create().withQuery("Stadsvy Nordin"))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withQuery("Nordin Stadsvy"))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withQuery("Stadsvy Timrå"))).isEmpty();
	}

	/** A wildcard in the query is a character to search for, not a pattern. The counters escape it the same way. */
	@Test
	void matchesEscapesWildcards() {
		persistPhoto(1, "100% ull", null, "1920", null);
		persistPhoto(2, "Stadsvy", null, "1930", null);

		final var parameters = CombinedObjectParameters.create().withQuery("100%");

		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByType(parameters)).containsExactly(entry("Foto", 1L));
	}

	/** A search for a person's name puts the person first, not the photo whose comment mentions them. */
	@Test
	void relevancePutsANameHitAboveABodyHit() {
		persistPhoto(1, "Handelsbod vid Storgatan", "Anton Nordin utanför sin handelsbod", "1890", null);
		persistPerson(2, "Anton", "Nordin", null);
		entityManager.clear();

		assertThat(rankedKeys(CombinedObjectParameters.create().withQuery("Anton Nordin")))
			.containsExactly("person-2", "foto-1");
	}

	/** An exact name outranks a prefix, which outranks a name that merely contains the query. */
	@Test
	void relevancePutsAnExactNameFirst() {
		persistPhoto(1, "Handelsboden vid Berg", null, "1900", null);
		persistPhoto(2, "Bergström", null, "1900", null);
		persistPhoto(3, "Berg", null, "1900", null);

		assertThat(rankedKeys(CombinedObjectParameters.create().withQuery("Berg")))
			.containsExactly("foto-3", "foto-2", "foto-1");
	}

	/** {@code NAME_TEXT} carries the alternative name too, so a search for either ranks the company as a name hit. */
	@Test
	void relevanceRanksALegalEntityByItsAlternativeName() {
		final var legalEntity = LegalEntityEntity.create()
			.withLegalEntityId(20)
			.withOptions(PUBLISHED)
			.withName("Nödhjälpskommittén 1888-1889")
			.withAlternativeNames("Nödhjälpskommittén");
		entityManager.persist(legalEntity);
		entityManager.flush();
		persistPhoto(1, "Branden 1888", "Bilden skänktes av Nödhjälpskommittén", "1888", null);
		entityManager.clear();

		assertThat(rankedKeys(CombinedObjectParameters.create().withQuery("Nödhjälpskommittén")))
			.containsExactly("jurpers-20", "foto-1");
	}

	/** An explicit sort replaces relevance but still gets the tiebreak, so tied rows keep a stable order. */
	@Test
	void explicitSortReplacesRelevanceButNotTheTiebreak() {
		persistPhoto(2, "Nordin B", "Nordin", "1900", null);
		persistPhoto(1, "Nordin A", "Nordin", "1900", null);
		persistPhoto(3, "Nordin C", "Nordin", "1900", null);

		final var parameters = CombinedObjectParameters.create().withQuery("Nordin");
		parameters.setSortBy(List.of("year"));

		assertThat(rankedKeys(parameters)).containsExactly("foto-1", "foto-2", "foto-3");
	}

	/**
	 * {@code location} is not an attribute name: it sorts objects by their free-text place and persons by their birth
	 * parish.
	 */
	@Test
	void locationSortOrdersObjectsByPlaceAndPersonsByParish() {
		persistAudio(1, "Intervju", "1975", "Njurunda");
		persistAudio(2, "Intervju", "1975", "Alnö");
		// only a resolved place, so the sort has to fall back to the topography or the photo clumps with the empties
		persistPhoto(4, "Stadsvy", null, "1920", persistTopography(500, "Bergsjö"));
		entityManager.persist(PersonEntity.create()
			.withPersonId(3)
			.withOptions(PUBLISHED)
			.withFirstName("Anna")
			.withLastName("Berg")
			.withBirthParish("Indal"));
		entityManager.flush();
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create();
		parameters.setSortBy(List.of("location"));

		assertThat(rankedKeys(parameters)).containsExactly("ljud-2", "foto-4", "person-3", "ljud-1");
	}

	/** The gender filter reads the view's {@code KON}, so it also excludes every type that records none. */
	@Test
	void genderFilterSelectsOnlyRegisterRowsOfThatGender() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		persistPerson(3, "Anton", "Nordin", "man", null);
		persistCensusRecord(4, "Erik", "Holm", "man", "1889");
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withGender("MAN");

		assertThat(findKeys(parameters)).containsExactly("mantal-4", "person-3");
	}

	/**
	 * Selecting a gender narrows the list but not its own counters. Every other filter, the type selection included, does
	 * reach them, and rows recording no gender are not counted.
	 */
	@Test
	void countByGenderIgnoresTheGenderSelectionButNoOtherFilter() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		persistPerson(3, "Anton", "Nordin", "man", null);
		persistCensusRecord(4, "Erik", "Holm", "man", "1889");
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withGender("man");
		assertThat(findKeys(parameters)).containsExactly("mantal-4", "person-3");
		assertThat(countByGender(parameters)).containsExactly(entry("kvinna", 1L), entry("man", 2L));

		final var mantalOnly = CombinedObjectParameters.create().withObjectType(List.of("Mantal"));
		assertThat(countByGender(mantalOnly)).containsExactly(entry("man", 1L));
	}

	/** Census records are searchable as the object type {@code Mantal}, composed and dated like the other registers. */
	@Test
	void searchIncludesCensusRecordsAsMantal() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistCensusRecord(7, "Anton", "Nordin", "man", "1852");
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withQuery("Anton Nordin"))).containsExactly("mantal-7");
		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of("Mantal")))).containsExactly("mantal-7");
		assertThat(countByType(CombinedObjectParameters.create())).containsExactly(entry("Foto", 1L), entry("Mantal", 1L));
	}

	/** Several legal entity ids are alternatives, so a whole category can be filtered in one call. */
	@Test
	void creatorLegalEntityIdsAreAlternatives() {
		final var first = LegalEntityEntity.create().withLegalEntityId(20).withName("Nödhjälpskommittén");
		final var second = LegalEntityEntity.create().withLegalEntityId(21).withName("Rederiet");
		entityManager.persist(first);
		entityManager.persist(second);
		entityManager.flush();

		persistPhoto(1, "Av kommittén", null, "1900", null);
		persistPhoto(2, "Av rederiet", null, "1900", null);
		persistPhoto(3, "Utan upphovsman", null, "1900", null);
		photoRepository.findById(1).ifPresent(photo -> photo.setCreatorLegalEntity(first));
		photoRepository.findById(2).ifPresent(photo -> photo.setCreatorLegalEntity(second));
		photoRepository.flush();
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create();
		parameters.setCreatorLegalEntityId(List.of(20, 21));

		assertThat(findKeys(parameters)).containsExactly("foto-1", "foto-2");
	}

	private void persistPerson(final Integer id, final String firstName, final String lastName, final String gender, final String comment) {
		entityManager.persist(PersonEntity.create()
			.withPersonId(id)
			.withOptions(PUBLISHED)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withGender(gender)
			.withComment(comment));
		entityManager.flush();
	}

	private void persistCensusRecord(final Integer id, final String firstName, final String lastName, final String gender, final String birthYear) {
		entityManager.persist(CensusRecordEntity.create()
			.withId(id)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withGender(gender)
			.withBirthYear(birthYear));
		entityManager.flush();
	}

	/** Calls the gender counters the way the service does. */
	private Map<String, Long> countByGender(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.countByGender(parameters).stream()
			.collect(toMap(GenderCount::gender, GenderCount::total, (first, _) -> first, LinkedHashMap::new));
	}

	/** Spring Data runs the same specification to derive the count query, where an order is invalid. */
	@Test
	void relevanceOrderingDoesNotBreakTheCountQuery() {
		final var sundsvall = persistTopography(500, "Sundsvall");
		persistPhoto(1, "Nordin a", null, "1920", sundsvall);
		persistPhoto(2, "Nordin b", null, "1930", sundsvall);
		entityManager.clear();

		final var page = combinedObjectRepository.findAllByParameters(CombinedObjectParameters.create().withQuery("Nordin"), Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}

	/** Deletion sets DELETEDDATE but leaves the published bit set, so the view has to check both. */
	@Test
	void softDeletedRowsAreInvisibleToTheSearchAndTheCounters() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		photoRepository.saveAndFlush(PhotoEntity.create()
			.withId(2)
			.withOptions(PUBLISHED)
			.withObjectType("Foto")
			.withDocumentTitle("Stadsvy raderad")
			.withEarliest("1920")
			.withDeletedDate(LocalDate.of(2026, JANUARY, 1)));
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withQuery("Stadsvy");

		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByType(parameters)).containsExactly(entry("Foto", 1L));
	}

	/** The view concatenates title and comment, so a word that appears only in the comment still matches. */
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

	/** The view normalises an unreadable date to {@code NULL}, so a row without a year falls outside every range. */
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

	/** The types are alternatives, so picking a second one widens the result. An unknown type matches nothing. */
	@Test
	void objectTypeSelectsTheTypesToSearchIn() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistAudio(2, "Intervju", "1975", null);
		persistPerson(3, "Anton", "Nordin", null);
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of("Ljud")))).containsExactly("ljud-2");
		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of("Foto", "Person")))).containsExactly("foto-1", "person-3");
		assertThat(findKeys(CombinedObjectParameters.create())).containsExactly("foto-1", "ljud-2", "person-3");
		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of("Karta")))).isEmpty();
	}

	/** An empty selection is no selection: every type comes back, not the rows whose type is the empty string. */
	@Test
	void objectTypeIgnoresAnEmptyOrBlankSelectionAndTrimsTheRest() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistAudio(2, "Intervju", "1975", null);

		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of()))).containsExactly("foto-1", "ljud-2");
		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of(" ")))).containsExactly("foto-1", "ljud-2");
		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of(" Ljud ")))).containsExactly("ljud-2");
	}

	/** Selecting a type narrows the list but not the chips. Every other filter does reach them. */
	@Test
	void countByTypeIgnoresTheTypeSelectionButNoOtherFilter() {
		persistPhoto(1, "Sundsvall stadsvy", null, "1920", null);
		persistPhoto(2, "Sundsvall hamnen", null, "1930", null);
		persistAudio(3, "Sundsvall intervju", "1975", null);
		persistAudio(4, "Timrå intervju", "1975", null);

		final var parameters = CombinedObjectParameters.create().withQuery("sundsvall").withObjectType(List.of("Ljud"));

		assertThat(findKeys(parameters)).containsExactly("ljud-3");
		assertThat(countByType(parameters)).containsExactly(entry("Foto", 2L), entry("Ljud", 1L));
	}

	/** The counters and the search must select the same rows, or a chip contradicts the list it labels. */
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

	/** Every filter, on both paths, over rows that differ in exactly one respect each. */
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

	/** A blank parameter means "no filter" on both paths, and an untrimmed one is trimmed. */
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

	/** Calls the counters the way the service does. */
	private Map<String, Long> countByType(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.countByType(parameters).stream()
			.collect(toMap(TypeCount::objectType, TypeCount::total, (first, _) -> first, LinkedHashMap::new));
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
