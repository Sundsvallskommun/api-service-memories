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
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.PagedSeamanResponse;
import se.sundsvall.memories.api.model.Seaman;
import se.sundsvall.memories.service.SeamanService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class SeamanResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/seamen";
	private static final String GET_PATH = "/{municipalityId}/seamen/{id}";

	@MockitoBean
	private SeamanService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchSeamen() {
		final var seaman = Seaman.create().withId(1).withLastName1("Nordin").withFirstName("Anton");
		final var pagedResponse = PagedSeamanResponse.create()
			.withSeamen(List.of(seaman))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("lastName", "Nordin")
				.queryParam("yearFrom", "1850")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedSeamanResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getSeamen()).hasSize(1);
		assertThat(response.getSeamen().getFirst().getLastName1()).isEqualTo("Nordin");
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(serviceMock).search(any());
	}

	@Test
	void searchSeamenWithoutFilters() {
		final var pagedResponse = PagedSeamanResponse.create()
			.withSeamen(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedSeamanResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getSeamen()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getSeamanById() {
		final var id = 1;
		final var seaman = Seaman.create().withId(id).withLastName1("Nordin");

		when(serviceMock.getById(id)).thenReturn(seaman);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Seaman.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(id);
		verify(serviceMock).getById(id);
	}
}
