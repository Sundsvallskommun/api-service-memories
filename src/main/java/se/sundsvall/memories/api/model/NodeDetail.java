package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Archive or collection node with the path from the root down to it")
public class NodeDetail {

	@Schema(implementation = Node.class, accessMode = READ_ONLY)
	private Node node;

	@ArraySchema(arraySchema = @Schema(description = "Ancestors of the node, root first, excluding the node itself. Empty for a root node."),
		schema = @Schema(implementation = Node.class, accessMode = READ_ONLY))
	private List<Node> path;

	public static NodeDetail create() {
		return new NodeDetail();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(final Node node) {
		this.node = node;
	}

	public NodeDetail withNode(final Node node) {
		this.node = node;
		return this;
	}

	public List<Node> getPath() {
		return path;
	}

	public void setPath(final List<Node> path) {
		this.path = path;
	}

	public NodeDetail withPath(final List<Node> path) {
		this.path = path;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final NodeDetail that = (NodeDetail) o;
		return Objects.equals(node, that.node) && Objects.equals(path, that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(node, path);
	}

	@Override
	public String toString() {
		return "NodeDetail{" +
			"node=" + node +
			", path=" + path +
			'}';
	}
}
