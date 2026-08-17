package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged seaman response")
public class PagedSeamanResponse {

	@ArraySchema(schema = @Schema(implementation = Seaman.class, accessMode = READ_ONLY))
	private List<Seaman> seamen;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedSeamanResponse create() {
		return new PagedSeamanResponse();
	}

	public List<Seaman> getSeamen() {
		return seamen;
	}

	public void setSeamen(final List<Seaman> seamen) {
		this.seamen = seamen;
	}

	public PagedSeamanResponse withSeamen(final List<Seaman> seamen) {
		this.seamen = seamen;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedSeamanResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedSeamanResponse that = (PagedSeamanResponse) o;
		return Objects.equals(seamen, that.seamen) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(seamen, metaData);
	}

	@Override
	public String toString() {
		return "PagedSeamanResponse{" +
			"seamen=" + seamen +
			", metaData=" + metaData +
			'}';
	}
}
