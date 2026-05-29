package se.sundsvall.memories.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.service.TextService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class TextResourceFailureTest {

	private static final String INVALID_MUNICIPALITY_ID = "bad-municipality-id";
	private static final String SEARCH_PATH = "/{municipalityId}/texts";
	private static final String GET_PATH = "/{municipalityId}/texts/{id}";
	private static final String FILE_PATH = "/{municipalityId}/texts/{id}/file";
	private static final String MEDIA_FILE_PATH = "/{municipalityId}/texts/{id}/media/{mediaId}/file";

	@MockitoBean
	private TextService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchTextsWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(tuple("searchTexts.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getTextByIdWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID, "id", 1)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(tuple("getText.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getTextFileWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.queryParam("variant", "thumbnail")
				.build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID, "id", 1)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(tuple("getTextFile.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getTextFileWithInvalidVariant() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.queryParam("variant", "bogus")
				.build(Map.of("municipalityId", "2281", "id", 1)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getTextMediaFileWithInvalidMunicipalityId() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(MEDIA_FILE_PATH)
				.queryParam("variant", "thumbnail")
				.build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID, "id", 1, "mediaId", 1)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::field, Violation::message)
			.containsExactlyInAnyOrder(tuple("getTextMediaFile.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getTextMediaFileWithInvalidVariant() {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(MEDIA_FILE_PATH)
				.queryParam("variant", "bogus")
				.build(Map.of("municipalityId", "2281", "id", 1, "mediaId", 1)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);

		verifyNoInteractions(serviceMock);
	}
}
