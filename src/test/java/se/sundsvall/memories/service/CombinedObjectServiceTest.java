package se.sundsvall.memories.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Which rows the filters select, and which counts they produce, is verified against a real database in
// CombinedObjectSpecificationTest. These tests cover what the service itself does: build the pageable, forward the
// parameters, and assemble the response.
@ExtendWith(MockitoExtension.class)
class CombinedObjectServiceTest {

	@Mock
	private CombinedObjectRepository repositoryMock;

	@InjectMocks
	private CombinedObjectService service;

	@Test
	void searchDelegatesResolvesLocationAndBuildsTypeCounts() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = CombinedObjectParameters.create().withQuery("Sundsvall").withYearFrom(1900).withYearTo(1950).withLocation("Sundsvall").withPage(1).withLimit(100);
		final var entity = CombinedObjectEntity.create().withObjectKey("foto-1001").withObjectType("Foto").withTitle("Stadsvy")
			.withTopography(TopographyEntity.create().withId(1).withName("Sundsvalls kommun"));
		final Map<String, Long> typeCounts = new LinkedHashMap<>();
		typeCounts.put("Foto", 1L);
		typeCounts.put("Text", 3L);

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
		when(repositoryMock.countByType(any(CombinedObjectParameters.class))).thenReturn(typeCounts);

		final var result = service.search(parameters);

		assertThat(result.getObjects()).hasSize(1);
		assertThat(result.getObjects().getFirst().getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getTypeCounts()).containsExactly(entry("Foto", 1L), entry("Text", 3L));
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
	}

	/**
	 * The counters have to see the same filters as the search, otherwise a chip can claim more hits than the list can
	 * ever show.
	 */
	@Test
	void searchForwardsTheSameParametersToBothQueries() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = CombinedObjectParameters.create().withQuery("   ").withLocation("");

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));
		when(repositoryMock.countByType(any(CombinedObjectParameters.class))).thenReturn(Map.of());

		final var result = service.search(parameters);

		assertThat(result.getObjects()).isEmpty();
		assertThat(result.getTypeCounts()).isEmpty();

		final var searchCaptor = ArgumentCaptor.forClass(CombinedObjectParameters.class);
		final var countCaptor = ArgumentCaptor.forClass(CombinedObjectParameters.class);
		verify(repositoryMock).findAllByParameters(searchCaptor.capture(), eq(pageable));
		verify(repositoryMock).countByType(countCaptor.capture());
		assertThat(searchCaptor.getValue()).isSameAs(parameters);
		assertThat(countCaptor.getValue()).isSameAs(parameters);
	}
}
