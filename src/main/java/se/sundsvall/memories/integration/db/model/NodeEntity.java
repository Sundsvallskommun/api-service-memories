package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity for the {@code TBL_NODES} archive-structure table (arkiv/serie/volym). Self-referential via {@code PARENTID};
 * the node type is given by {@code NODETYPEID} (see {@link NodeTypeEntity}). Publish status lives in the
 * {@code OPTIONS}
 * bitmask: bit {@code 4} = active/published, bit {@code 1024} = deleted.
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

	@Column(name = "NODETYPEID")
	private Integer nodeTypeId;

	@Column(name = "RUID")
	private Integer ruId;

	@Column(name = "STARTYEAR")
	private Integer startYear;

	@Column(name = "STOPYEAR")
	private Integer stopYear;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "SORT")
	private Integer sort;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SUBITEMS")
	private Integer subItems;

	@Column(name = "SUBITEMS_4")
	private Integer subItems4;

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

	public Integer getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public NodeEntity withNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
		return this;
	}

	public Integer getRuId() {
		return ruId;
	}

	public void setRuId(final Integer ruId) {
		this.ruId = ruId;
	}

	public NodeEntity withRuId(final Integer ruId) {
		this.ruId = ruId;
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

	public Integer getSort() {
		return sort;
	}

	public void setSort(final Integer sort) {
		this.sort = sort;
	}

	public NodeEntity withSort(final Integer sort) {
		this.sort = sort;
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

	public Integer getSubItems() {
		return subItems;
	}

	public void setSubItems(final Integer subItems) {
		this.subItems = subItems;
	}

	public NodeEntity withSubItems(final Integer subItems) {
		this.subItems = subItems;
		return this;
	}

	public Integer getSubItems4() {
		return subItems4;
	}

	public void setSubItems4(final Integer subItems4) {
		this.subItems4 = subItems4;
	}

	public NodeEntity withSubItems4(final Integer subItems4) {
		this.subItems4 = subItems4;
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
		return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(name, that.name) && Objects.equals(nodeTypeId, that.nodeTypeId)
			&& Objects.equals(ruId, that.ruId) && Objects.equals(startYear, that.startYear) && Objects.equals(stopYear, that.stopYear) && Objects.equals(options, that.options)
			&& Objects.equals(sort, that.sort) && Objects.equals(description, that.description) && Objects.equals(subItems, that.subItems) && Objects.equals(subItems4, that.subItems4)
			&& Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, name, nodeTypeId, ruId, startYear, stopYear, options, sort, description, subItems, subItems4, deletedDate);
	}

	@Override
	public String toString() {
		return "NodeEntity{" +
			"id=" + id +
			", parentId=" + parentId +
			", name='" + name + '\'' +
			", nodeTypeId=" + nodeTypeId +
			", ruId=" + ruId +
			", startYear=" + startYear +
			", stopYear=" + stopYear +
			", options=" + options +
			", sort=" + sort +
			", description='" + description + '\'' +
			", subItems=" + subItems +
			", subItems4=" + subItems4 +
			", deletedDate=" + deletedDate +
			'}';
	}
}
