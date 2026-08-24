package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Schema(description = "Film search parameters")
public class FilmParameters extends AbstractSearchParameters {

	public static FilmParameters create() {
		return new FilmParameters();
	}

	@Override
	@Schema(description = "Free text search query", examples = "midsummer")
	public String getQuery() {
		return super.getQuery();
	}

	public FilmParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	@Override
	@Schema(description = "Year from (inclusive), matched against the object's date", examples = "1970")
	public Integer getYearFrom() {
		return super.getYearFrom();
	}

	public FilmParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	@Override
	@Schema(description = "Year to (inclusive), matched against the object's date", examples = "1990")
	public Integer getYearTo() {
		return super.getYearTo();
	}

	public FilmParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	public String getLocation() {
		return super.getLocation();
	}

	public FilmParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public FilmParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public FilmParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "documentTitle", allowableValues = {
		"documentTitle", "date", "id"
	}))
	public List<@Pattern(regexp = SortableProperties.FILM, message = SortableProperties.FILM_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}
}
