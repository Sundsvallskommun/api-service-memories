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
import se.sundsvall.memories.api.model.Topography;
import se.sundsvall.memories.service.TopographyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class TopographyResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/{municipalityId}/topographies";

	@MockitoBean
	private TopographyService topographyServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getTopographies() {
		when(topographyServiceMock.getTopographies()).thenReturn(List.of(
			Topography.create().withTopographyId(1).withDisplayName("Sundsvall").withName("Sundsvall").withCode("SUN"),
			Topography.create().withTopographyId(2).withDisplayName("Timrå").withName("Timrå").withCode("TIM")));

		final var response = webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Topography.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).hasSize(2)
			.extracting(Topography::getTopographyId, Topography::getDisplayName)
			.containsExactly(tuple(1, "Sundsvall"), tuple(2, "Timrå"));
		verify(topographyServiceMock).getTopographies();
	}
}
