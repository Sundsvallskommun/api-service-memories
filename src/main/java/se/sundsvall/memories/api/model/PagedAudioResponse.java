package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged audio response")
public class PagedAudioResponse {

	@ArraySchema(schema = @Schema(implementation = Audio.class, accessMode = READ_ONLY))
	private List<Audio> audios;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedAudioResponse create() {
		return new PagedAudioResponse();
	}

	public List<Audio> getAudios() {
		return audios;
	}

	public void setAudios(final List<Audio> audios) {
		this.audios = audios;
	}

	public PagedAudioResponse withAudios(final List<Audio> audios) {
		this.audios = audios;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedAudioResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedAudioResponse that = (PagedAudioResponse) o;
		return Objects.equals(audios, that.audios) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(audios, metaData);
	}

	@Override
	public String toString() {
		return "PagedAudioResponse{" +
			"audios=" + audios +
			", metaData=" + metaData +
			'}';
	}
}
