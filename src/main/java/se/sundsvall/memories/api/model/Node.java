package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Archive or collection node")
public class Node {

	@Schema(description = "Node ID", examples = "100")
	private Integer id;

	@Schema(description = "ID of the node this one sits under, or null for a root node", examples = "10")
	private Integer parentId;

	@Schema(description = "Node name", examples = "Sundsvalls stads arkiv")
	private String name;

	@Schema(description = "Node type ID", examples = "1")
	private Integer nodeTypeId;

	@Schema(description = "Resolved node type name (arkiv, serie, volym and so on)", examples = "Arkiv")
	private String nodeType;

	@Schema(description = "First year the node covers, or null when unknown", examples = "1862")
	private Integer startYear;

	@Schema(description = "Last year the node covers, or null when it has not ended", examples = "1951")
	private Integer stopYear;

	@Schema(description = "Description of what the node contains", examples = "Handlingar från stadsfullmäktige")
	private String description;

	@Schema(description = "Sort order among its siblings, as set in the archive", examples = "10")
	private Integer sortOrder;

	@Schema(description = "Number of items below the node", examples = "42")
	private Integer subItemCount;

	@Schema(description = "Number of published items below the node", examples = "40")
	private Integer publishedSubItemCount;

	@Schema(description = "Status bitmask; bit 4 marks the node as published", examples = "6")
	private Integer options;

	public static Node create() {
		return new Node();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Node withId(final Integer id) {
		this.id = id;
		return this;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(final Integer parentId) {
		this.parentId = parentId;
	}

	public Node withParentId(final Integer parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Node withName(final String name) {
		this.name = name;
		return this;
	}

	public Integer getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public Node withNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
		return this;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(final String nodeType) {
		this.nodeType = nodeType;
	}

	public Node withNodeType(final String nodeType) {
		this.nodeType = nodeType;
		return this;
	}

	public Integer getStartYear() {
		return startYear;
	}

	public void setStartYear(final Integer startYear) {
		this.startYear = startYear;
	}

	public Node withStartYear(final Integer startYear) {
		this.startYear = startYear;
		return this;
	}

	public Integer getStopYear() {
		return stopYear;
	}

	public void setStopYear(final Integer stopYear) {
		this.stopYear = stopYear;
	}

	public Node withStopYear(final Integer stopYear) {
		this.stopYear = stopYear;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Node withDescription(final String description) {
		this.description = description;
		return this;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(final Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Node withSortOrder(final Integer sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public Integer getSubItemCount() {
		return subItemCount;
	}

	public void setSubItemCount(final Integer subItemCount) {
		this.subItemCount = subItemCount;
	}

	public Node withSubItemCount(final Integer subItemCount) {
		this.subItemCount = subItemCount;
		return this;
	}

	public Integer getPublishedSubItemCount() {
		return publishedSubItemCount;
	}

	public void setPublishedSubItemCount(final Integer publishedSubItemCount) {
		this.publishedSubItemCount = publishedSubItemCount;
	}

	public Node withPublishedSubItemCount(final Integer publishedSubItemCount) {
		this.publishedSubItemCount = publishedSubItemCount;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public Node withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Node that = (Node) o;
		return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(name, that.name)
			&& Objects.equals(nodeTypeId, that.nodeTypeId) && Objects.equals(nodeType, that.nodeType)
			&& Objects.equals(startYear, that.startYear) && Objects.equals(stopYear, that.stopYear)
			&& Objects.equals(description, that.description) && Objects.equals(sortOrder, that.sortOrder)
			&& Objects.equals(subItemCount, that.subItemCount)
			&& Objects.equals(publishedSubItemCount, that.publishedSubItemCount) && Objects.equals(options, that.options);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, name, nodeTypeId, nodeType, startYear, stopYear, description, sortOrder, subItemCount, publishedSubItemCount, options);
	}

	@Override
	public String toString() {
		return "Node{" +
			"id=" + id +
			", parentId=" + parentId +
			", name=" + name +
			", nodeTypeId=" + nodeTypeId +
			", nodeType=" + nodeType +
			", startYear=" + startYear +
			", stopYear=" + stopYear +
			", description=" + description +
			", sortOrder=" + sortOrder +
			", subItemCount=" + subItemCount +
			", publishedSubItemCount=" + publishedSubItemCount +
			", options=" + options +
			'}';
	}
}
