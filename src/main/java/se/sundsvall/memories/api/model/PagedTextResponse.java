package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged text response")
public class PagedTextResponse {

	@ArraySchema(schema = @Schema(implementation = Text.class, accessMode = READ_ONLY))
	private List<Text> texts;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedTextResponse create() {
		return new PagedTextResponse();
	}

	public List<Text> getTexts() {
		return texts;
	}

	public void setTexts(final List<Text> texts) {
		this.texts = texts;
	}

	public PagedTextResponse withTexts(final List<Text> texts) {
		this.texts = texts;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedTextResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedTextResponse that = (PagedTextResponse) o;
		return Objects.equals(texts, that.texts) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(texts, metaData);
	}

	@Override
	public String toString() {
		return "PagedTextResponse{" +
			"texts=" + texts +
			", metaData=" + metaData +
			'}';
	}
}
