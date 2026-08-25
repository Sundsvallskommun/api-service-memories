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
}
