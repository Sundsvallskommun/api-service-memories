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
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.api.model.PagedLegalEntityResponse;
import se.sundsvall.memories.service.LegalEntityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class LegalEntityResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/legal-entities";
	private static final String GET_PATH = "/{municipalityId}/legal-entities/{id}";

	@MockitoBean
	private LegalEntityService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchLegalEntities() {
		final var entity = LegalEntity.create().withLegalEntityId(1).withName("Nödhjälpskommittén");
		final var pagedResponse = PagedLegalEntityResponse.create()
			.withLegalEntities(List.of(entity))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("name", "Nödhjälp")
				.queryParam("categoryId", "5")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedLegalEntityResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getLegalEntities()).hasSize(1);
		assertThat(response.getLegalEntities().getFirst().getName()).isEqualTo("Nödhjälpskommittén");
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(serviceMock).search(any());
	}

	@Test
	void searchLegalEntitiesWithoutFilters() {
		final var pagedResponse = PagedLegalEntityResponse.create()
			.withLegalEntities(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedLegalEntityResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getLegalEntities()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getLegalEntityById() {
		final var id = 1;
		final var entity = LegalEntity.create().withLegalEntityId(id).withName("Nödhjälpskommittén");

		when(serviceMock.getById(id)).thenReturn(entity);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(LegalEntity.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getLegalEntityId()).isEqualTo(id);
		verify(serviceMock).getById(id);
	}
}
