package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged legal entity response")
public class PagedLegalEntityResponse {

	@ArraySchema(schema = @Schema(implementation = LegalEntity.class, accessMode = READ_ONLY))
	private List<LegalEntity> legalEntities;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedLegalEntityResponse create() {
		return new PagedLegalEntityResponse();
	}

	public List<LegalEntity> getLegalEntities() {
		return legalEntities;
	}

	public void setLegalEntities(final List<LegalEntity> legalEntities) {
		this.legalEntities = legalEntities;
	}

	public PagedLegalEntityResponse withLegalEntities(final List<LegalEntity> legalEntities) {
		this.legalEntities = legalEntities;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedLegalEntityResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedLegalEntityResponse that = (PagedLegalEntityResponse) o;
		return Objects.equals(legalEntities, that.legalEntities) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(legalEntities, metaData);
	}

	@Override
	public String toString() {
		return "PagedLegalEntityResponse{" +
			"legalEntities=" + legalEntities +
			", metaData=" + metaData +
			'}';
	}
}
