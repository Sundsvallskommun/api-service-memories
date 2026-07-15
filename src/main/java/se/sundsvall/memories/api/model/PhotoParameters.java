package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Photo search parameters")
public class PhotoParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search query", examples = "Sundsvall")
	private String query;

	@Schema(description = "Filter by object type. Use 'Foto' for photographs or 'Föremål' for physical objects. Omit to return both.", examples = "Foto")
	private String objectType;

	@Schema(description = "Year from (inclusive); matched against the photo's time period (TIDIG–SENAST)", examples = "1900")
	private Integer yearFrom;

	@Schema(description = "Year to (inclusive); matched against the photo's time period (TIDIG–SENAST)", examples = "1950")
	private Integer yearTo;

	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	private String location;

	public static PhotoParameters create() {
		return new PhotoParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public PhotoParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public PhotoParameters withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public PhotoParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public PhotoParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public PhotoParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public PhotoParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public PhotoParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final PhotoParameters that = (PhotoParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(objectType, that.objectType) && Objects.equals(yearFrom, that.yearFrom)
			&& Objects.equals(yearTo, that.yearTo) && Objects.equals(location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, objectType, yearFrom, yearTo, location);
	}

	@Override
	public String toString() {
		return "PhotoParameters{" +
			"query='" + query + '\'' +
			", objectType='" + objectType + '\'' +
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
