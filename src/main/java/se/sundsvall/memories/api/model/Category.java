package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Category (verksamhetskategori) model")
public class Category {

	@Schema(description = "Category ID", examples = "5")
	private Integer categoryId;

	@Schema(description = "Category code", examples = "FÖR")
	private String code;

	@Schema(description = "Category name", examples = "Förening")
	private String name;

	public static Category create() {
		return new Category();
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Category withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public Category withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Category withName(final String name) {
		this.name = name;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Category that = (Category) o;
		return Objects.equals(categoryId, that.categoryId) && Objects.equals(code, that.code) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoryId, code, name);
	}

	@Override
	public String toString() {
		return "Category{" +
			"categoryId=" + categoryId +
			", code='" + code + '\'' +
			", name='" + name + '\'' +
			'}';
	}
}
