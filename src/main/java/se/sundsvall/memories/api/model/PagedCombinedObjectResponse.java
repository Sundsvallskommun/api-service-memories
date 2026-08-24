package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged combined object response")
public class PagedCombinedObjectResponse {

	@ArraySchema(schema = @Schema(implementation = CombinedObject.class, accessMode = READ_ONLY))
	private List<CombinedObject> objects;

	@ArraySchema(schema = @Schema(implementation = ObjectTypeCount.class, accessMode = READ_ONLY),
		arraySchema = @Schema(description = "Total number of matching objects per type (for chip counters), ordered by object type. The counts are "
			+ "independent of the current page and of the objectType filter — every other filter applies, so a chip keeps saying how many objects "
			+ "selecting that type would return."))
	private List<ObjectTypeCount> typeCounts;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedCombinedObjectResponse create() {
		return new PagedCombinedObjectResponse();
	}

	public List<CombinedObject> getObjects() {
		return objects;
	}

	public void setObjects(final List<CombinedObject> objects) {
		this.objects = objects;
	}

	public PagedCombinedObjectResponse withObjects(final List<CombinedObject> objects) {
		this.objects = objects;
		return this;
	}

	public List<ObjectTypeCount> getTypeCounts() {
		return typeCounts;
	}

	public void setTypeCounts(final List<ObjectTypeCount> typeCounts) {
		this.typeCounts = typeCounts;
	}

	public PagedCombinedObjectResponse withTypeCounts(final List<ObjectTypeCount> typeCounts) {
		this.typeCounts = typeCounts;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedCombinedObjectResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedCombinedObjectResponse that = (PagedCombinedObjectResponse) o;
		return Objects.equals(objects, that.objects) && Objects.equals(typeCounts, that.typeCounts) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objects, typeCounts, metaData);
	}

	@Override
	public String toString() {
		return "PagedCombinedObjectResponse{" +
			"objects=" + objects +
			", typeCounts=" + typeCounts +
			", metaData=" + metaData +
			'}';
	}
}
