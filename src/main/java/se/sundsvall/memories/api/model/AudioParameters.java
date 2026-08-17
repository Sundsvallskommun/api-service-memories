package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

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
}
