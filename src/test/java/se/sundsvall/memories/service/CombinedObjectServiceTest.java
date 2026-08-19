package se.sundsvall.memories.service;

import java.util.List;
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
import se.sundsvall.memories.integration.db.CombinedObjectRepository.TypeCount;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Which rows the filters select, and that the counters agree with the search, is verified against a real database in
// CombinedObjectSpecificationTest. These tests cover what the service itself does: build the pageable, forward the
// parameters, and assemble the response.
@ExtendWith(MockitoExtension.class)
class CombinedObjectServiceTest {

	@Mock
	private CombinedObjectRepository repositoryMock;

	@InjectMocks
	private CombinedObjectService service;

	private static TypeCount typeCount(final String type, final long total) {
		final var typeCount = mock(TypeCount.class);
		when(typeCount.getObjectType()).thenReturn(type);
		when(typeCount.getTotal()).thenReturn(total);
		return typeCount;
	}

	@Test
	void searchDelegatesResolvesLocationAndBuildsTypeCounts() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = CombinedObjectParameters.create().withQuery("Sundsvall").withYearFrom(1900).withYearTo(1950).withLocation("Sundsvall").withPage(1).withLimit(100);
		final var entity = CombinedObjectEntity.create().withObjectKey("foto-1001").withObjectType("Foto").withTitle("Stadsvy")
			.withTopography(TopographyEntity.create().withId(1).withName("Sundsvalls kommun"));
		// Built before the stubbing below: these are stubbed mocks themselves, and Mockito rejects stubbing inside an
		// unfinished when(...).
		final var counts = List.of(typeCount("Foto", 1L), typeCount("Text", 3L));

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
		when(repositoryMock.countByType("Sundsvall", 1900, 1950, "Sundsvall")).thenReturn(counts);

		final var result = service.search(parameters);

		assertThat(result.getObjects()).hasSize(1);
		assertThat(result.getObjects().getFirst().getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getTypeCounts()).containsExactly(entry("Foto", 1L), entry("Text", 3L));
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
	}

	/**
	 * The search trims through its specifications and the counters are a native query, so the service has to trim on
	 * the way to the counters. Otherwise a blank parameter means "no filter" for the list and a literal match for the
	 * chips, and the two disagree.
	 */
	@Test
	void searchTrimsOnTheWayToTheCounters() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = CombinedObjectParameters.create().withQuery("  Sundsvall  ").withLocation("   ");

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));
		when(repositoryMock.countByType(eq("Sundsvall"), isNull(), isNull(), isNull())).thenReturn(List.of());

		final var result = service.search(parameters);

		assertThat(result.getObjects()).isEmpty();
		assertThat(result.getTypeCounts()).isEmpty();

		// The search itself gets the parameters untouched — the trimming there lives in the specifications.
		final var searchCaptor = ArgumentCaptor.forClass(CombinedObjectParameters.class);
		verify(repositoryMock).findAllByParameters(searchCaptor.capture(), eq(pageable));
		assertThat(searchCaptor.getValue()).isSameAs(parameters);
		verify(repositoryMock).countByType(eq("Sundsvall"), isNull(), isNull(), isNull());
	}
}
