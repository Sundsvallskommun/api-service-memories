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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.CategoryCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.GenderCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.InstitutionCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TopographyCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;
import se.sundsvall.memories.service.util.Pageables;

import static java.time.Month.JANUARY;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.groups.Tuple.tuple;

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

	@Autowired
	private PublicationRepository publicationRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		photoRepository.deleteAll();
		audioRepository.deleteAll();
		publicationRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM PERSON").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM JURPERS").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM KATEGORI").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM MANTAL").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM TBL_NODEATTRIBUTES").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM TBL_NODES").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM INSTITUTION").executeUpdate();
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

	private InstitutionEntity persistInstitution(final int id, final String name) {
		final var institution = InstitutionEntity.create().withId(id).withName(name);
		entityManager.persist(institution);
		entityManager.flush();
		return institution;
	}

	/**
	 * The bug Lars reported: searching "Drunkningsolycka" returned one hit where the source holds four, because three
	 * of them carry the word only inside the digitised page. The combined search reads SEARCH_TEXT, and the PUBL branch
	 * of the view left XMLTEXT out while the per-type /publications search read it — so the two disagreed on the same
	 * data.
	 */
	@Test
	void searchReachesAPublicationBodyAndNotOnlyItsTitle() {
		persistPublication(1, "Drunkningsolycka i Selångersån", null, "en notis om samma olycka");
		persistPublication(2, "Katastrofartade översvämningar", null, "Hur drunkningsolyckan tillgått är oklart");
		persistPublication(3, "Helt orelaterad titel", null, "ingenting av intresse här");

		assertThat(findKeys(CombinedObjectParameters.create().withQuery("Drunkningsolycka")))
			.containsExactly("publ-1", "publ-2");
	}

	private void persistPublication(final Integer id, final String title, final String comment, final String body) {
		publicationRepository.saveAndFlush(PublicationEntity.create()
			.withId(id)
			.withOptions(PUBLISHED)
			.withDocumentTitle(title)
			.withComment(comment)
			.withXmltext(body));
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

	/**
	 * A node selection keeps the objects created in it and nothing else: a register row is not placed in the tree and
	 * never matches, and the ids are alternatives.
	 */
	@Test
	void nodeFilterKeepsTheObjectsInTheNodesAndNoRegisterRow() {
		photoRepository.saveAndFlush(PhotoEntity.create().withId(1).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I noden").withNodeId(100));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(2).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I en annan").withNodeId(200));
		persistAudio(3, "Utan nod", "1975", null);
		persistPerson(4, "Anton", "Nordin", null);

		assertThat(findKeys(CombinedObjectParameters.create().withNodeId(List.of(100)))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withNodeId(List.of(100, 200)))).containsExactly("foto-1", "foto-2");
		assertThat(findKeys(CombinedObjectParameters.create().withNodeId(List.of(999)))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withNodeId(List.of()))).hasSize(4);
		assertThat(combinedObjectRepository.findAllByParameters(CombinedObjectParameters.create().withNodeId(List.of(100)), Pageable.unpaged()).getContent())
			.extracting(CombinedObjectEntity::getNodeId).containsExactly(100);
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

	/**
	 * A row without a place sorts last whichever way the list runs, so a list of places opens on the places. The
	 * database would put the empty values first in ascending order on its own.
	 */
	@Test
	void locationSortPutsRowsWithoutAPlaceLastInEitherDirection() {
		persistAudio(1, "Intervju", "1975", "Njurunda");
		persistAudio(2, "Intervju", "1975", null);
		persistAudio(3, "Intervju", "1975", "Alnö");

		final var ascending = CombinedObjectParameters.create();
		ascending.setSortBy(List.of("location"));
		final var descending = CombinedObjectParameters.create();
		descending.setSortBy(List.of("location"));
		descending.setSortDirection(Sort.Direction.DESC);

		assertThat(rankedKeys(ascending)).containsExactly("ljud-3", "ljud-1", "ljud-2");
		assertThat(rankedKeys(descending)).containsExactly("ljud-1", "ljud-3", "ljud-2");
	}

	/**
	 * Selecting a place narrows the list but not its own counters. Every other filter, the substring location filter
	 * included, does reach them, and only the rows placed in a listable topography are counted: a row with no place, or
	 * with one the dropdown could not offer, is not a chip, though the row itself still matches.
	 */
	@Test
	void countByTopographyIgnoresTheTopographySelectionButNoOtherFilter() {
		final var sundsvall = persistTopography(500, "Sundsvall");
		final var timra = persistTopography(501, "Timrå");
		final var blank = persistTopography(502, "  ");
		persistPhoto(1, "Stadsvy", null, "1920", sundsvall);
		persistPhoto(2, "Hamnen", null, "1930", timra);
		persistPhoto(3, "Sågverket", null, "1930", timra);
		persistPhoto(4, "Utan plats", null, "1930", null);
		persistPhoto(5, "Blank plats", null, "1930", blank);
		persistPerson(6, "Anton", "Nordin", null);

		final var parameters = CombinedObjectParameters.create().withTopographyId(List.of(500));
		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByTopography(parameters)).containsExactly(entry(500, 1L), entry(501, 2L));

		final var byQuery = CombinedObjectParameters.create().withQuery("hamnen").withTopographyId(List.of(500));
		assertThat(findKeys(byQuery)).isEmpty();
		assertThat(countByTopography(byQuery)).containsExactly(entry(501, 1L));

		assertThat(countByTopography(CombinedObjectParameters.create().withLocation("timr"))).containsExactly(entry(501, 2L));
		assertThat(countByTopography(CombinedObjectParameters.create().withObjectType(List.of("Person")))).isEmpty();
		assertThat(combinedObjectRepository.countByTopography(CombinedObjectParameters.create()))
			.extracting(TopographyCount::topographyId, TopographyCount::name, TopographyCount::place)
			.containsExactly(tuple(500, "Sundsvall", null), tuple(501, "Timrå", null));
	}

	/** The type counters see the topography selection the way they see every other selection. */
	@Test
	void countByTypeAppliesTheTopographySelection() {
		final var sundsvall = persistTopography(500, "Sundsvall");
		persistPhoto(1, "Stadsvy", null, "1920", sundsvall);
		persistAudio(2, "Intervju", "1975", null);

		assertThat(countByType(CombinedObjectParameters.create().withTopographyId(List.of(500)))).containsExactly(entry("Foto", 1L));
	}

	/**
	 * The gender filter narrows the types that record one and leaves the rest of the search alone — the photo is not a
	 * man, but neither is it a woman, so excluding it would answer a question it was never asked.
	 */
	@Test
	void genderFilterNarrowsTheRegistersAndLeavesEveryOtherTypeUntouched() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		persistPerson(3, "Anton", "Nordin", "man", null);
		persistCensusRecord(4, "Erik", "Holm", "man", "1889");
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withGender("MAN");

		assertThat(findKeys(parameters)).containsExactly("foto-1", "mantal-1845-4", "person-3");
	}

	/**
	 * The registers are where a gender selection bites, so restricting the search to a type that records none returns
	 * that type whole rather than nothing — what the filter promises when it sits inside the person facet.
	 */
	@Test
	void genderFilterMatchesEveryRowOfATypeRecordingNoGender() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withObjectType(List.of("Foto")).withGender("Man");

		assertThat(findKeys(parameters)).containsExactly("foto-1");
	}

	/**
	 * A label the archive knows no gender by matches no register row, but it still says nothing about the types that
	 * record none, so those stay in the result.
	 */
	@Test
	void genderFilterOfAnUnknownLabelKeepsOnlyTheTypesRecordingNoGender() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		persistCensusRecord(3, "Erik", "Holm", "man", "1889");
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withGender("Hen"))).containsExactly("foto-1");
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
		assertThat(findKeys(parameters)).containsExactly("foto-1", "mantal-1845-4", "person-3");
		assertThat(countByGender(parameters)).containsExactly(entry("Kvinna", 1L), entry("Man", 2L));

		final var mantalOnly = CombinedObjectParameters.create().withObjectType(List.of("Mantal"));
		assertThat(countByGender(mantalOnly)).containsExactly(entry("Man", 1L));
	}

	/**
	 * Every register row is counted under one of the three labels: a stray, blank or missing value is a gender the
	 * archive does not know, which is what Okänt says, so the counters add up to the register rows the search matched.
	 */
	@Test
	void countByGenderGathersEveryUnreadableValueUnderUnknown() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		persistPerson(3, "Brita", "Piga", "1830-06-12", null);
		persistPerson(4, "Okänd", "Person", "okänt", null);
		persistPerson(5, "Tom", "Person", null, null);
		persistCensusRecord(6, "Erik", "Holm", "0", "1889");
		entityManager.clear();

		assertThat(countByGender(CombinedObjectParameters.create())).containsExactly(entry("Kvinna", 1L), entry("Okänt", 4L));
	}

	/**
	 * The type counters leave out the type selection, not the gender one — but a gender selection no longer empties the
	 * types that record none, so every chip keeps reporting what selecting it would return.
	 */
	@Test
	void countByTypeCoversEveryTypeWithAGenderSelected() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistPerson(2, "Anna", "Berg", "kvinna", null);
		persistPerson(3, "Anton", "Nordin", "man", null);
		persistCensusRecord(4, "Erik", "Holm", "man", "1889");
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withGender("Man");

		assertThat(countByType(parameters)).containsExactly(entry("Foto", 1L), entry("Mantal", 1L), entry("Person", 1L));
	}

	/**
	 * The institution counters ignore their own selection but no other filter, and an institution without a name to show
	 * is not a chip, though the row itself still matches.
	 */
	@Test
	void countByInstitutionIgnoresTheInstitutionSelectionButNoOtherFilter() {
		persistInstitutionTree();
		final var blank = persistInstitution(5, "  ");
		photoRepository.saveAndFlush(PhotoEntity.create().withId(7).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("Blank").withInstitution(blank));

		final var parameters = CombinedObjectParameters.create().withInstitutionId(List.of(43));
		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByInstitution(parameters)).containsOnly(entry(3, 2L), entry(43, 1L));

		assertThat(countByInstitution(CombinedObjectParameters.create().withQuery("hamnen"))).containsOnly(entry(3, 1L));
		assertThat(countByInstitution(CombinedObjectParameters.create().withObjectType(List.of("Person")))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(5)))).containsExactly("foto-7");
		assertThat(combinedObjectRepository.countByInstitution(CombinedObjectParameters.create()))
			.extracting(InstitutionCount::institutionId, InstitutionCount::name, InstitutionCount::total)
			.containsExactlyInAnyOrder(tuple(3, "Föreningsarkivet Västernorrland", 2L), tuple(43, "Sundsvalls museum", 1L));
	}

	@Test
	void countByTypeAppliesTheInstitutionSelection() {
		persistInstitutionTree();

		assertThat(countByType(CombinedObjectParameters.create().withInstitutionId(List.of(3)))).containsOnly(entry("Foto", 1L), entry("Ljud", 1L));
	}

	@Test
	void institutionFilterReadsThePhotosOwnInstitutionElseItsArchives() {
		persistInstitutionTree();

		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(43)))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(3)))).containsExactly("foto-2", "ljud-3");
		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(3, 43)))).containsExactly("foto-1", "foto-2", "ljud-3");
		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(2)))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(1)))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of()))).hasSize(6);
	}

	/**
	 * Every link of the walk counts: the archive is found from the archive itself and from three and four levels down,
	 * but not from five, and an object on a deleted node gets no institution though it stays in the results. One and two
	 * levels down are covered by institutionFilterReadsThePhotosOwnInstitutionElseItsArchives.
	 */
	@Test
	void institutionFilterReachesTheArchiveFromEveryLevel() {
		persistInstitution(3, "Föreningsarkivet Västernorrland");
		persistNode(100, 0, 3, false);
		persistNode(110, 100, null, false);
		persistNode(111, 110, null, false);
		persistNode(112, 111, null, false);
		persistNode(113, 112, null, false);
		persistNode(114, 113, null, false);
		persistNode(120, 100, null, true);
		audioRepository.saveAndFlush(AudioEntity.create().withId(1).withOptions(PUBLISHED).withDocumentTitle("archive").withNodeId(100));
		audioRepository.saveAndFlush(AudioEntity.create().withId(2).withOptions(PUBLISHED).withDocumentTitle("3 nodes deep").withNodeId(112));
		audioRepository.saveAndFlush(AudioEntity.create().withId(3).withOptions(PUBLISHED).withDocumentTitle("4 nodes deep").withNodeId(113));
		audioRepository.saveAndFlush(AudioEntity.create().withId(4).withOptions(PUBLISHED).withDocumentTitle("5 nodes deep").withNodeId(114));
		audioRepository.saveAndFlush(AudioEntity.create().withId(5).withOptions(PUBLISHED).withDocumentTitle("deleted node").withNodeId(120));

		assertThat(findKeys(CombinedObjectParameters.create().withInstitutionId(List.of(3)))).containsExactly("ljud-1", "ljud-2", "ljud-3");
		assertThat(findKeys(CombinedObjectParameters.create())).containsExactly("ljud-1", "ljud-2", "ljud-3", "ljud-4", "ljud-5");
	}

	/** Census records are searchable as the object type {@code Mantal}, composed and dated like the other registers. */
	@Test
	void searchIncludesCensusRecordsAsMantal() {
		persistPhoto(1, "Stadsvy", null, "1920", null);
		persistCensusRecord(7, "Anton", "Nordin", "man", "1852");
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withQuery("Anton Nordin"))).containsExactly("mantal-1845-7");
		assertThat(findKeys(CombinedObjectParameters.create().withObjectType(List.of("Mantal")))).containsExactly("mantal-1845-7");
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
			.withSource("1845")
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

	/**
	 * A category is a property of the originator, so the filter keeps the objects whose originator is in it: several
	 * are alternatives, the register rows are excluded like {@code creator} excludes them, and the sentinel category
	 * every legal entity defaults to is never a match — naming it alone matches nothing, not every uncategorised object.
	 */
	@Test
	void categoryFilterFindsObjectsByTheirOriginatorsCategory() {
		persistCategorisedOriginators();
		persistPerson(9, "Anton", "Nordin", null);
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withCategoryId(List.of(5)))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withCategoryId(List.of(2, 5)))).containsExactly("foto-1", "foto-2");
		assertThat(findKeys(CombinedObjectParameters.create().withCategoryId(List.of(1)))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withCategoryId(List.of(1, 5)))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withCategoryId(List.of()))).containsExactly("foto-1", "foto-2", "foto-3", "foto-4", "person-9");
	}

	/** A deleted legal entity is not served by its own endpoint, so its category must not select objects either. */
	@Test
	void categoryFilterSkipsADeletedLegalEntity() {
		persistCategorisedOriginators();
		entityManager.createNativeQuery("UPDATE JURPERS SET DELETEDDATE = '2026-01-01' WHERE J_ID = 20").executeUpdate();
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withCategoryId(List.of(5));

		assertThat(findKeys(parameters)).isEmpty();
		assertThat(countByCategory(CombinedObjectParameters.create())).containsExactly(entry("Aktiebolag", 1L));
	}

	/**
	 * Selecting a category narrows the list but not its own counters. Every other filter, the type selection included,
	 * does reach them, and only the rows with a categorised originator are counted — never the sentinel category.
	 */
	@Test
	void countByCategoryIgnoresTheCategorySelectionButNoOtherFilter() {
		persistCategorisedOriginators();
		persistPerson(9, "Anton", "Nordin", null);
		entityManager.clear();

		final var parameters = CombinedObjectParameters.create().withCategoryId(List.of(5));
		assertThat(findKeys(parameters)).containsExactly("foto-1");
		assertThat(countByCategory(parameters)).containsExactly(entry("Aktiebolag", 1L), entry("Kommitté", 1L));

		final var byQuery = CombinedObjectParameters.create().withQuery("kommittén").withCategoryId(List.of(2));
		assertThat(findKeys(byQuery)).isEmpty();
		assertThat(countByCategory(byQuery)).containsExactly(entry("Kommitté", 1L));

		final var personsOnly = CombinedObjectParameters.create().withObjectType(List.of("Person"));
		assertThat(countByCategory(personsOnly)).isEmpty();
	}

	/**
	 * The counters group on the same join the originator filters match through, so a name filter on the legal entity
	 * and a category count in one query agree — and the name filter's sentinel guard does not empty the count.
	 */
	@Test
	void countByCategoryAgreesWithTheSearchOnTheOriginatorFilters() {
		persistCategorisedOriginators();
		entityManager.clear();

		final var byName = CombinedObjectParameters.create();
		byName.setCreator("rederi");
		assertThat(findKeys(byName)).containsExactly("foto-2");
		assertThat(countByCategory(byName)).containsExactly(entry("Aktiebolag", 1L));
		assertThat(countByType(byName)).containsExactly(entry("Foto", 1L));

		final var byId = CombinedObjectParameters.create();
		byId.setCreatorLegalEntityId(List.of(20, 21));
		assertThat(countByCategory(byId)).containsExactly(entry("Aktiebolag", 1L), entry("Kommitté", 1L));
	}

	/**
	 * A chip is labelled from the KATEGORI row, so only a row that can label one may become a chip — the same rows
	 * /categories offers. The foreign key guard does not reach that far on its own: it is read straight off JURPERS,
	 * so a KAT_ID pointing at no row would group as (null, null), and a blank-named category would become a chip the
	 * dropdown never lists. Nothing in the schema declares a foreign key, so neither case is hypothetical. The objects
	 * themselves still match; it is only the chip that is left out.
	 */
	@Test
	void countByCategoryLeavesOutTheCategoriesTheDropdownCannotOffer() {
		persistCategorisedOriginators();
		final var blank = persistCategory(7, "   ");
		final var blankNamed = LegalEntityEntity.create().withLegalEntityId(22).withName("Blanktecknad").withCategory(blank);
		final var orphaned = LegalEntityEntity.create().withLegalEntityId(23).withName("Utan kategorirad");
		entityManager.persist(blankNamed);
		entityManager.persist(orphaned);
		entityManager.flush();

		persistPhoto(5, "Av den blanktecknade", null, "1900", null);
		persistPhoto(6, "Av den utan kategorirad", null, "1900", null);
		photoRepository.findById(5).ifPresent(photo -> photo.setCreatorLegalEntity(blankNamed));
		photoRepository.findById(6).ifPresent(photo -> photo.setCreatorLegalEntity(orphaned));
		photoRepository.flush();
		entityManager.createNativeQuery("UPDATE JURPERS SET KAT_ID = 99 WHERE J_ID = 23").executeUpdate();
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create())).contains("foto-5", "foto-6");
		assertThat(combinedObjectRepository.countByCategory(CombinedObjectParameters.create()))
			.containsExactly(new CategoryCount(2, "Aktiebolag", 1L), new CategoryCount(5, "Kommitté", 1L));
	}

	/** The chips carry the category's name, so the client need not look it up, and are ordered by it. */
	@Test
	void countByCategoryNamesEachCategory() {
		persistCategorisedOriginators();
		entityManager.clear();

		assertThat(combinedObjectRepository.countByCategory(CombinedObjectParameters.create()))
			.containsExactly(new CategoryCount(2, "Aktiebolag", 1L), new CategoryCount(5, "Kommitté", 1L));
	}

	/**
	 * The exact counterpart of the substring location filter: only the rows placed in one of the topographies, which
	 * are alternatives. A row that merely names the place in its free text is not placed there.
	 */
	@Test
	void topographyFilterFindsTheRowsPlacedInOneOfThePlaces() {
		final var sundsvall = persistTopography(500, "Sundsvall");
		final var timra = persistTopography(501, "Timrå");
		persistPhoto(1, "Stadsvy", null, "1920", sundsvall);
		persistPhoto(2, "Sågverket", null, "1930", timra);
		persistAudio(3, "Intervju", "1975", "Sundsvall");
		persistPerson(4, "Anna", "Berg", null);
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withTopographyId(List.of(500)))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withTopographyId(List.of(500, 501)))).containsExactly("foto-1", "foto-2");
		assertThat(findKeys(CombinedObjectParameters.create().withTopographyId(List.of(999)))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withTopographyId(List.of()))).containsExactly("foto-1", "foto-2", "ljud-3", "person-4");
		assertThat(countByType(CombinedObjectParameters.create().withTopographyId(List.of(501)))).containsExactly(entry("Foto", 1L));
	}

	/**
	 * The arkivbildare filter reads the legal entity on the archive, not the originator on the object. It reaches an
	 * object from levels below the archive, an archive whose arkivbildare is a person counts as none, and a deleted legal
	 * entity selects nothing. Combined with the originator filter, both have to hold.
	 */
	@Test
	void archiveCreatorFilterReadsTheArchiveNotTheOriginator() {
		final var committee = LegalEntityEntity.create().withLegalEntityId(20).withName("Nödhjälpskommittén");
		final var shipping = LegalEntityEntity.create().withLegalEntityId(21).withName("Rederiet");
		final var closed = LegalEntityEntity.create().withLegalEntityId(22).withName("Nedlagt bolag");
		entityManager.persist(committee);
		entityManager.persist(shipping);
		entityManager.persist(closed);
		entityManager.flush();
		persistNode(100, 0, null, false);
		persistNode(110, 100, null, false);
		persistNode(111, 110, null, false);
		persistNode(200, 0, null, false);
		persistNode(300, 0, null, false);
		persistNodeAttribute(100, "FIELD1", 20);
		persistNodeAttribute(200, "FIELD2", 7);
		persistNodeAttribute(300, "FIELD1", 22);
		entityManager.createNativeQuery("UPDATE JURPERS SET DELETEDDATE = '2026-01-01' WHERE J_ID = 22").executeUpdate();
		photoRepository.saveAndFlush(PhotoEntity.create().withId(1).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I en volym").withNodeId(111));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(2).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I arkivet").withNodeId(100));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(3).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("Utan arkiv"));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(4).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I en persons arkiv").withNodeId(200));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(5).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I ett nedlagt bolags arkiv").withNodeId(300));
		photoRepository.findById(1).ifPresent(photo -> photo.setCreatorLegalEntity(shipping));
		photoRepository.findById(3).ifPresent(photo -> photo.setCreatorLegalEntity(committee));
		photoRepository.flush();
		entityManager.clear();

		final var bothFilters = CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of(20));
		bothFilters.setCreatorLegalEntityId(List.of(21));

		assertThat(findKeys(CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of(20)))).containsExactly("foto-1", "foto-2");
		assertThat(findKeys(CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of(21)))).isEmpty();
		assertThat(findKeys(CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of(22)))).isEmpty();
		assertThat(findKeys(bothFilters)).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of()))).hasSize(5);
	}

	/**
	 * The walk stops at the first node that names any arkivbildare: a series whose arkivbildare is a person does not take
	 * the legal entity of the archive above it. JURPERS 1 is the blank sentinel and names no arkivbildare, even though the
	 * row exists.
	 */
	@Test
	void archiveCreatorFilterStopsAtAPersonAndSkipsTheSentinel() {
		final var placeholder = LegalEntityEntity.create().withLegalEntityId(1).withName("Ingen");
		final var committee = LegalEntityEntity.create().withLegalEntityId(20).withName("Nödhjälpskommittén");
		entityManager.persist(placeholder);
		entityManager.persist(committee);
		entityManager.flush();
		persistNode(100, 0, null, false);
		persistNode(110, 100, null, false);
		persistNode(400, 0, null, false);
		persistNodeAttribute(100, "FIELD1", 20);
		persistNodeAttribute(110, "FIELD2", 7);
		persistNodeAttribute(400, "FIELD1", 1);
		photoRepository.saveAndFlush(PhotoEntity.create().withId(1).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I arkivet").withNodeId(100));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(2).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I en persons serie").withNodeId(110));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(3).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I ett arkiv utan arkivbildare").withNodeId(400));
		entityManager.clear();

		assertThat(findKeys(CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of(20)))).containsExactly("foto-1");
		assertThat(findKeys(CombinedObjectParameters.create().withArchiveCreatorLegalEntityId(List.of(1)))).isEmpty();
	}

	/** Each arkivbildare once, however many objects its archive holds, and none for an archive without objects. */
	@Test
	void findArchiveCreatorLegalEntityIdsListsEachArkivbildareWithObjectsOnce() {
		entityManager.persist(LegalEntityEntity.create().withLegalEntityId(20).withName("Nödhjälpskommittén"));
		entityManager.persist(LegalEntityEntity.create().withLegalEntityId(30).withName("Berg Aktiebolag"));
		entityManager.flush();
		persistNode(100, 0, null, false);
		persistNode(110, 100, null, false);
		persistNode(600, 0, null, false);
		persistNodeAttribute(100, "FIELD1", 20);
		persistNodeAttribute(600, "FIELD1", 30);
		photoRepository.saveAndFlush(PhotoEntity.create().withId(1).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I arkivet").withNodeId(100));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(2).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("I en serie").withNodeId(110));
		entityManager.clear();

		assertThat(combinedObjectRepository.findArchiveCreatorLegalEntityIds()).containsExactly(20);
	}

	/**
	 * Two categories with one originator each, a photo apiece, plus a photo whose originator is the sentinel legal
	 * entity (in the sentinel category, as every legal entity is by default) and one without any originator at all.
	 */
	private void persistCategorisedOriginators() {
		final var none = persistCategory(1, null);
		final var company = persistCategory(2, "Aktiebolag");
		final var committee = persistCategory(5, "Kommitté");
		final var placeholder = LegalEntityEntity.create().withLegalEntityId(1).withName("Ingen").withCategory(none);
		final var first = LegalEntityEntity.create().withLegalEntityId(20).withName("Nödhjälpskommittén").withCategory(committee);
		final var second = LegalEntityEntity.create().withLegalEntityId(21).withName("Rederiet").withCategory(company);
		entityManager.persist(placeholder);
		entityManager.persist(first);
		entityManager.persist(second);
		entityManager.flush();

		persistPhoto(1, "Av kommittén", null, "1900", null);
		persistPhoto(2, "Av rederiet", null, "1900", null);
		persistPhoto(3, "Av ingen", null, "1900", null);
		persistPhoto(4, "Utan upphovsman", null, "1900", null);
		photoRepository.findById(1).ifPresent(photo -> photo.setCreatorLegalEntity(first));
		photoRepository.findById(2).ifPresent(photo -> photo.setCreatorLegalEntity(second));
		photoRepository.findById(3).ifPresent(photo -> photo.setCreatorLegalEntity(placeholder));
		photoRepository.flush();
	}

	private CategoryEntity persistCategory(final int id, final String name) {
		final var category = CategoryEntity.create().withCategoryId(id).withName(name);
		entityManager.persist(category);
		entityManager.flush();
		return category;
	}

	private Map<Integer, Long> countByTopography(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.countByTopography(parameters).stream()
			.collect(toMap(TopographyCount::topographyId, TopographyCount::total, (first, _) -> first, LinkedHashMap::new));
	}

	/** Calls the category counters the way the service does, keyed by name since that is what a chip shows. */
	private Map<String, Long> countByCategory(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.countByCategory(parameters).stream()
			.collect(toMap(CategoryCount::name, CategoryCount::total, (first, _) -> first, LinkedHashMap::new));
	}

	/**
	 * Archive 100 (Föreningsarkivet) holds series 110 and volume 111, archive 200 points at blank, archive 300
	 * (Kommunarkivet) is deleted. Photo 1 names its own institution, photo 2 and the audio take their archives.
	 */
	private void persistInstitutionTree() {
		final var museum = persistInstitution(43, "Sundsvalls museum");
		persistInstitution(3, "Föreningsarkivet Västernorrland");
		persistInstitution(2, "Sundsvalls kommun - Kommunarkivet");
		persistInstitution(1, "");
		persistNode(100, 0, 3, false);
		persistNode(110, 100, null, false);
		persistNode(111, 110, null, false);
		persistNode(200, 0, 1, false);
		persistNode(300, 0, 2, true);
		persistNode(310, 300, null, false);
		photoRepository.saveAndFlush(PhotoEntity.create().withId(1).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("Stadsvy").withNodeId(111).withInstitution(museum));
		photoRepository.saveAndFlush(PhotoEntity.create().withId(2).withOptions(PUBLISHED).withObjectType("Foto").withDocumentTitle("Hamnen").withNodeId(111));
		audioRepository.saveAndFlush(AudioEntity.create().withId(3).withOptions(PUBLISHED).withDocumentTitle("Intervju").withNodeId(110));
		publicationRepository.saveAndFlush(PublicationEntity.create().withId(4).withOptions(PUBLISHED).withDocumentTitle("Utan institution").withNodeId(200));
		publicationRepository.saveAndFlush(PublicationEntity.create().withId(5).withOptions(PUBLISHED).withDocumentTitle("I raderat arkiv").withNodeId(310));
		persistPerson(6, "Anton", "Nordin", null);
	}

	private void persistNode(final int id, final int parentId, final Integer institutionId, final boolean deleted) {
		entityManager.createNativeQuery("INSERT INTO TBL_NODES (ID, PARENTID, NAME, NODETYPEID, DELETEDDATE) VALUES (?1, ?2, ?3, 1, "
			+ (deleted ? "CURRENT_DATE" : "NULL") + ")")
			.setParameter(1, id)
			.setParameter(2, parentId)
			.setParameter(3, "Nod " + id)
			.executeUpdate();
		if (institutionId != null) {
			entityManager.createNativeQuery("INSERT INTO TBL_NODEATTRIBUTES (NODEID, FIELD3) VALUES (?1, ?2)")
				.setParameter(1, id)
				.setParameter(2, institutionId)
				.executeUpdate();
		}
	}

	private void persistNodeAttribute(final int nodeId, final String field, final int value) {
		entityManager.createNativeQuery("INSERT INTO TBL_NODEATTRIBUTES (NODEID, " + field + ") VALUES (?1, ?2)")
			.setParameter(1, nodeId)
			.setParameter(2, value)
			.executeUpdate();
	}

	private Map<Integer, Long> countByInstitution(final CombinedObjectParameters parameters) {
		return combinedObjectRepository.countByInstitution(parameters).stream()
			.collect(toMap(InstitutionCount::institutionId, InstitutionCount::total, (first, _) -> first, LinkedHashMap::new));
	}
}
