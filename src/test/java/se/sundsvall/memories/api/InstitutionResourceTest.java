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
import se.sundsvall.memories.api.model.Institution;
import se.sundsvall.memories.service.InstitutionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class InstitutionResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/institutions";

	@MockitoBean
	private InstitutionService institutionServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getInstitutions() {
		when(institutionServiceMock.getInstitutions()).thenReturn(List.of(
			Institution.create().withInstitutionId(2).withName("Föreningsarkivet Västernorrland").withCode("FAV"),
			Institution.create().withInstitutionId(3).withName("Sundsvalls museum").withCode("SVM")));

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Institution.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).hasSize(2)
			.extracting(Institution::getInstitutionId, Institution::getName)
			.containsExactly(tuple(2, "Föreningsarkivet Västernorrland"), tuple(3, "Sundsvalls museum"));
		verify(institutionServiceMock).getInstitutions();
	}
}
