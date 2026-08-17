package se.sundsvall.memories.api.model;

import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

/**
 * Search parameters shared by every searchable object type (audio, film, photo, publication and text): the free-text
 * {@code query}, the {@code yearFrom}/{@code yearTo} range and the {@code location} filter, on top of the dept44 paging
 * and sorting parameters.
 *
 * <p>
 * The query parameter names are defined here, but the OpenAPI documentation is not: every concrete subclass overrides
 * the four getters purely to carry its own {@code @Schema} annotation. The year filters in particular are backed by
 * different columns per object type (e.g. {@code TIDIG}/{@code SENAST} for photos, {@code DOKDATUM}/
 * {@code DOKDATUM_SLUT} for texts, {@code DATUM} for the rest), so the documentation has to stay type-specific even
 * though the parameter itself is shared.
 */
public abstract class AbstractSearchParameters extends AbstractParameterPagingAndSortingBase {

	protected String query;

	protected Integer yearFrom;

	protected Integer yearTo;

	protected String location;

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final AbstractSearchParameters that = (AbstractSearchParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, yearFrom, yearTo, location);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
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
