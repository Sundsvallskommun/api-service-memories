package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

@Schema(description = "Photo search parameters")
public class PhotoParameters extends AbstractSearchParameters {

	/**
	 * The selection is deliberately not validated against a fixed list of types: FOTO carries its own {@code OBJTYP},
	 * so the set of values is the archive's rather than this API's. An unrecognised value therefore matches nothing
	 * rather than failing the request. The combined {@code /objects} search takes the same parameter, spelled the same
	 * way, so a client can move a chip row between the two.
	 */
	@ArraySchema(schema = @Schema(description = "Object type to include: 'Foto' for photographs or 'Föremål' for physical objects. "
		+ "Repeat the parameter, or comma-separate the values, to select several — they are alternatives, so Foto and Föremål "
		+ "together means either. Omit to return both.", examples = "Foto"))
	private List<String> objectType;

	public static PhotoParameters create() {
		return new PhotoParameters();
	}

	@Override
	@Schema(description = "Free text search query", examples = "Sundsvall")
	public String getQuery() {
		return super.getQuery();
	}

	public PhotoParameters withQuery(final String query) {
		this.query = query;
		return this;
	}

	public List<String> getObjectType() {
		return objectType;
	}

	public void setObjectType(final List<String> objectType) {
		this.objectType = objectType;
	}

	public PhotoParameters withObjectType(final List<String> objectType) {
		this.objectType = objectType;
		return this;
	}

	@Override
	@Schema(description = "Year from (inclusive); matched against the photo's time period (TIDIG–SENAST)", examples = "1900")
	public Integer getYearFrom() {
		return super.getYearFrom();
	}

	public PhotoParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	@Override
	@Schema(description = "Year to (inclusive); matched against the photo's time period (TIDIG–SENAST)", examples = "1950")
	public Integer getYearTo() {
		return super.getYearTo();
	}

	public PhotoParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	@Override
	@Schema(description = "Location (substring, case-insensitive; resolved place name or free-text location)", examples = "Sundsvall")
	public String getLocation() {
		return super.getLocation();
	}

	public PhotoParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public PhotoParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public PhotoParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	/**
	 * {@link #getSortBy()} feeds a specification, so a sort property is an attribute of the entity rather than a
	 * column of the table. Restricting the accepted values here turns an unresolvable property into a
	 * {@code 400 Constraint Violation} that names the alternatives, instead of the {@code 500} it would otherwise
	 * cause once it reached Spring Data.
	 */
	private static final String SORTABLE_PROPERTIES = "documentTitle|earliest|latest|objectType|id";

	private static final String SORTABLE_PROPERTIES_MESSAGE = "must be one of: documentTitle, earliest, latest, objectType, id";

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "documentTitle", allowableValues = {
		"documentTitle", "earliest", "latest", "objectType", "id"
	}))
	public List<@Pattern(regexp = SORTABLE_PROPERTIES, message = SORTABLE_PROPERTIES_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}

	@Override
	public boolean equals(final Object o) {
		if (!super.equals(o))
			return false;
		final PhotoParameters that = (PhotoParameters) o;
		return Objects.equals(objectType, that.objectType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), objectType);
	}

	@Override
	public String toString() {
		return "PhotoParameters{" +
			"query='" + query + '\'' +
			", objectType=" + objectType +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", location='" + location + '\'' +
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
