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
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.integration.db.NodeRepository;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// Which rows the filters select is verified against a real database in NodeSpecificationTest. These tests cover what
// the service itself does: build the pageable, forward the parameters, and map the resulting page.
@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

	@Mock
	private NodeRepository repositoryMock;

	@InjectMocks
	private NodeService service;

	@Test
	void searchDelegatesAndMaps() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = NodeParameters.create()
			.withQuery("stadsfullmäktige")
			.withNodeTypeId(1)
			.withYearFrom(1862)
			.withYearTo(1951)
			.withPage(1)
			.withLimit(100);
		final var entity = NodeEntity.create()
			.withId(100)
			.withName("Sundsvalls stads arkiv")
			.withNodeType(NodeTypeEntity.create().withId(1).withName("Arkiv"));

		when(repositoryMock.findAllByParameters(any(NodeParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(parameters);

		assertThat(result.getNodes()).hasSize(1);
		assertThat(result.getNodes().getFirst().getName()).isEqualTo("Sundsvalls stads arkiv");
		assertThat(result.getNodes().getFirst().getNodeType()).isEqualTo("Arkiv");
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).findAllByParameters(any(NodeParameters.class), eq(pageable));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = NodeParameters.create().withQuery("  arkiv  ");

		when(repositoryMock.findAllByParameters(any(NodeParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getNodes()).isEmpty();
		// Trimming and the "blank means no filter" rule live in the specifications, verified against a real database in
		// NodeSpecificationTest. The service only has to hand the parameters over untouched.
		final var parametersCaptor = ArgumentCaptor.forClass(NodeParameters.class);
		verify(repositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25);

		when(repositoryMock.findAllByParameters(any(NodeParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(NodeEntity.create().withId(1)), pageable, 51));

		final var result = service.search(NodeParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
	}
}
