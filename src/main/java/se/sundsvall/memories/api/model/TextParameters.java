package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Schema(description = "Text search parameters")
public class TextParameters extends AbstractSearchParameters {

	public static TextParameters create() {
		return new TextParameters();
	}

	@Override
	@Schema(description = "Free text search query", examples = "Stadshuset")
	public String getQuery() {
		return super.getQuery();
	}

	public TextParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	@Override
	@Schema(description = "Year from (inclusive); matched against the document's date period (DOKDATUM–DOKDATUM_SLUT)", examples = "1900")
	public Integer getYearFrom() {
		return super.getYearFrom();
	}

	public TextParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	@Override
	@Schema(description = "Year to (inclusive); matched against the document's date period (DOKDATUM–DOKDATUM_SLUT)", examples = "1950")
	public Integer getYearTo() {
		return super.getYearTo();
	}

	public TextParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	public String getLocation() {
		return super.getLocation();
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
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "documentTitle", allowableValues = {
		"documentTitle", "documentDate", "id"
	}))
	public List<@Pattern(regexp = SortableProperties.TEXT, message = SortableProperties.TEXT_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}
}
