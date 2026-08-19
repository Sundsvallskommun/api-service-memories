package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entity for the {@code TBL_NODETYPES} lookup table — the kind of archive node a row in {@code TBL_NODES} is (arkiv,
 * serie, volym and so on). Small and effectively static, and read only to give a node's type a name.
 */
@Entity
@Table(name = "TBL_NODETYPES")
public class NodeTypeEntity {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "PARENTID")
	private Integer parentId;

	@Column(name = "NAME", length = 100)
	private String name;

	public static NodeTypeEntity create() {
		return new NodeTypeEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public NodeTypeEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(final Integer parentId) {
		this.parentId = parentId;
	}

	public NodeTypeEntity withParentId(final Integer parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public NodeTypeEntity withName(final String name) {
		this.name = name;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final NodeTypeEntity that = (NodeTypeEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, name);
	}

	@Override
	public String toString() {
		return "NodeTypeEntity{" +
			"id=" + id +
			", parentId=" + parentId +
			", name='" + name + '\'' +
			'}';
	}
}
