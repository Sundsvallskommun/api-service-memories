package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.memories.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(files = "classpath:/FilmIT/", classes = Application.class)
@Sql(scripts = "/db/scripts/testdata-it.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class FilmIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String RESPONSE_FILE = "response.json";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/films";

	@Test
	void test01_searchFilms() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_searchFilmsWithQuery() {
		setupCall()
			.withServicePath(PATH + "?query=midsommar")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getFilmById() {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getFilmByIdNotFound() {
		setupCall()
			.withServicePath(PATH + "/999")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
