package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Combined object search parameters (across all object types). All filters are optional and "
	+ "combined with AND. Sort on a view column: TITLE, SORT_YEAR or OBJECT_TYPE.")
public class CombinedObjectParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search (substring, case-insensitive) across title and comment", examples = "Sundsvall")
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
