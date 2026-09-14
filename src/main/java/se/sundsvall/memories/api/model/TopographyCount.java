package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * One chip counter. The place is given by id, which the filter accepts, and by the name {@code /topographies} lists
 * it under, so the chip can be labelled without a second call.
 */
@Schema(description = "How many objects placed in one topography the search matches")
public class TopographyCount {

	@Schema(description = "Topography ID, which the topographyId filter accepts", examples = "4")
	private Integer topographyId;

	@Schema(description = "The place's display name, the same one /topographies lists it under and an object reports as its location", examples = "Kvissleby, Njurunda")
	private String name;

	@Schema(
		description = "Number of matching objects placed in that topography, across every page. The counters cover every matched row placed in a topography, so they sum to fewer than totalRecords whenever the result also holds rows without one.",
		examples = "12")
	private Long count;

	public static TopographyCount create() {
		return new TopographyCount();
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public TopographyCount withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public TopographyCount withName(final String name) {
		this.name = name;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public TopographyCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TopographyCount that = (TopographyCount) o;
		return Objects.equals(topographyId, that.topographyId) && Objects.equals(name, that.name) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topographyId, name, count);
	}

	@Override
	public String toString() {
		return "TopographyCount{" +
			"topographyId=" + topographyId +
			", name='" + name + '\'' +
			", count=" + count +
			'}';
	}
}
