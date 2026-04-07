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
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.service.FilmService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class FilmResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/films";
	private static final String GET_PATH = "/{municipalityId}/films/{id}";
	private static final String FILE_PATH = "/{municipalityId}/films/{id}/file";

	@MockitoBean
	private FilmService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchFilms() {
		final var query = "sundsvall";
		final var film = Film.create().withFilmId(1L).withDoktitel("Sundsvall film");

		when(serviceMock.search(query)).thenReturn(List.of(film));

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("query", query)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Film.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).hasSize(1);
		assertThat(response.getFirst().getDoktitel()).isEqualTo("Sundsvall film");
		verify(serviceMock).search(query);
	}

	@Test
	void searchFilmsWithoutQuery() {
		when(serviceMock.search(null)).thenReturn(List.of());

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Film.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEmpty();
		verify(serviceMock).search(null);
	}

	@Test
	void getFilmById() {
		final var filmId = 1L;
		final var film = Film.create().withFilmId(filmId).withDoktitel("Test");

		when(serviceMock.getById(filmId)).thenReturn(film);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", filmId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Film.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getFilmId()).isEqualTo(filmId);
		verify(serviceMock).getById(filmId);
	}

	@Test
	void getFilmFile() {
		final var filmId = 1L;

		webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", filmId)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamFile(eq(filmId), any(HttpServletResponse.class));
	}
}
