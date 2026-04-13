package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged film response")
public class PagedFilmResponse {

	@ArraySchema(schema = @Schema(implementation = Film.class, accessMode = READ_ONLY))
	private List<Film> films;

	@JsonProperty("_meta")
	@Schema(implementation = PagingMetaData.class, accessMode = READ_ONLY)
	private PagingMetaData metaData;

	public static PagedFilmResponse create() {
		return new PagedFilmResponse();
	}

	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(final List<Film> films) {
		this.films = films;
	}

	public PagedFilmResponse withFilms(final List<Film> films) {
		this.films = films;
		return this;
	}

	public PagingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedFilmResponse withMetaData(final PagingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedFilmResponse that = (PagedFilmResponse) o;
		return Objects.equals(films, that.films) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(films, metaData);
	}

	@Override
	public String toString() {
		return "PagedFilmResponse{" +
			"films=" + films +
			", metaData=" + metaData +
			'}';
	}
}
