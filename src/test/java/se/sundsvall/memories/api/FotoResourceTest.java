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
import se.sundsvall.memories.api.model.Foto;
import se.sundsvall.memories.api.model.PagedFotoResponse;
import se.sundsvall.memories.service.FotoService;
import se.sundsvall.memories.service.FotoService.FileVariant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class FotoResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/photos";
	private static final String GET_PATH = "/{municipalityId}/photos/{id}";
	private static final String FILE_PATH = "/{municipalityId}/photos/{id}/file";

	@MockitoBean
	private FotoService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchPhotos() {
		final var foto = Foto.create().withFotoId(1).withDoktitel("Stadsvy");
		final var pagedResponse = PagedFotoResponse.create()
			.withPhotos(List.of(foto))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("query", "Sundsvall")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedFotoResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPhotos()).hasSize(1);
		assertThat(response.getPhotos().getFirst().getDoktitel()).isEqualTo("Stadsvy");
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(serviceMock).search(any());
	}

	@Test
	void searchPhotosWithoutQuery() {
		final var pagedResponse = PagedFotoResponse.create()
			.withPhotos(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedFotoResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPhotos()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getPhotoById() {
		final var fotoId = 1234;
		final var foto = Foto.create().withFotoId(fotoId).withDoktitel("Stadsvy");

		when(serviceMock.getById(fotoId)).thenReturn(foto);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", fotoId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Foto.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getFotoId()).isEqualTo(fotoId);
		verify(serviceMock).getById(fotoId);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of("liten", FileVariant.LITEN),
			Arguments.of("stor", FileVariant.STOR));
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void getPhotoFile(final String pathSegment, final FileVariant expectedVariant) {
		final var fotoId = 1234;

		webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.queryParam("variant", pathSegment)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", fotoId)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamFile(eq(fotoId), eq(expectedVariant), any(HttpServletResponse.class));
	}
}
