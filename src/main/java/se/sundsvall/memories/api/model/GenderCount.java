package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * One chip counter. The gender is a value rather than a JSON key, which keeps the archive's spelling out of the keys.
 */
@Schema(description = "How many objects recording one gender the search matches")
public class GenderCount {

	@Schema(description = "Gender — the stored value, which the gender filter accepts", examples = "man")
	private String gender;

	@Schema(description = "Number of matching objects recording that gender, across every page", examples = "12")
	private Long count;

	public static GenderCount create() {
		return new GenderCount();
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public GenderCount withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public GenderCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final GenderCount that = (GenderCount) o;
		return Objects.equals(gender, that.gender) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gender, count);
	}

	@Override
	public String toString() {
		return "GenderCount{" +
			"gender='" + gender + '\'' +
			", count=" + count +
			'}';
	}
}
