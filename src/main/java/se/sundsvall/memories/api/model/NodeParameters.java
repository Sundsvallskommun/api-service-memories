package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = """
	Archive and collection node search parameters. All filters are optional and combined with AND, except that several \
	values of institutionId, categoryId and topographyId are alternatives. Sort on one of: name, startYear, stopYear, \
	sortOrder or location.""")
public class NodeParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search (substring, case-insensitive) across name, description and the name of the arkivbildare", examples = "stadsfullmäktige")
	private String query;

	@Schema(description = "Node type ID", examples = "2")
	private Integer nodeTypeId;

	@Schema(description = "Node type by name, matched case-insensitively — Arkiv for the archives and collections themselves, which is what a list of arkiv och samlingar shows, Serie or Volym for the levels under them", examples = "Arkiv")
	private String nodeType;

	@ArraySchema(schema = @Schema(description = """
		ID of the institution (arkivinstitution) holding the archive, as listed by /institutions. Repeat the parameter, \
		or comma-separate the values, to select several — they are alternatives.""", examples = "3"))
	private List<Integer> institutionId;

	@ArraySchema(schema = @Schema(description = """
		ID of the arkivbildare's category (verksamhetskategori), as listed by /categories. Repeat the parameter, or \
		comma-separate the values, to select several — they are alternatives. The sentinel every legal entity defaults \
		to is not a category, and naming it alone matches nothing.""", examples = "5"))
	private List<Integer> categoryId;

	@ArraySchema(schema = @Schema(description = """
		ID of the place (topografi) the archive is about or comes from, as listed by /topographies — the exact \
		counterpart of location, for a place picked from that list rather than typed. Repeat the parameter, or \
		comma-separate the values, to select several — they are alternatives.""", examples = "4"))
	private List<Integer> topographyId;

	@Schema(description = "Place (substring, case-insensitive; the resolved place name or the free-text place)", examples = "Njurunda")
	private String location;

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

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(final String nodeType) {
		this.nodeType = nodeType;
	}

	public NodeParameters withNodeType(final String nodeType) {
		this.nodeType = nodeType;
		return this;
	}

	public List<Integer> getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(final List<Integer> institutionId) {
		this.institutionId = institutionId;
	}

	public NodeParameters withInstitutionId(final List<Integer> institutionId) {
		this.institutionId = institutionId;
		return this;
	}

	public List<Integer> getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final List<Integer> categoryId) {
		this.categoryId = categoryId;
	}

	public NodeParameters withCategoryId(final List<Integer> categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public List<Integer> getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final List<Integer> topographyId) {
		this.topographyId = topographyId;
	}

	public NodeParameters withTopographyId(final List<Integer> topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public NodeParameters withLocation(final String location) {
		this.location = location;
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
	@ArraySchema(schema = @Schema(description = "Property to sort on. location orders by the place a node is placed in, nodes without one last whichever way the list runs", examples = "name", allowableValues = {
		"name", "startYear", "stopYear", "sortOrder", "location"
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
		return Objects.equals(query, that.query) && Objects.equals(nodeTypeId, that.nodeTypeId) && Objects.equals(nodeType, that.nodeType)
			&& Objects.equals(institutionId, that.institutionId) && Objects.equals(categoryId, that.categoryId) && Objects.equals(topographyId, that.topographyId)
			&& Objects.equals(location, that.location) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, nodeTypeId, nodeType, institutionId, categoryId, topographyId, location, yearFrom, yearTo);
	}

	@Override
	public String toString() {
		return "NodeParameters{" +
			"query='" + query + '\'' +
			", nodeTypeId=" + nodeTypeId +
			", nodeType='" + nodeType + '\'' +
			", institutionId=" + institutionId +
			", categoryId=" + categoryId +
			", topographyId=" + topographyId +
			", location='" + location + '\'' +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
