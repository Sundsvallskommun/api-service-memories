package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.memories.Application;

import static org.springframework.http.HttpMethod.GET;
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
	 * Node 400 points at a node type that does not exist, so it comes back without a type rather than not at all. Node
	 * 500 is soft-deleted and node 300 unpublished, and neither may appear.
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
}
