package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity for the {@code TBL_NODES} table — the archive and collection tree (arkiv, serier, volymer, samlingar). Objects
 * point into it through their own {@code NODEID} column, so a node is what places a photo or a document in the archive
 * hierarchy.
 *
 * <p>
 * {@code PARENTID} is kept as a plain id rather than a self-association: the search only ever reports which node a hit
 * sits under, and walking the tree is done by id.
 */
@Entity
@Table(name = "TBL_NODES")
public class NodeEntity {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "PARENTID")
	private Integer parentId;

	@Column(name = "NAME", length = 1000)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NODETYPEID")
	private NodeTypeEntity nodeType;

	@Column(name = "STARTYEAR")
	private Integer startYear;

	@Column(name = "STOPYEAR")
	private Integer stopYear;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SORT")
	private Integer sortOrder;

	/**
	 * Number of items below the node, and of those, the number that are published. The published counter is
	 * {@code SUBITEMS_4}, named after bit 4 of {@code OPTIONS} — the same bit that marks a row as published everywhere
	 * else in this schema.
	 */
	@Column(name = "SUBITEMS")
	private Integer subItemCount;

	@Column(name = "SUBITEMS_4")
	private Integer publishedSubItemCount;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static NodeEntity create() {
		return new NodeEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public NodeEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(final Integer parentId) {
		this.parentId = parentId;
	}

	public NodeEntity withParentId(final Integer parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public NodeEntity withName(final String name) {
		this.name = name;
		return this;
	}

	public NodeTypeEntity getNodeType() {
		return nodeType;
	}

	public void setNodeType(final NodeTypeEntity nodeType) {
		this.nodeType = nodeType;
	}

	public NodeEntity withNodeType(final NodeTypeEntity nodeType) {
		this.nodeType = nodeType;
		return this;
	}

	public Integer getStartYear() {
		return startYear;
	}

	public void setStartYear(final Integer startYear) {
		this.startYear = startYear;
	}

	public NodeEntity withStartYear(final Integer startYear) {
		this.startYear = startYear;
		return this;
	}

	public Integer getStopYear() {
		return stopYear;
	}

	public void setStopYear(final Integer stopYear) {
		this.stopYear = stopYear;
	}

	public NodeEntity withStopYear(final Integer stopYear) {
		this.stopYear = stopYear;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public NodeEntity withDescription(final String description) {
		this.description = description;
		return this;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(final Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public NodeEntity withSortOrder(final Integer sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public Integer getSubItemCount() {
		return subItemCount;
	}

	public void setSubItemCount(final Integer subItemCount) {
		this.subItemCount = subItemCount;
	}

	public NodeEntity withSubItemCount(final Integer subItemCount) {
		this.subItemCount = subItemCount;
		return this;
	}

	public Integer getPublishedSubItemCount() {
		return publishedSubItemCount;
	}

	public void setPublishedSubItemCount(final Integer publishedSubItemCount) {
		this.publishedSubItemCount = publishedSubItemCount;
	}

	public NodeEntity withPublishedSubItemCount(final Integer publishedSubItemCount) {
		this.publishedSubItemCount = publishedSubItemCount;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public NodeEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public NodeEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final NodeEntity that = (NodeEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(name, that.name) && Objects.equals(nodeType, that.nodeType)
			&& Objects.equals(startYear, that.startYear) && Objects.equals(stopYear, that.stopYear) && Objects.equals(description, that.description)
			&& Objects.equals(sortOrder, that.sortOrder) && Objects.equals(subItemCount, that.subItemCount) && Objects.equals(publishedSubItemCount, that.publishedSubItemCount)
			&& Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, name, nodeType, startYear, stopYear, description, sortOrder, subItemCount, publishedSubItemCount, options, deletedDate);
	}

	@Override
	public String toString() {
		return "NodeEntity{" +
			"id=" + id +
			", parentId=" + parentId +
			", name='" + name + '\'' +
			", nodeType=" + nodeType +
			", startYear=" + startYear +
			", stopYear=" + stopYear +
			", description='" + description + '\'' +
			", sortOrder=" + sortOrder +
			", subItemCount=" + subItemCount +
			", publishedSubItemCount=" + publishedSubItemCount +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
