package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;

@Schema(description = """
	Legal entity (juridisk person) search parameters. All filters are optional and combined with \
	AND. Sort on one of: name, startDate or endDate.""")
public class LegalEntityParameters extends AbstractParameterPagingAndSortingBase {

	@Schema(description = "Name (substring, case-insensitive; matches name or alternative names)", examples = "Nödhjälpskommittén")
	private String name;

	@Schema(description = "Location (substring, case-insensitive; matches resolved place name or free-text location)", examples = "Sundsvall")
	private String location;

	@Schema(description = "Category ID", examples = "5")
	private Integer categoryId;

	@Schema(description = "Activity period year from (inclusive)", examples = "1880")
	private Integer yearFrom;

	@Schema(description = "Activity period year to (inclusive)", examples = "1920")
	private Integer yearTo;

	@Schema(description = "Legal entities that are an archive creator", examples = "true")
	private Boolean archiveCreator;

	@Schema(description = "Only archive creators with at least one object in /objects, counting the archives they created, not the objects they are the creator of", examples = "true")
	private Boolean archiveCreatorWithObjects;

	public static LegalEntityParameters create() {
		return new LegalEntityParameters();
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public LegalEntityParameters withName(final String name) {
		this.name = name;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public LegalEntityParameters withLocation(final String location) {
		this.location = location;
		return this;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public LegalEntityParameters withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public Integer getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
	}

	public LegalEntityParameters withYearFrom(final Integer yearFrom) {
		this.yearFrom = yearFrom;
		return this;
	}

	public Integer getYearTo() {
		return yearTo;
	}

	public void setYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
	}

	public LegalEntityParameters withYearTo(final Integer yearTo) {
		this.yearTo = yearTo;
		return this;
	}

	public Boolean getArchiveCreator() {
		return archiveCreator;
	}

	public void setArchiveCreator(final Boolean archiveCreator) {
		this.archiveCreator = archiveCreator;
	}

	public LegalEntityParameters withArchiveCreator(final Boolean archiveCreator) {
		this.archiveCreator = archiveCreator;
		return this;
	}

	public Boolean getArchiveCreatorWithObjects() {
		return archiveCreatorWithObjects;
	}

	public void setArchiveCreatorWithObjects(final Boolean archiveCreatorWithObjects) {
		this.archiveCreatorWithObjects = archiveCreatorWithObjects;
	}

	public LegalEntityParameters withArchiveCreatorWithObjects(final Boolean archiveCreatorWithObjects) {
		this.archiveCreatorWithObjects = archiveCreatorWithObjects;
		return this;
	}

	public LegalEntityParameters withPage(final int page) {
		super.setPage(page);
		return this;
	}

	public LegalEntityParameters withLimit(final int limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	@ArraySchema(schema = @Schema(description = "Property to sort on", examples = "name", allowableValues = {
		"name", "startDate", "endDate", "legalEntityId"
	}))
	public List<@Pattern(regexp = SortableProperties.LEGAL_ENTITY, message = SortableProperties.LEGAL_ENTITY_MESSAGE) String> getSortBy() {
		return super.getSortBy();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		final LegalEntityParameters that = (LegalEntityParameters) o;
		return Objects.equals(name, that.name) && Objects.equals(location, that.location) && Objects.equals(categoryId, that.categoryId)
			&& Objects.equals(yearFrom, that.yearFrom) && Objects.equals(yearTo, that.yearTo) && Objects.equals(archiveCreator, that.archiveCreator)
			&& Objects.equals(archiveCreatorWithObjects, that.archiveCreatorWithObjects);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name, location, categoryId, yearFrom, yearTo, archiveCreator, archiveCreatorWithObjects);
	}

	@Override
	public String toString() {
		return "LegalEntityParameters{" +
			"name='" + name + '\'' +
			", location='" + location + '\'' +
			", categoryId=" + categoryId +
			", yearFrom=" + yearFrom +
			", yearTo=" + yearTo +
			", archiveCreator=" + archiveCreator +
			", archiveCreatorWithObjects=" + archiveCreatorWithObjects +
			", page=" + page +
			", limit=" + limit +
			", sortBy=" + sortBy +
			", sortDirection=" + sortDirection +
			'}';
	}
}
