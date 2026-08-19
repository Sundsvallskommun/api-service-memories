package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;
import se.sundsvall.memories.integration.db.model.PersonEntity;

@Schema(description = "Person search parameters. All filters are optional and combined with AND. Sort on one of: "
	+ "lastName, firstName, birthDate or birthParish.")
public class PersonParameters extends AbstractParameterPagingAndSortingBase {

	/**
	 * {@link #getSortBy()} feeds a specification, so a sort property is an attribute of {@link PersonEntity} rather than
	 * a {@code PERSON} column. Anything else fails to resolve and comes back as a 500, hence the up-front validation.
	 */
	private static final String SORTABLE_PROPERTIES = "lastName|firstName|birthDate|birthParish";

	@Schema(description = "Last name (substring, case-insensitive)", examples = "Nordin")
	private String lastName;

	@Schema(description = "First name (substring, case-insensitive)", examples = "Anton")
	private String firstName;

	@Schema(description = "Birth parish (substring, case-insensitive)", examples = "Sundsvall")
	private String birthParish;

	@Schema(description = "Birth year from (inclusive)", examples = "1850")
	private Integer yearFrom;

	@Schema(description = "Birth year to (inclusive)", examples = "1900")
	private Integer yearTo;

	@Schema(description = "Gender (matched case-insensitively against the stored value)", examples = "man")
	private String gender;

	public static PersonParameters create() {
		return new PersonParameters();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public PersonParameters withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public PersonParameters withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getBirthParish() {
		return birthParish;
	}

	public void setBirthParish(final String birthParish) {
		this.birthParish = birthParish;
	}

	public PersonParameters withBirthParish(final String birthParish) {
		this.birthParish = birthParish;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public PersonParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public PersonParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public PersonParameters withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "lastName", allowableValues = {
		"lastName", "firstName", "birthDate", "birthParish"
	}))
	public List<@Pattern(regexp = SORTABLE_PROPERTIES, message = "must be one of: lastName, firstName, birthDate, birthParish") String> getSortBy() {
		return super.getSortBy();
	}

	public PersonParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public PersonParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final PersonParameters that = (PersonParameters) o;
		return Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName) && Objects.equals(birthParish, that.birthParish)
			&& Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(gender, that.gender);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), lastName, firstName, birthParish, yearFrom, yearTo, gender);
	}

	@Override
	public String toString() {
		return "PersonParameters{" +
			"lastName='" + lastName + '\'' +
			", firstName='" + firstName + '\'' +
			", birthParish='" + birthParish + '\'' +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", gender='" + gender + '\'' +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
