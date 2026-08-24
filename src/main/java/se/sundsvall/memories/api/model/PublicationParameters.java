package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Schema(description = "Publication search parameters")
public class PublicationParameters extends AbstractSearchParameters {

	public static PublicationParameters create() {
		return new PublicationParameters();
	}

	@Override
	@Schema(description = "Free text search query", examples = "Drowning accident")
	public String getQuery() {
		return super.getQuery();
	}

	public PublicationParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	@Override
	@Schema(description = "Year from (inclusive), matched against the object's date", examples = "1970")
	public Integer getYearFrom() {
		return super.getYearFrom();
	}

	public PublicationParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	@Override
	@Schema(description = "Year to (inclusive), matched against the object's date", examples = "1990")
	public Integer getYearTo() {
		return super.getYearTo();
	}

	public PublicationParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	public String getLocation() {
		return super.getLocation();
	}

	public PublicationParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public PublicationParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public PublicationParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "documentTitle", allowableValues = {
		"documentTitle", "documentDate", "date", "id"
	}))
	public List<@Pattern(regexp = SortableProperties.PUBLICATION, message = SortableProperties.PUBLICATION_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}
}
