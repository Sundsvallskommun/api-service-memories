package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingBase;

@Schema(description = "Publication search parameters")
public class PublicationParameters extends AbstractParameterPagingBase {

	@Schema(description = "Free text search query", examples = "Drunkningsolycka")
	private String query;

	public static PublicationParameters create() {
		return new PublicationParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public PublicationParameters withQuery(final String query) {
		this.query = query;
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
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final PublicationParameters that = (PublicationParameters) o;
		return Objects.equals(query, that.query);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query);
	}

	@Override
	public String toString() {
		return "PublicationParameters{" +
			"query='" + query + '\'' +
			", page=" + page +
			", limit=" + limit +
			'}';
	}
}
