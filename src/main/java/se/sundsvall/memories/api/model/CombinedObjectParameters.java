package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

@Schema(description = "Combined object search parameters (across all object and register types). All filters are optional "
	+ "and combined with AND. Sort on one of: objectKey, title, year or objectType.")
public class CombinedObjectParameters extends AbstractParameterPagingAndSortingBase {

	/**
	 * {@link #getSortBy()} feeds a specification, so a sort property is an attribute of {@link CombinedObjectEntity}
	 * rather than a column of the view. Restricting the accepted values here turns an unresolvable property into a
	 * {@code 400 Constraint Violation} that names the alternatives, instead of the {@code 500} it would otherwise
	 * cause.
	 */
	private static final String SORTABLE_PROPERTIES = "objectKey|title|year|objectType";

	private static final String SORTABLE_PROPERTIES_MESSAGE = "must be one of: objectKey, title, year, objectType";

	@Schema(description = "Free text search (substring, case-insensitive) across title and comment, and for the register types also across names, parishes and other identifying fields", examples = "Sundsvall")
	private String query;

	@Schema(description = "Year from (inclusive)", examples = "1900")
	private Integer yearFrom;

	@Schema(description = "Year to (inclusive)", examples = "1950")
	private Integer yearTo;

	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	private String location;

	public static CombinedObjectParameters create() {
		return new CombinedObjectParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public CombinedObjectParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public CombinedObjectParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public CombinedObjectParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public CombinedObjectParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "title", allowableValues = {
		"objectKey", "title", "year", "objectType"
	}))
	public List<@Pattern(regexp = SORTABLE_PROPERTIES, message = SORTABLE_PROPERTIES_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}

	public CombinedObjectParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public CombinedObjectParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final CombinedObjectParameters that = (CombinedObjectParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, yearFrom, yearTo, location);
	}

	@Override
	public String toString() {
		return "CombinedObjectParameters{" +
			"query='" + query + '\'' +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", location='" + location + '\'' +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
