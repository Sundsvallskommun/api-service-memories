package se.sundsvall.memories.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.NodeDetail;
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.api.model.PagedNodeResponse;
import se.sundsvall.memories.integration.db.NodeRepository;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.service.mapper.NodeMapper;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class NodeService {

	private static final String NODE_NOT_FOUND = "Node with id '%s' not found";

	/**
	 * Siblings are shown in the order the archive itself sets, which is what {@code SORT} is for. Name breaks the ties
	 * that leaves — the column is not unique, and a page boundary in an arbitrary order would drop or repeat rows.
	 */
	private static final Sort CHILD_ORDER = Sort.by("sortOrder", "name");

	/**
	 * Depth cap for the walk up the tree. The archive nests a handful of levels; anything deeper means the data is
	 * cyclic, and a cycle that revisits a node is caught before this cap is ever reached.
	 */
	private static final int MAX_DEPTH = 50;

	private final NodeRepository nodeRepository;

	public NodeService(final NodeRepository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	@Transactional(readOnly = true)
	public PagedNodeResponse search(final NodeParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		return toResponse(nodeRepository.findAllByParameters(parameters, pageable));
	}

	/**
	 * The children of one node. Unlike the free search this falls back to the archive's own order rather than to no
	 * order at all, so a tree renders the way the archive is arranged.
	 */
	@Transactional(readOnly = true)
	public PagedNodeResponse searchChildren(final Integer parentId, final NodeParameters parameters) {
		final var requestedSort = parameters.sort();
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), requestedSort.isSorted() ? requestedSort : CHILD_ORDER);

		nodeRepository.findDetailById(parentId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, NODE_NOT_FOUND.formatted(parentId)));

		return toResponse(nodeRepository.findChildrenByParameters(parentId, parameters, pageable));
	}

	/**
	 * A node with the path from the root down to it, for a breadcrumb.
	 */
	@Transactional(readOnly = true)
	public NodeDetail getById(final Integer id) {
		final var node = nodeRepository.findDetailById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, NODE_NOT_FOUND.formatted(id)));

		return NodeMapper.toNodeDetail(node, ancestorsOf(node));
	}

	/**
	 * Walks up the tree from a node, collecting its ancestors root first. The walk stops on a node that has no parent,
	 * on a parent that does not exist — {@code PARENTID} carries no foreign key — and on a node already seen, which is
	 * what a cyclic parent chain in the legacy data would look like.
	 */
	private List<NodeEntity> ancestorsOf(final NodeEntity node) {
		final var ancestors = new ArrayList<NodeEntity>();
		final var visited = new HashSet<Integer>();
		visited.add(node.getId());

		var parentId = node.getParentId();
		while (isNode(parentId) && visited.add(parentId) && ancestors.size() < MAX_DEPTH) {
			final var parent = nodeRepository.findDetailById(parentId).orElse(null);
			if (parent == null) {
				break;
			}
			ancestors.add(parent);
			parentId = parent.getParentId();
		}

		return ancestors.reversed();
	}

	/**
	 * A root node has no parent, which the legacy schema writes as both {@code NULL} and {@code 0}.
	 */
	private static boolean isNode(final Integer id) {
		return id != null && id != 0;
	}

	private static PagedNodeResponse toResponse(final Page<NodeEntity> page) {
		return PagedNodeResponse.create()
			.withNodes(NodeMapper.toNodeList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}
}
