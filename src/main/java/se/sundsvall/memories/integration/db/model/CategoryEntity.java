package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entity for the {@code KATEGORI} lookup table (verksamhetskategorier). Small and effectively static; used by legal
 * entities and archive nodes and exposed via the {@code /categories} dropdown endpoint.
 */
@Entity
@Table(name = "KATEGORI")
public class CategoryEntity {

	@Id
	@Column(name = "KAT_ID")
	private Integer categoryId;

	@Column(name = "KATKOD", length = 7)
	private String code;

	@Column(name = "KATNAMN", length = 60)
	private String name;

	public static CategoryEntity create() {
		return new CategoryEntity();
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public CategoryEntity withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public CategoryEntity withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public CategoryEntity withName(final String name) {
		this.name = name;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CategoryEntity that = (CategoryEntity) o;
		return Objects.equals(categoryId, that.categoryId) && Objects.equals(code, that.code) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoryId, code, name);
	}

	@Override
	public String toString() {
		return "CategoryEntity{" +
			"categoryId=" + categoryId +
			", code='" + code + '\'' +
			", name='" + name + '\'' +
			'}';
	}
}
