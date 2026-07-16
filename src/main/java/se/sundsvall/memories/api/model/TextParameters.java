package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Text search parameters")
public class TextParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search query", examples = "Stadshuset")
	private String query;

	@Schema(description = "Year from (inclusive); matched against the document's date period (DOKDATUM–DOKDATUM_SLUT)", examples = "1900")
	private Integer yearFrom;

	@Schema(description = "Year to (inclusive); matched against the document's date period (DOKDATUM–DOKDATUM_SLUT)", examples = "1950")
	private Integer yearTo;

	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	private String location;

	public static TextParameters create() {
		return new TextParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public TextParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public TextParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public TextParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public TextParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public TextParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public TextParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final TextParameters that = (TextParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, yearFrom, yearTo, location);
	}

	@Override
	public String toString() {
		return "TextParameters{" +
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
