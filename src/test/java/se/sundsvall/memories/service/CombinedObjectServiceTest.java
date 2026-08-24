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
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;

// Which rows the filters select, how they are ranked, and that the counters agree with the search, is verified against
// a real database in CombinedObjectSpecificationTest. These tests cover what the service itself does: build the
// pageable, forward the parameters, and assemble the response.
@ExtendWith(MockitoExtension.class)
class CombinedObjectServiceTest {

	private static final PageRequest PAGEABLE = PageRequest.of(0, 100);

	@Mock
	private CombinedObjectRepository repositoryMock;

	@InjectMocks
	private CombinedObjectService service;

	@Test
	void searchDelegatesResolvesLocationAndBuildsTypeCounts() {
		final var parameters = CombinedObjectParameters.create().withQuery("Sundsvall").withYearFrom(1900).withYearTo(1950).withLocation("Sundsvall").withPage(1).withLimit(100);
		final var entity = CombinedObjectEntity.create().withObjectKey("foto-1001").withObjectType("Foto").withTitle("Stadsvy")
			.withTopography(TopographyEntity.create().withId(1).withName("Sundsvalls kommun"));

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(PAGEABLE)))
			.thenReturn(new PageImpl<>(List.of(entity), PAGEABLE, 1));
		when(repositoryMock.countByType(parameters)).thenReturn(List.of(new TypeCount("Foto", 1L), new TypeCount("Text", 3L)));

		final var result = service.search(parameters);

		assertThat(result.getObjects()).hasSize(1);
		assertThat(result.getObjects().getFirst().getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getTypeCounts()).containsExactly(entry("Foto", 1L), entry("Text", 3L));
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
	}

	/**
	 * The page request carries no sort, so there is no order to read the metadata back from: this search orders itself
	 * from its specification, because relevance is not a column. Both the parameters and the counters therefore go to
	 * the repository untouched — the trimming the counters used to need lives in the specifications now that they build
	 * the counters too.
	 */
	@Test
	void searchHandsTheParametersOnUntouchedAndPagesWithoutASort() {
		final var parameters = CombinedObjectParameters.create().withQuery("  Sundsvall  ").withLocation("   ");

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(PAGEABLE)))
			.thenReturn(new PageImpl<>(List.of(), PAGEABLE, 0));
		when(repositoryMock.countByType(parameters)).thenReturn(List.of());

		final var result = service.search(parameters);

		assertThat(result.getObjects()).isEmpty();
		assertThat(result.getTypeCounts()).isEmpty();

		final var searchCaptor = ArgumentCaptor.forClass(CombinedObjectParameters.class);
		verify(repositoryMock).findAllByParameters(searchCaptor.capture(), eq(PAGEABLE));
		assertThat(searchCaptor.getValue()).isSameAs(parameters);
		verify(repositoryMock).countByType(parameters);
	}

	/**
	 * The metadata reports the sort the caller asked for. Relevance and the id tiebreaker are orders the endpoint
	 * applies on its own, so a caller who asked for nothing is told nothing rather than being sent looking for a sort
	 * they never requested.
	 */
	@Test
	void metaDataReportsOnlyTheRequestedSort() {
		final var unsorted = CombinedObjectParameters.create().withQuery("Nordin");
		final var sorted = CombinedObjectParameters.create().withQuery("Nordin");
		sorted.setSortBy(List.of("title"));
		sorted.setSortDirection(DESC);

		when(repositoryMock.findAllByParameters(any(CombinedObjectParameters.class), eq(PAGEABLE)))
			.thenReturn(new PageImpl<>(List.of(), PAGEABLE, 0));
		when(repositoryMock.countByType(any(CombinedObjectParameters.class))).thenReturn(List.of());

		final var withoutSort = service.search(unsorted).getMetaData();
		final var withSort = service.search(sorted).getMetaData();

		assertThat(withoutSort.getSortBy()).isNull();
		assertThat(withoutSort.getSortDirection()).isNull();
		assertThat(withSort.getSortBy()).containsExactly("title");
		assertThat(withSort.getSortDirection()).isEqualTo(DESC);
	}
}
