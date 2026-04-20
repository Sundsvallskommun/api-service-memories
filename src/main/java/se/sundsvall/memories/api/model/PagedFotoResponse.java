package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged photo response")
public class PagedFotoResponse {

	@ArraySchema(schema = @Schema(implementation = Foto.class, accessMode = READ_ONLY))
	private List<Foto> photos;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedFotoResponse create() {
		return new PagedFotoResponse();
	}

	public List<Foto> getPhotos() {
		return photos;
	}

	public void setPhotos(final List<Foto> photos) {
		this.photos = photos;
	}

	public PagedFotoResponse withPhotos(final List<Foto> photos) {
		this.photos = photos;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedFotoResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedFotoResponse that = (PagedFotoResponse) o;
		return Objects.equals(photos, that.photos) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(photos, metaData);
	}

	@Override
	public String toString() {
		return "PagedFotoResponse{" +
			"photos=" + photos +
			", metaData=" + metaData +
			'}';
	}
}
