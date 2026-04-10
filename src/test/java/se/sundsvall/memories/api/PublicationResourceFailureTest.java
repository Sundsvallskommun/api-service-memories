package se.sundsvall.memories.api;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.service.PublicationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class PublicationResourceFailureTest {

	private static final String INVALID_MUNICIPALITY_ID = "bad-municipality-id";
	private static final String SEARCH_PATH = "/{municipalityId}/publications";
	private static final String GET_PATH = "/{municipalityId}/publications/{id}";
	private static final String FILE_PATH = "/{municipalityId}/publications/{id}/file/{variant}";

	@MockitoBean
	private PublicationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchPublicationsWithInvalidMunicipalityId() {
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
			.containsExactlyInAnyOrder(tuple("searchPublications.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getPublicationByIdWithInvalidMunicipalityId() {
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
			.containsExactlyInAnyOrder(tuple("getPublication.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}

	static Stream<Arguments> fileVariantPaths() {
		return Stream.of(
			Arguments.of("liten", "getPublicationFileLiten.municipalityId"),
			Arguments.of("stor", "getPublicationFileStor.municipalityId"),
			Arguments.of("original", "getPublicationFileOriginal.municipalityId"),
			Arguments.of("txt", "getPublicationFileTxt.municipalityId"),
			Arguments.of("xtra", "getPublicationFileXtra.municipalityId"));
	}

	@ParameterizedTest
	@MethodSource("fileVariantPaths")
	void getPublicationFileWithInvalidMunicipalityId(final String variant, final String expectedField) {
		final var response = webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.build(Map.of("municipalityId", INVALID_MUNICIPALITY_ID, "id", 1, "variant", variant)))
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
			.containsExactlyInAnyOrder(tuple(expectedField, "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}
}
