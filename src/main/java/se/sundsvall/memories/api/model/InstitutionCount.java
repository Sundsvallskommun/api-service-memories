package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * One chip counter. The institution is given by id, which the filter accepts, and by name, so the chip can be
 * labelled without a second call to {@code /institutions}.
 */
@Schema(description = "How many objects held by one institution the search matches")
public class InstitutionCount {

	@Schema(description = "Institution ID, which the institutionId filter accepts", examples = "3")
	private Integer institutionId;

	@Schema(description = "Institution name", examples = "Föreningsarkivet Västernorrland")
	private String name;

	@Schema(
		description = "Number of matching objects held by that institution, across every page. A photo or object counts under its own institution when it has one, otherwise under its archive's. The counters cover every matched row with an institution, so they sum to fewer than totalRecords whenever the result also holds rows without one, such as the registers.",
		examples = "12")
	private Long count;

	public static InstitutionCount create() {
		return new InstitutionCount();
	}

	public Integer getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
	}

	public InstitutionCount withInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public InstitutionCount withName(final String name) {
		this.name = name;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public InstitutionCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final InstitutionCount that = (InstitutionCount) o;
		return Objects.equals(institutionId, that.institutionId) && Objects.equals(name, that.name) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(institutionId, name, count);
	}

	@Override
	public String toString() {
		return "InstitutionCount{" +
			"institutionId=" + institutionId +
			", name='" + name + '\'' +
			", count=" + count +
			'}';
	}
}
