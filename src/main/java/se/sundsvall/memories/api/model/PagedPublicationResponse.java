package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged publication response")
public class PagedPublicationResponse {

	@ArraySchema(schema = @Schema(implementation = Publication.class, accessMode = READ_ONLY))
	private List<Publication> publications;

	@JsonProperty("_meta")
	@Schema(implementation = PagingMetaData.class, accessMode = READ_ONLY)
	private PagingMetaData metaData;

	public static PagedPublicationResponse create() {
		return new PagedPublicationResponse();
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public void setPublications(final List<Publication> publications) {
		this.publications = publications;
	}

	public PagedPublicationResponse withPublications(final List<Publication> publications) {
		this.publications = publications;
		return this;
	}

	public PagingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedPublicationResponse withMetaData(final PagingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedPublicationResponse that = (PagedPublicationResponse) o;
		return Objects.equals(publications, that.publications) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publications, metaData);
	}

	@Override
	public String toString() {
		return "PagedPublicationResponse{" +
			"publications=" + publications +
			", metaData=" + metaData +
			'}';
	}
}
