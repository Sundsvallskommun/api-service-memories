package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.memories.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(files = "classpath:/PersonIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PersonIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String RESPONSE_FILE = "response.json";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/persons";

	@Test
	void test01_searchPersons() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_searchPersonsByLastName() {
		setupCall()
			.withServicePath(PATH + "?lastName=Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_searchPersonsByYearRange() {
		setupCall()
			.withServicePath(PATH + "?yearFrom=1860&yearTo=1880")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getPersonById() {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_getPersonByIdNotFound() {
		setupCall()
			.withServicePath(PATH + "/999")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A yearTo-only search must not match persons whose FODDAT is blank or non-numeric — those cast to year 0, which
	 * would otherwise satisfy every upper bound.
	 */
	@Test
	void test06_searchPersonsByYearToExcludesUndated() {
		setupCall()
			.withServicePath(PATH + "?yearTo=1860")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * P_ID = 0 is the "ingen person" placeholder that other tables point at, not an archive record — it must not be
	 * retrievable by id. Note it carries OPTIONS = 6 (published), so a published-bit filter would not have hidden it;
	 * only the explicit P_ID &lt;&gt; 0 exclusion does.
	 */
	@Test
	void test07_getPersonByIdPlaceholderNotFound() {
		setupCall()
			.withServicePath(PATH + "/0")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Person 3 is unpublished (OPTIONS = 0) and is therefore hidden from search, but must still be retrievable by id —
	 * a planned administrative interface depends on it. This locks the decision in so it cannot be silently reversed by
	 * adding a published-bit filter to the detail lookup.
	 */
	@Test
	void test08_getUnpublishedPersonById() {
		setupCall()
			.withServicePath(PATH + "/3")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
