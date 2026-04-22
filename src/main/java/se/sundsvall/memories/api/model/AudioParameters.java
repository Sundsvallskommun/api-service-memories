package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Audio search parameters")
public class AudioParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search query", examples = "interview")
	private String query;

	public static AudioParameters create() {
		return new AudioParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public AudioParameters withQuery(final String query) {
		this.query = query;
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

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final AudioParameters that = (AudioParameters) o;
		return Objects.equals(query, that.query);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query);
	}

	@Override
	public String toString() {
		return "AudioParameters{" +
			"query='" + query + '\'' +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
