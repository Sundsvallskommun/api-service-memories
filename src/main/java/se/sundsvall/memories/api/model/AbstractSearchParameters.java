package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

/**
 * Search parameters shared by every searchable object type (audio, film, photo, publication and text): the free-text
 * {@code query}, the {@code yearFrom}/{@code yearTo} range and the {@code location} filter, on top of the dept44 paging
 * and sorting parameters.
 *
 * <p>
 * The query parameter names are defined here, but the OpenAPI documentation is not: every concrete subclass overrides
 * the four getters purely to carry its own {@code @Schema} annotation. The year filters in particular are backed by
 * different columns per object type (e.g. {@code TIDIG}/{@code SENAST} for photos, {@code DOKDATUM}/
 * {@code DOKDATUM_SLUT} for texts, {@code DATUM} for the rest), so the documentation has to stay type-specific even
 * though the parameter itself is shared.
 */
public abstract class AbstractSearchParameters extends AbstractParameterPagingAndSortingBase {

	protected String query;

	protected Integer yearFrom;

	protected Integer yearTo;

	protected String location;

	protected String creator;

	protected Integer creatorPersonId;

	protected Integer creatorLegalEntityId;

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	/**
	 * The originator filters are documented here rather than in every subclass: unlike the year filters they mean the
	 * same thing for every object type, since all five carry the originator in the same pair of columns.
	 */
	@Schema(description = "Originator (upphovsman) name (substring, case-insensitive; matches a person or a legal entity)", examples = "Nordin")
	public String getCreator() {
		return creator;
	}

	public void setCreator(final String creator) {
		this.creator = creator;
	}

	@Schema(description = "ID of the originator, when it is a person", examples = "1")
	public Integer getCreatorPersonId() {
		return creatorPersonId;
	}

	public void setCreatorPersonId(final Integer creatorPersonId) {
		this.creatorPersonId = creatorPersonId;
	}

	@Schema(description = "ID of the originator, when it is a legal entity", examples = "10")
	public Integer getCreatorLegalEntityId() {
		return creatorLegalEntityId;
	}

	public void setCreatorLegalEntityId(final Integer creatorLegalEntityId) {
		this.creatorLegalEntityId = creatorLegalEntityId;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final AbstractSearchParameters that = (AbstractSearchParameters) o;
		return Objects.equals(query, that.query) && Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(location, that.location)
			&& Objects.equals(creator, that.creator) && Objects.equals(creatorPersonId, that.creatorPersonId) && Objects.equals(creatorLegalEntityId, that.creatorLegalEntityId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), query, yearFrom, yearTo, location, creator, creatorPersonId, creatorLegalEntityId);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
			"query='" + query + '\'' +
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
