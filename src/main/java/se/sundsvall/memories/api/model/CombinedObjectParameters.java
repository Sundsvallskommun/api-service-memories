package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = "Combined object search parameters (across all object and register types). All filters are optional "
	+ "and combined with AND, except that several values of objectType are alternatives. Sort on one of: relevance, objectKey, "
	+ "title, year or objectType. Defaults to relevance when a query is given.")
public class CombinedObjectParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Free text search (case-insensitive). Every word must occur somewhere in the title and comment, and for the register "
		+ "types also in names, parishes and other identifying fields, in any order. Results are ranked with title and name matches above matches "
		+ "that only occurred in a comment or a body text.", examples = "Anton Nordin")
	private String query;

	@Schema(description = "Year from (inclusive)", examples = "1900")
	private Integer yearFrom;

	@Schema(description = "Year to (inclusive)", examples = "1950")
	private Integer yearTo;

	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	private String location;

	/**
	 * The selection is deliberately not validated against a fixed list of types. The five object tables are unioned
	 * under a literal type each, but FOTO carries its own ({@code OBJTYP}, "Foto" or "Föremål"), so the set of values
	 * is the archive's rather than this API's — a whitelist here would refuse a type the response had just counted.
	 * An unrecognised value therefore matches nothing rather than failing the request, and {@code typeCounts} names
	 * every value that does match something.
	 */
	@ArraySchema(schema = @Schema(description = "Object type to include: Foto, Föremål, Film, Ljud, Text, Publikation, Person, "
		+ "Juridisk person or Sjöman. Repeat the parameter, or comma-separate the values, to select several — they are "
		+ "alternatives, so Foto and Ljud together means either. Omit to include every type. The values are the ones each "
		+ "object reports under objectType and typeCounts counts by, so a client can filter on exactly what it counted.",
		examples = "Foto"))
	private List<String> objectType;

	@Schema(description = "Originator (upphovsman) name (substring, case-insensitive; matches a person or a legal entity). Only object types carry an originator, so this filter also excludes the register types.", examples = "Nordin")
	private String creator;

	@Schema(description = "ID of the originator, when it is a person", examples = "1")
	private Integer creatorPersonId;

	@Schema(description = "ID of the originator, when it is a legal entity", examples = "10")
	private Integer creatorLegalEntityId;

	public static CombinedObjectParameters create() {
		return new CombinedObjectParameters();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public CombinedObjectParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public CombinedObjectParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public CombinedObjectParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public CombinedObjectParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public List<String> getObjectType() {
		return objectType;
	}

	public void setObjectType(final List<String> objectType) {
		this.objectType = objectType;
	}

	public CombinedObjectParameters withObjectType(final List<String> objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(final String creator) {
		this.creator = creator;
	}

	public Integer getCreatorPersonId() {
		return creatorPersonId;
	}

	public void setCreatorPersonId(final Integer creatorPersonId) {
		this.creatorPersonId = creatorPersonId;
	}

	public Integer getCreatorLegalEntityId() {
		return creatorLegalEntityId;
	}

	public void setCreatorLegalEntityId(final Integer creatorLegalEntityId) {
		this.creatorLegalEntityId = creatorLegalEntityId;
	}

	/**
	 * {@code relevance} is the exception among the accepted values: it is deliberately not an attribute of anything —
	 * nothing stores it, the specification computes it per request — while every other value is an attribute of
	 * {@link se.sundsvall.memories.integration.db.model.CombinedObjectEntity}. The whitelist is what keeps the two
	 * kinds apart, and an entity attribute it does not offer, such as the text the ranking itself reads, is rejected
	 * too.
	 */
	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on. 'relevance' ranks the best match first and is the default when a query is "
		+ "given; it is ignored without one. Sorting is ascending by default, which for relevance means most relevant first.",
		examples = "relevance",
		allowableValues = {
			"relevance", "objectKey", "title", "year", "objectType"
		}))
	public List<@Pattern(regexp = SortableProperties.COMBINED_OBJECT, message = SortableProperties.COMBINED_OBJECT_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}

	public CombinedObjectParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public CombinedObjectParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final CombinedObjectParameters that = (CombinedObjectParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(location, that.location)
			&& Objects.equals(objectType, that.objectType) && Objects.equals(creator, that.creator) && Objects.equals(creatorPersonId, that.creatorPersonId)
			&& Objects.equals(creatorLegalEntityId, that.creatorLegalEntityId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, yearFrom, yearTo, location, objectType, creator, creatorPersonId, creatorLegalEntityId);
	}

	@Override
	public String toString() {
		return "CombinedObjectParameters{" +
			"query='" + query + '\'' +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", location='" + location + '\'' +
			", objectType=" + objectType +
			", creator='" + creator + '\'' +
			", creatorPersonId=" + creatorPersonId +
			", creatorLegalEntityId=" + creatorLegalEntityId +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
