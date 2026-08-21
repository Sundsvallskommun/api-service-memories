package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class NodeMapperTest {

	private static NodeEntity sampleEntity() {
		return NodeEntity.create()
			.withId(100)
			.withParentId(10)
			.withName("Sundsvalls stads arkiv")
			.withNodeType(NodeTypeEntity.create().withId(1).withName("Arkiv"))
			.withStartYear(1862)
			.withStopYear(1951)
			.withDescription("Handlingar från stadsfullmäktige")
			.withSortOrder(10)
			.withSubItemCount(42)
			.withPublishedSubItemCount(40)
			.withOptions(6);
	}

	@Test
	void toNode() {
		final var result = NodeMapper.toNode(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(100);
		assertThat(result.getParentId()).isEqualTo(10);
		assertThat(result.getName()).isEqualTo("Sundsvalls stads arkiv");
		assertThat(result.getNodeTypeId()).isEqualTo(1);
		assertThat(result.getNodeType()).isEqualTo("Arkiv");
		assertThat(result.getStartYear()).isEqualTo(1862);
		assertThat(result.getStopYear()).isEqualTo(1951);
		assertThat(result.getDescription()).isEqualTo("Handlingar från stadsfullmäktige");
		assertThat(result.getSortOrder()).isEqualTo(10);
		assertThat(result.getSubItemCount()).isEqualTo(42);
		assertThat(result.getPublishedSubItemCount()).isEqualTo(40);
		assertThat(result.getOptions()).isEqualTo(6);
	}

	/**
	 * NODETYPEID is declared NOT NULL, but it can still point at a type row that no longer exists — the schema carries
	 * no foreign key. Both the id and the name are then read as absent rather than blowing up.
	 */
	@Test
	void toNodeWithoutNodeType() {
		final var result = NodeMapper.toNode(NodeEntity.create().withId(100).withName("Lös nod"));

		assertThat(result).isNotNull();
		assertThat(result.getNodeTypeId()).isNull();
		assertThat(result.getNodeType()).isNull();
	}

	@Test
	void toNodeWhenNull() {
		assertThat(NodeMapper.toNode(null)).isNull();
	}

	@Test
	void toNodeList() {
		final var result = NodeMapper.toNodeList(List.of(sampleEntity(), NodeEntity.create().withId(200).withName("Fotosamlingen")));

		assertThat(result).hasSize(2)
			.extracting("id", "name")
			.containsExactly(tuple(100, "Sundsvalls stads arkiv"), tuple(200, "Fotosamlingen"));
	}

	@Test
	void toNodeListWhenNull() {
		assertThat(NodeMapper.toNodeList(null)).isEqualTo(emptyList());
	}

	@Test
	void toNodeDetail() {
		final var ancestors = List.of(
			NodeEntity.create().withId(100).withName("Sundsvalls stads arkiv"),
			NodeEntity.create().withId(110).withName("Protokoll"));

		final var result = NodeMapper.toNodeDetail(sampleEntity(), ancestors);

		assertThat(result).isNotNull();
		assertThat(result.getNode().getId()).isEqualTo(100);
		assertThat(result.getPath())
			.extracting("id", "name")
			.containsExactly(tuple(100, "Sundsvalls stads arkiv"), tuple(110, "Protokoll"));
	}

	/**
	 * A root node has nothing above it, which is an empty path rather than a missing one.
	 */
	@Test
	void toNodeDetailWithoutAncestors() {
		final var result = NodeMapper.toNodeDetail(sampleEntity(), null);

		assertThat(result).isNotNull();
		assertThat(result.getPath()).isEqualTo(emptyList());
	}

	@Test
	void toNodeDetailWhenNull() {
		assertThat(NodeMapper.toNodeDetail(null, List.of())).isNull();
	}
}
