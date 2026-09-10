package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * One chip counter. The category is given by id, which the filter accepts, and by name, so the chip can be labelled
 * without a second call to {@code /categories}.
 */
@Schema(description = "How many objects with an originator in one category the search matches")
public class CategoryCount {

	@Schema(description = "Category ID, which the categoryId filter accepts", examples = "5")
	private Integer categoryId;

	@Schema(description = "Category name", examples = "Förening")
	private String name;

	@Schema(
		description = "Number of matching objects whose originator is a legal entity in that category, across every page. The counters cover every matched row with a categorised originator, so they sum to fewer than totalRecords whenever the result also holds rows without one.",
		examples = "12")
	private Long count;

	public static CategoryCount create() {
		return new CategoryCount();
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public CategoryCount withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public CategoryCount withName(final String name) {
		this.name = name;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public CategoryCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CategoryCount that = (CategoryCount) o;
		return Objects.equals(categoryId, that.categoryId) && Objects.equals(name, that.name) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoryId, name, count);
	}

	@Override
	public String toString() {
		return "CategoryCount{" +
			"categoryId=" + categoryId +
			", name='" + name + '\'' +
			", count=" + count +
			'}';
	}
}
