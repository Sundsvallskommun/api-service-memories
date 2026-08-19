package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged archive and collection node response")
public class PagedNodeResponse {

	@ArraySchema(schema = @Schema(implementation = Node.class, accessMode = READ_ONLY))
	private List<Node> nodes;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedNodeResponse create() {
		return new PagedNodeResponse();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(final List<Node> nodes) {
		this.nodes = nodes;
	}

	public PagedNodeResponse withNodes(final List<Node> nodes) {
		this.nodes = nodes;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedNodeResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedNodeResponse that = (PagedNodeResponse) o;
		return Objects.equals(nodes, that.nodes) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes, metaData);
	}

	@Override
	public String toString() {
		return "PagedNodeResponse{" +
			"nodes=" + nodes +
			", metaData=" + metaData +
			'}';
	}
}
