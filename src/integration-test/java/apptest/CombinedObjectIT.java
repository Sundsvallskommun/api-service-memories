package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.memories.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(files = "classpath:/CombinedObjectIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CombinedObjectIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String RESPONSE_FILE = "response.json";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/objects";

	@Test
	void test01_searchObjectsByQuery() {
		setupCall()
			.withServicePath(PATH + "?query=Folkmusik")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_searchObjectsIncludesRegisters() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_searchObjectsIncludesLegalEntities() {
		setupCall()
			.withServicePath(PATH + "?query=kommitt")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_searchObjectsExcludesUnpublishedRegisterRows() {
		setupCall()
			.withServicePath(PATH + "?query=Dold")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// PERSON.P_ID = 0 and JURPERS.J_ID = 1 are sentinels, and both are flagged published.
	@Test
	void test05_searchObjectsExcludesRegisterPlaceholders() {
		setupCall()
			.withServicePath(PATH + "?query=Ingen")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_searchObjectsSortedByTitle() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=title&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_searchObjectsSortedByYear() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=year&sortBy=objectKey&sortDirection=DESC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_searchObjectsSortedByObjectType() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=objectType&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_searchObjectsWithInvalidSortBy() {
		setupCall()
			.withServicePath(PATH + "?sortBy=SORT_YEAR")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_searchObjectsByCreator() {
		setupCall()
			.withServicePath(PATH + "?creator=Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_searchObjectsByCreatorIgnoresThePlaceholders() {
		setupCall()
			.withServicePath(PATH + "?creator=Ingen")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test12_searchObjectsRanksTheNamedPersonFirst() {
		setupCall()
			.withServicePath(PATH + "?query=Anton Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// Legal entity 10 is registered as "Nödhjälpskommittén 1888-1889" and matched on its alternative name.
	@Test
	void test13_searchObjectsMatchesLegalEntityByCompanyName() {
		setupCall()
			.withServicePath(PATH + "?query=Nödhjälpskommittén")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test14_searchObjectsMatchesTheDocumentTitle() {
		setupCall()
			.withServicePath(PATH + "?query=Stadsvy")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test15_searchObjectsMatchesASecondSurname() {
		setupCall()
			.withServicePath(PATH + "?query=Erik Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test16_searchObjectsSortedByRelevance() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=relevance")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test17_searchObjectsExcludesSoftDeletedRows() {
		setupCall()
			.withServicePath(PATH + "?query=raderad")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// The type selection narrows the list but not typeCounts, so the unselected chips keep their counts.
	@Test
	void test18_searchObjectsFilteredByObjectType() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Sjöman&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test19_searchObjectsFilteredBySeveralObjectTypes() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Person,Sjöman&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test20_searchObjectsFilteredByUnknownObjectType() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Karta")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// location reads the free text — the birth parish for the person registers — and falls back to the topography,
	// which is what puts foto-1005 among the Sundsvall rows instead of first among the empties.
	@Test
	void test21_searchObjectsSortedByLocation() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=location&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// The gender selection narrows the registers to the men and leaves the types recording no gender — the photo and
	// the seamen — where they were. It does not reach its own counters, which keep counting every gender the search
	// matches, and the type counts still cover every type.
	@Test
	void test22_searchObjectsFilteredByGender() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&gender=man&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// Narrowed to a type that records no gender, the selection has nothing to narrow: the seamen come back whole
	// rather than not at all, and the gender counters are empty because no matched row records one.
	@Test
	void test23_searchObjectsFilteredByGenderAndATypeRecordingNone() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Sjöman&gender=Kvinna&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// A category is a property of the originator: selecting one keeps the objects whose originator is in it. The
	// selection narrows the list but not its own counters, which keep counting every category the search matches.
	@Test
	void test24_searchObjectsFilteredByCategory() {
		setupCall()
			.withServicePath(PATH + "?categoryId=5")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test25_searchObjectsFilteredBySeveralCategories() {
		setupCall()
			.withServicePath(PATH + "?categoryId=2,5&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// KAT_ID 1 is the sentinel every legal entity defaults to, not a category: naming it matches nothing rather than
	// every object whose originator is uncategorised, while the category counters stay as they were.
	@Test
	void test26_searchObjectsFilteredByTheSentinelCategory() {
		setupCall()
			.withServicePath(PATH + "?categoryId=1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// Each counter leaves out only its own selection: the type counts still apply the category and so name the film,
	// while the category counts apply the type and find no photo with a categorised originator.
	@Test
	void test27_searchObjectsFilteredByCategoryAndATypeWithoutOne() {
		setupCall()
			.withServicePath(PATH + "?categoryId=5&objectType=Foto")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// The exact counterpart of location: only the rows placed in topography 2 (Timrå), the legal entity among them,
	// and not the rows that merely name the place in their free text.
	@Test
	void test28_searchObjectsFilteredByTopography() {
		setupCall()
			.withServicePath(PATH + "?topographyId=2&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/** What a node's own page lists under it: the objects created in that node, and no register row. */
	@Test
	void test29_searchObjectsFilteredByNode() {
		setupCall()
			.withServicePath(PATH + "?nodeId=19000")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/** The ids are alternatives, and every hit reports the node it sits in. */
	@Test
	void test30_searchObjectsFilteredBySeveralNodes() {
		setupCall()
			.withServicePath(PATH + "?nodeId=19000,20003&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/** Rows without a place come last whichever way the list runs, so a list of places never opens on the empty ones. */
	@Test
	void test31_searchObjectsSortedByLocationDescending() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=location&sortDirection=DESC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
