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
import se.sundsvall.memories.api.model.PagedTextResponse;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.service.TextService;
import se.sundsvall.memories.service.TextService.FileVariant;
import se.sundsvall.memories.service.TextService.MediaFileVariant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class TextResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/texts";
	private static final String GET_PATH = "/{municipalityId}/texts/{id}";
	private static final String FILE_PATH = "/{municipalityId}/texts/{id}/file";
	private static final String MEDIA_FILE_PATH = "/{municipalityId}/texts/{id}/media/{mediaId}/file";

	@MockitoBean
	private TextService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchTexts() {
		final var text = Text.create().withTextId(1).withDocumentTitle("Minne");
		final var pagedResponse = PagedTextResponse.create()
			.withTexts(List.of(text))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("query", "stadshuset")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedTextResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTexts()).hasSize(1);
		assertThat(response.getTexts().getFirst().getDocumentTitle()).isEqualTo("Minne");
		verify(serviceMock).search(any());
	}

	@Test
	void searchTextsWithoutQuery() {
		final var pagedResponse = PagedTextResponse.create()
			.withTexts(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedTextResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTexts()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getTextById() {
		final var textId = 1001;
		final var text = Text.create().withTextId(textId).withDocumentTitle("Minne från Sundsvall");

		when(serviceMock.getById(textId)).thenReturn(text);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", textId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Text.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTextId()).isEqualTo(textId);
		verify(serviceMock).getById(textId);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of("thumbnail", FileVariant.THUMBNAIL),
			Arguments.of("large", FileVariant.LARGE),
			Arguments.of("text", FileVariant.TEXT));
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void getTextFile(final String pathSegment, final FileVariant expectedVariant) {
		final var textId = 1001;

		webTestClient.get()
			.uri(builder -> builder.path(FILE_PATH)
				.queryParam("variant", pathSegment)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", textId)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamFile(eq(textId), eq(expectedVariant), any(HttpServletResponse.class));
	}

	static Stream<Arguments> mediaFileVariants() {
		return Stream.of(
			Arguments.of("thumbnail", MediaFileVariant.THUMBNAIL),
			Arguments.of("large", MediaFileVariant.LARGE),
			Arguments.of("original", MediaFileVariant.ORIGINAL));
	}

	@ParameterizedTest
	@MethodSource("mediaFileVariants")
	void getTextMediaFile(final String pathSegment, final MediaFileVariant expectedVariant) {
		final var textId = 1001;
		final var mediaId = 1;

		webTestClient.get()
			.uri(builder -> builder.path(MEDIA_FILE_PATH)
				.queryParam("variant", pathSegment)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", textId, "mediaId", mediaId)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamMediaFile(eq(textId), eq(mediaId), eq(expectedVariant), any(HttpServletResponse.class));
	}
}
