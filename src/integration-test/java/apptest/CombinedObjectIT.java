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

	/**
	 * The register types (Person, Sjöman) take part in the same union as the object types — a name matches the person
	 * record as well as both seamen, including the one carrying the name in its second surname column.
	 */
	@Test
	void test02_searchObjectsIncludesRegisters() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A legal entity is the one register type with a topography reference, so its location is resolved to a place name
	 * just like the object types', and its year comes from the start of the activity period.
	 */
	@Test
	void test03_searchObjectsIncludesLegalEntities() {
		setupCall()
			.withServicePath(PATH + "?query=kommitt")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Unpublished register rows (person 3 and legal entity 30, neither with bit 4 of OPTIONS set) must stay out of the
	 * combined search, exactly as they stay out of the per-type searches.
	 */
	@Test
	void test04_searchObjectsExcludesUnpublishedRegisterRows() {
		setupCall()
			.withServicePath(PATH + "?query=Dold")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The placeholder rows PERSON.P_ID = 0 and JURPERS.J_ID = 1 ("ingen") are sentinels, not archive records. Both are
	 * flagged published, so only the explicit id predicates keep them out of the result.
	 */
	@Test
	void test05_searchObjectsExcludesRegisterPlaceholders() {
		setupCall()
			.withServicePath(PATH + "?query=Ingen")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Every value the parameter accepts has to resolve as an entity attribute. A property that only fails once it
	 * reaches Spring Data is a 500 no unit test would catch, so each one is walked through the whole request here.
	 */
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

	/**
	 * A column of the view is not an entity attribute, and is refused with the list of alternatives rather than
	 * reaching Spring Data and failing there.
	 */
	@Test
	void test09_searchObjectsWithInvalidSortBy() {
		setupCall()
			.withServicePath(PATH + "?sortBy=SORT_YEAR")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

}
