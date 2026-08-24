package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Archive and collection node search parameters. All filters are optional and combined with AND. "
	+ "Sort on one of: name, startYear, stopYear or sortOrder.")
public class NodeParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search (substring, case-insensitive) across name and description", examples = "stadsfullmäktige")
	private String query;

	@Schema(description = "Node type ID", examples = "1")
	private Integer nodeTypeId;

	@Schema(description = "Keep nodes whose period reaches this year or later (inclusive)", examples = "1862")
	private Integer yearFrom;

	@Schema(description = "Keep nodes whose period starts this year or earlier (inclusive)", examples = "1951")
	private Integer yearTo;

	public static NodeParameters create() {
		return new NodeParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public NodeParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	public Integer getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public NodeParameters withNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public NodeParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public NodeParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "name", allowableValues = {
		"name", "startYear", "stopYear", "sortOrder"
	}))
	public List<@Pattern(regexp = SortableProperties.NODE, message = SortableProperties.NODE_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}

	public NodeParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public NodeParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final NodeParameters that = (NodeParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(nodeTypeId, that.nodeTypeId) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, nodeTypeId, yearFrom, yearTo);
	}

	@Override
	public String toString() {
		return "NodeParameters{" +
			"query='" + query + '\'' +
			", nodeTypeId=" + nodeTypeId +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
