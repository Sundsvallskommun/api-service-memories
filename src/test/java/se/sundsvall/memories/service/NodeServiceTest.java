package se.sundsvall.memories.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.integration.db.NodeRepository;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
		final var pageable = PageRequest.of(0, 100, Sort.by("id"));
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
		final var pageable = PageRequest.of(0, 100, Sort.by("id"));
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
	void getByIdReturnsTheNodeWithItsPathRootFirst() {
		final var node = node(111, 110);
		when(repositoryMock.findNodeById(111)).thenReturn(Optional.of(node));
		when(repositoryMock.findNodeById(110)).thenReturn(Optional.of(node(110, 100)));
		when(repositoryMock.findNodeById(100)).thenReturn(Optional.of(node(100, null)));

		final var result = service.getById(111);

		assertThat(result.getNode().getId()).isEqualTo(111);
		assertThat(result.getPath()).extracting("id").containsExactly(100, 110);
	}

	@Test
	void getByIdReturnsAnEmptyPathForARootNode() {
		when(repositoryMock.findNodeById(100)).thenReturn(Optional.of(node(100, null)));

		final var result = service.getById(100);

		assertThat(result.getPath()).isEmpty();
		verify(repositoryMock).findNodeById(100);
		verifyNoMoreInteractions(repositoryMock);
	}

	/**
	 * PARENTID carries no foreign key, so it can point at a node that does not exist. The walk stops there and returns
	 * the part of the path it could resolve.
	 */
	@Test
	void getByIdStopsAtAParentThatDoesNotExist() {
		when(repositoryMock.findNodeById(111)).thenReturn(Optional.of(node(111, 110)));
		when(repositoryMock.findNodeById(110)).thenReturn(Optional.empty());

		final var result = service.getById(111);

		assertThat(result.getPath()).isEmpty();
	}

	/**
	 * A cyclic parent chain in the data must not hang the request. The walk stops the first time it meets a node it has
	 * already seen.
	 */
	@Test
	void getByIdStopsOnACyclicParentChain() {
		when(repositoryMock.findNodeById(1)).thenReturn(Optional.of(node(1, 2)));
		when(repositoryMock.findNodeById(2)).thenReturn(Optional.of(node(2, 1)));

		final var result = service.getById(1);

		assertThat(result.getNode().getId()).isEqualTo(1);
		assertThat(result.getPath()).extracting("id").containsExactly(2);
	}

	@Test
	void getByIdNotFound() {
		when(repositoryMock.findNodeById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(999))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Node with id '999' not found");
	}

	/**
	 * Siblings default to the archive's own order, which is what SORT is for, with the name breaking its ties.
	 */
	@Test
	void searchChildrenFallsBackToTheArchiveOrder() {
		final var pageable = PageRequest.of(0, 100, Sort.by("sortOrder", "name").and(Sort.by("id")));
		when(repositoryMock.existsNodeById(100)).thenReturn(true);
		when(repositoryMock.findChildrenByParameters(eq(100), any(NodeParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(node(110, 100)), pageable, 1));

		final var result = service.searchChildren(100, NodeParameters.create());

		assertThat(result.getNodes()).extracting("id").containsExactly(110);
		verify(repositoryMock).findChildrenByParameters(eq(100), any(NodeParameters.class), eq(pageable));
	}

	@Test
	void searchChildrenKeepsARequestedSort() {
		final var requested = Sort.by(Sort.Direction.DESC, "name");
		final var pageable = PageRequest.of(0, 100, requested.and(Sort.by("id")));
		final var parameters = NodeParameters.create();
		parameters.setSortBy(List.of("name"));
		parameters.setSortDirection(Sort.Direction.DESC);

		when(repositoryMock.existsNodeById(100)).thenReturn(true);
		when(repositoryMock.findChildrenByParameters(eq(100), any(NodeParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		service.searchChildren(100, parameters);

		verify(repositoryMock).findChildrenByParameters(eq(100), any(NodeParameters.class), eq(pageable));
	}

	/**
	 * The children of a node that does not exist is a 404, not an empty page: the two mean different things to a client
	 * walking the tree.
	 */
	@Test
	void searchChildrenOfUnknownNode() {
		when(repositoryMock.existsNodeById(999)).thenReturn(false);

		final var parameters = NodeParameters.create();
		assertThatThrownBy(() -> service.searchChildren(999, parameters))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Node with id '999' not found");
		verify(repositoryMock).existsNodeById(999);
		verifyNoMoreInteractions(repositoryMock);
	}

	private static NodeEntity node(final Integer id, final Integer parentId) {
		return NodeEntity.create().withId(id).withParentId(parentId).withName("Nod " + id);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25, Sort.by("id"));

		when(repositoryMock.findAllByParameters(any(NodeParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(NodeEntity.create().withId(1)), pageable, 51));

		final var result = service.search(NodeParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
	}
}
