package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Photo search parameters")
public class PhotoParameters extends AbstractSearchParameters {

	@Schema(description = "Filter by object type. Use 'Foto' for photographs or 'Föremål' for physical objects. Omit to return both.", examples = "Foto")
	private String objectType;

	public static PhotoParameters create() {
		return new PhotoParameters();
	}

	@Override
	@Schema(description = "Free text search query", examples = "Sundsvall")
	public String getQuery() {
		return super.getQuery();
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

	@Override
	@Schema(description = "Year from (inclusive); matched against the photo's time period (TIDIG–SENAST)", examples = "1900")
	public Integer getYearFrom() {
		return super.getYearFrom();
	}

	public PhotoParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	@Override
	@Schema(description = "Year to (inclusive); matched against the photo's time period (TIDIG–SENAST)", examples = "1950")
	public Integer getYearTo() {
		return super.getYearTo();
	}

	public PhotoParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	public String getLocation() {
		return super.getLocation();
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
		if (!super.equals(o))
			return false;
		final PhotoParameters that = (PhotoParameters) o;
		return Objects.equals(objectType, that.objectType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), objectType);
	}

	@Override
	public String toString() {
		return "PhotoParameters{" +
			"query='" + query + '\'' +
			", objectType='" + objectType + '\'' +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", location='" + location + '\'' +
			", creator='" + creator + '\'' +
			", creatorPersonId=" + creatorPersonId +
			", creatorLegalEntityId=" + creatorLegalEntityId +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
