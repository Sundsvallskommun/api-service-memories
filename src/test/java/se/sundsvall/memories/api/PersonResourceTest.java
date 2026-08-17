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
import se.sundsvall.memories.api.model.PagedPersonResponse;
import se.sundsvall.memories.api.model.Person;
import se.sundsvall.memories.service.PersonService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class PersonResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SEARCH_PATH = "/{municipalityId}/persons";
	private static final String GET_PATH = "/{municipalityId}/persons/{id}";

	@MockitoBean
	private PersonService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void searchPersons() {
		final var person = Person.create().withPersonId(1).withLastName("Nordin").withFirstName("Anton");
		final var pagedResponse = PagedPersonResponse.create()
			.withPersons(List.of(person))
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(1).withTotalRecords(1).withTotalPages(1));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.queryParam("lastName", "Nordin")
				.queryParam("yearFrom", "1850")
				.queryParam("yearTo", "1900")
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedPersonResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPersons()).hasSize(1);
		assertThat(response.getPersons().getFirst().getLastName()).isEqualTo("Nordin");
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(serviceMock).search(any());
	}

	@Test
	void searchPersonsWithoutFilters() {
		final var pagedResponse = PagedPersonResponse.create()
			.withPersons(List.of())
			.withMetaData(PagingAndSortingMetaData.create().withPage(1).withLimit(100).withCount(0).withTotalRecords(0).withTotalPages(0));

		when(serviceMock.search(any())).thenReturn(pagedResponse);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(SEARCH_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(PagedPersonResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPersons()).isEmpty();
		verify(serviceMock).search(any());
	}

	@Test
	void getPersonById() {
		final var personId = 1;
		final var person = Person.create().withPersonId(personId).withLastName("Nordin");

		when(serviceMock.getById(personId)).thenReturn(person);

		final var response = webTestClient.get()
			.uri(builder -> builder.path(GET_PATH)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "id", personId)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Person.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getPersonId()).isEqualTo(personId);
		verify(serviceMock).getById(personId);
	}
}
