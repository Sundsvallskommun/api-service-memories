package se.sundsvall.memories.api;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.api.model.PagedAudioResponse;
import se.sundsvall.memories.service.AudioService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class AudioResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/audios";
	private static final String GET_PATH = "/{municipalityId}/audios/{id}";
	private static final String FILE_PATH = "/{municipalityId}/audios/{id}/file";

	@MockitoBean
	private AudioService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchAudios() {
		final var audio = Audio.create().withAudioId(1).withDocumentTitle("Sundsvall intervju");
		final var pagedResponse = PagedAudioResponse.create()
			.withAudios(List.of(audio))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("query", "sundsvall")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedAudioResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getAudios()).hasSize(1);
		assertThat(response.getAudios().getFirst().getDocumentTitle()).isEqualTo("Sundsvall intervju");
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(serviceMock).search(any());
	}

	@Test
	void searchAudiosWithoutQuery() {
		final var pagedResponse = PagedAudioResponse.create()
			.withAudios(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedAudioResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getAudios()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getAudioById() {
		final var audioId = 1;
		final var audio = Audio.create().withAudioId(audioId).withDocumentTitle("Test");

		when(serviceMock.getById(audioId)).thenReturn(audio);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", audioId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Audio.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getAudioId()).isEqualTo(audioId);
		verify(serviceMock).getById(audioId);
	}

	@Test
	void getAudioFile() {
		final var audioId = 1;

		webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", audioId)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamFile(eq(audioId), any(HttpServletResponse.class));
	}
}
