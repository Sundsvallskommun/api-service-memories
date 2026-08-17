package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Seaman (sjömanshus) search parameters. All filters are optional and combined with AND. Sort on a "
	+ "physical column: EFTERNAMN1, FORNAMN, FODDAT or FODFORS.")
public class SeamanParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Last name (substring, case-insensitive; matches either surname column)", examples = "Nordin")
	private String lastName;

	@Schema(description = "First name (substring, case-insensitive)", examples = "Anton")
	private String firstName;

	@Schema(description = "Birth parish (substring, case-insensitive)", examples = "Sundsvall")
	private String birthParish;

	@Schema(description = "Birth year from (inclusive)", examples = "1850")
	private Integer yearFrom;

	@Schema(description = "Birth year to (inclusive)", examples = "1900")
	private Integer yearTo;

	public static SeamanParameters create() {
		return new SeamanParameters();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public SeamanParameters withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public SeamanParameters withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getBirthParish() {
		return birthParish;
	}

	public void setBirthParish(final String birthParish) {
		this.birthParish = birthParish;
	}

	public SeamanParameters withBirthParish(final String birthParish) {
		this.birthParish = birthParish;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public SeamanParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public SeamanParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public SeamanParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public SeamanParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final SeamanParameters that = (SeamanParameters) o;
		return Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName) && Objects.equals(birthParish, that.birthParish)
			&& Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), lastName, firstName, birthParish, yearFrom, yearTo);
	}

	@Override
	public String toString() {
		return "SeamanParameters{" +
			"lastName='" + lastName + '\'' +
			", firstName='" + firstName + '\'' +
			", birthParish='" + birthParish + '\'' +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
