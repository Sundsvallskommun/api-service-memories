package se.sundsvall.memories.api;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
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
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.PagedPublicationResponse;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.service.PublicationService;
import se.sundsvall.memories.service.PublicationService.FileVariant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class PublicationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/publications";
	private static final String GET_PATH = "/{municipalityId}/publications/{id}";
	private static final String FILE_PATH = "/{municipalityId}/publications/{id}/file";

	@MockitoBean
	private PublicationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchPublications() {
		final var publication = Publication.create().withPublId(1).withDoktitel("Drunkningsolycka");
		final var pagedResponse = PagedPublicationResponse.create()
			.withPublications(List.of(publication))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("query", "drunkning")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedPublicationResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPublications()).hasSize(1);
		assertThat(response.getPublications().getFirst().getDoktitel()).isEqualTo("Drunkningsolycka");
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(serviceMock).search(any());
	}

	@Test
	void searchPublicationsWithoutQuery() {
		final var pagedResponse = PagedPublicationResponse.create()
			.withPublications(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedPublicationResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPublications()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getPublicationById() {
		final var publicationId = 207;
		final var publication = Publication.create().withPublId(publicationId).withDoktitel("Alfwar och Skämt");

		when(serviceMock.getById(publicationId)).thenReturn(publication);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", publicationId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Publication.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPublId()).isEqualTo(publicationId);
		verify(serviceMock).getById(publicationId);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of("liten", FileVariant.LITEN),
			Arguments.of("stor", FileVariant.STOR),
			Arguments.of("original", FileVariant.ORIGINAL),
			Arguments.of("txt", FileVariant.TXT),
			Arguments.of("xtra", FileVariant.XTRA));
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void getPublicationFile(final String pathSegment, final FileVariant expectedVariant) {
		final var publicationId = 207;

		webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.queryParam("variant", pathSegment)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", publicationId)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamFile(eq(publicationId), eq(expectedVariant), any(HttpServletResponse.class));
	}
}
