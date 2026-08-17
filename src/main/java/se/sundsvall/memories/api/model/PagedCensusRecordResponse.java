package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged census record response")
public class PagedCensusRecordResponse {

	@ArraySchema(schema = @Schema(implementation = CensusRecord.class, accessMode = READ_ONLY))
	private List<CensusRecord> censusRecords;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedCensusRecordResponse create() {
		return new PagedCensusRecordResponse();
	}

	public List<CensusRecord> getCensusRecords() {
		return censusRecords;
	}

	public void setCensusRecords(final List<CensusRecord> censusRecords) {
		this.censusRecords = censusRecords;
	}

	public PagedCensusRecordResponse withCensusRecords(final List<CensusRecord> censusRecords) {
		this.censusRecords = censusRecords;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedCensusRecordResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedCensusRecordResponse that = (PagedCensusRecordResponse) o;
		return Objects.equals(censusRecords, that.censusRecords) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(censusRecords, metaData);
	}

	@Override
	public String toString() {
		return "PagedCensusRecordResponse{" +
			"censusRecords=" + censusRecords +
			", metaData=" + metaData +
			'}';
	}
}
