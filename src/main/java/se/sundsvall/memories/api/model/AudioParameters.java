package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Schema(description = "Audio search parameters")
public class AudioParameters extends AbstractSearchParameters {

	public static AudioParameters create() {
		return new AudioParameters();
	}

	@Override
	@Schema(description = "Free text search query", examples = "interview")
	public String getQuery() {
		return super.getQuery();
	}

	public AudioParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	@Override
	@Schema(description = "Year from (inclusive), matched against the object's date", examples = "1970")
	public Integer getYearFrom() {
		return super.getYearFrom();
	}

	public AudioParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	@Override
	@Schema(description = "Year to (inclusive), matched against the object's date", examples = "1990")
	public Integer getYearTo() {
		return super.getYearTo();
	}

	public AudioParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	public String getLocation() {
		return super.getLocation();
	}

	public AudioParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public AudioParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public AudioParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	/**
	 * {@link #getSortBy()} feeds a specification, so a sort property is an attribute of the entity rather than a
	 * column of the table. Restricting the accepted values here turns an unresolvable property into a
	 * {@code 400 Constraint Violation} that names the alternatives, instead of the {@code 500} it would otherwise
	 * cause once it reached Spring Data.
	 */
	private static final String SORTABLE_PROPERTIES = "documentTitle|date|id";

	private static final String SORTABLE_PROPERTIES_MESSAGE = "must be one of: documentTitle, date, id";

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "documentTitle", allowableValues = {
		"documentTitle", "date", "id"
	}))
	public List<@Pattern(regexp = SORTABLE_PROPERTIES, message = SORTABLE_PROPERTIES_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}
}
