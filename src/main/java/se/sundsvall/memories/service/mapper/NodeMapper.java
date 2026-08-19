package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Node;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class NodeMapper {

	private NodeMapper() {}

	/**
	 * Map a single {@link NodeEntity} to a {@link Node} API model. The node type is read through the association, which
	 * is {@code null} both when the node has no type and when {@code NODETYPEID} points at a row that does not exist.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Node}, or {@code null} if {@code entity} is null
	 */
	public static Node toNode(final NodeEntity entity) {
		return ofNullable(entity)
			.map(e -> Node.create()
				.withId(e.getId())
				.withParentId(e.getParentId())
				.withName(e.getName())
				.withNodeTypeId(nodeTypeId(e))
				.withNodeType(nodeTypeName(e))
				.withStartYear(e.getStartYear())
				.withStopYear(e.getStopYear())
				.withDescription(e.getDescription())
				.withSortOrder(e.getSortOrder())
				.withSubItemCount(e.getSubItemCount())
				.withPublishedSubItemCount(e.getPublishedSubItemCount())
				.withOptions(e.getOptions()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link NodeEntity} objects to {@link Node} API models.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Node} objects (empty if {@code entities} is null)
	 */
	public static List<Node> toNodeList(final List<NodeEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(NodeMapper::toNode)
			.toList();
	}

	private static Integer nodeTypeId(final NodeEntity entity) {
		return ofNullable(entity.getNodeType())
			.map(NodeTypeEntity::getId)
			.orElse(null);
	}

	private static String nodeTypeName(final NodeEntity entity) {
		return ofNullable(entity.getNodeType())
			.map(NodeTypeEntity::getName)
			.orElse(null);
	}
}
