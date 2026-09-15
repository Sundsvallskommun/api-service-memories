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
		arraySchema = @Schema(description = """
			Total number of matching objects per type (for chip counters), ordered by object type. The counts are \
			independent of the current page and of the objectType filter — every other filter applies, so a chip keeps saying how many objects \
			selecting that type would return."""))
	private List<ObjectTypeCount> typeCounts;

	@ArraySchema(schema = @Schema(implementation = GenderCount.class, accessMode = READ_ONLY),
		arraySchema = @Schema(description = """
			Total number of matching objects per gender (for chip counters), ordered by gender, over the rows that \
			record one. Gender is its own dimension — a row is both Person and man — so these overlap with typeCounts \
			rather than summing with them. The counts are independent of the current page and of the gender filter — \
			every other filter applies, so a chip keeps saying how many objects selecting that gender would return."""))
	private List<GenderCount> genderCounts;

	@ArraySchema(schema = @Schema(implementation = CategoryCount.class, accessMode = READ_ONLY),
		arraySchema = @Schema(description = """
			Total number of matching objects per originator category (for chip counters), ordered by category name, over \
			the rows whose originator is a categorised legal entity. A dimension of its own like genderCounts, so these \
			overlap with typeCounts rather than summing with them, and the register rows and objects without an \
			originator are not counted. The counts are independent of the current page and of the categoryId filter — \
			every other filter applies, so a chip keeps saying how many objects selecting that category would return."""))
	private List<CategoryCount> categoryCounts;

	@ArraySchema(arraySchema = @Schema(description = """
		How many matching objects are placed in each topography (place), by id and by the name /topographies lists it \
		under, sorted by name — for a place picker that can hide the empty places and show the numbers. Covers the rows \
		placed in a topography: the object types and the legal entities, not the person registers, which hold a parish as \
		free text. A dimension of its own like the other counters, so these overlap with typeCounts rather than summing \
		with them. The counts are independent of the current page and of the topographyId filter — every other filter \
		applies, the substring location filter included, so a chip keeps saying how many objects selecting that place \
		would return."""))
	private List<TopographyCount> topographyCounts;

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

	public List<GenderCount> getGenderCounts() {
		return genderCounts;
	}

	public void setGenderCounts(final List<GenderCount> genderCounts) {
		this.genderCounts = genderCounts;
	}

	public PagedCombinedObjectResponse withGenderCounts(final List<GenderCount> genderCounts) {
		this.genderCounts = genderCounts;
		return this;
	}

	public List<CategoryCount> getCategoryCounts() {
		return categoryCounts;
	}

	public void setCategoryCounts(final List<CategoryCount> categoryCounts) {
		this.categoryCounts = categoryCounts;
	}

	public PagedCombinedObjectResponse withCategoryCounts(final List<CategoryCount> categoryCounts) {
		this.categoryCounts = categoryCounts;
		return this;
	}

	public List<TopographyCount> getTopographyCounts() {
		return topographyCounts;
	}

	public void setTopographyCounts(final List<TopographyCount> topographyCounts) {
		this.topographyCounts = topographyCounts;
	}

	public PagedCombinedObjectResponse withTopographyCounts(final List<TopographyCount> topographyCounts) {
		this.topographyCounts = topographyCounts;
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
		return Objects.equals(objects, that.objects) && Objects.equals(typeCounts, that.typeCounts) && Objects.equals(genderCounts, that.genderCounts)
			&& Objects.equals(categoryCounts, that.categoryCounts) && Objects.equals(metaData, that.metaData) && Objects.equals(topographyCounts, that.topographyCounts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objects, typeCounts, genderCounts, categoryCounts, metaData, topographyCounts);
	}

	@Override
	public String toString() {
		return "PagedCombinedObjectResponse{" +
			"objects=" + objects +
			", typeCounts=" + typeCounts +
			", genderCounts=" + genderCounts +
			", categoryCounts=" + categoryCounts +
			", topographyCounts=" + topographyCounts +
			", metaData=" + metaData +
			'}';
	}
}
