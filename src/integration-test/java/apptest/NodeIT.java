package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.memories.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(files = "classpath:/NodeIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class NodeIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String RESPONSE_FILE = "response.json";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/nodes";

	/**
	 * Node 400 points at a node type that does not exist, so it comes back without a type rather than not at all.
	 */
	@Test
	void test01_searchNodes() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The free text reaches the description as well as the name — a series is often findable only through what its
	 * description says it contains.
	 */
	@Test
	void test02_searchNodesByQuery() {
		setupCall()
			.withServicePath(PATH + "?query=fotograf")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_searchNodesByNodeType() {
		setupCall()
			.withServicePath(PATH + "?nodeTypeId=2")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A node that has not ended stays in range however late the range starts. The legacy schema writes that open end as
	 * both {@code NULL} (node 200, 400) and {@code 0} (node 120).
	 */
	@Test
	void test04_searchNodesByPeriod() {
		setupCall()
			.withServicePath(PATH + "?yearFrom=1952")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Node 300 carries OPTIONS = 1, i.e. bit 4 is not set, and must stay out of the search.
	 */
	@Test
	void test05_searchNodesExcludesUnpublished() {
		setupCall()
			.withServicePath(PATH + "?query=Dolt")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Node 111 sits two levels down, so the detail carries the whole path root first: the archive, then the series.
	 */
	@Test
	void test06_getNodeById() {
		setupCall()
			.withServicePath(PATH + "/111")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_getNodeByIdNotFound() {
		setupCall()
			.withServicePath(PATH + "/999")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The children come back in the archive's own order (SORT), not in the order the rows happen to be stored.
	 */
	@Test
	void test08_getNodeChildren() {
		setupCall()
			.withServicePath(PATH + "/100/children")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Listing the children of a node that does not exist is a 404, not an empty page — the two mean different things to
	 * a client walking the tree.
	 */
	@Test
	void test09_getNodeChildrenNotFound() {
		setupCall()
			.withServicePath(PATH + "/999/children")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Node 300 is unpublished and hidden from search, but must stay retrievable by id — the same decision the person
	 * and legal entity lookups document for their administrative interface.
	 */
	@Test
	void test10_getUnpublishedNodeById() {
		setupCall()
			.withServicePath(PATH + "/300")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
