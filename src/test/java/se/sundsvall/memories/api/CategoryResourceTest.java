package se.sundsvall.memories.api;

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
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.service.CategoryLookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class CategoryResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/categories";

	@MockitoBean
	private CategoryLookup categoryLookupMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getCategories() {
		when(categoryLookupMock.getAllCategories()).thenReturn(List.of(
			Category.create().withCategoryId(2).withCode("AB").withName("Aktiebolag"),
			Category.create().withCategoryId(5).withCode("FÖR").withName("Förening")));

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Category.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).hasSize(2)
			.extracting(Category::getName)
			.containsExactly("Aktiebolag", "Förening");
		verify(categoryLookupMock).getAllCategories();
	}
}
