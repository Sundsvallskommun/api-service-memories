package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Legal entity (juridisk person) model")
public class LegalEntity {

	@Schema(description = "Legal entity ID", examples = "123")
	private Integer legalEntityId;

	@Schema(description = "Name", examples = "Nödhjälpskommittén 1888-1889")
	private String name;

	@Schema(description = "Alternative names", examples = "Nödhjälpskommittén")
	private String alternativeNames;

	@Schema(description = "Topography ID", examples = "1")
	private Integer topographyId;

	@Schema(description = "Free-text location (OPLATS)", examples = "Sundsvall")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over locationText when set)", examples = "Sundsvall")
	private String location;

	@Schema(description = "Start date (stored as free text)", examples = "1888")
	private String startDate;

	@Schema(description = "End date (stored as free text)", examples = "1889")
	private String endDate;

	@Schema(description = "Principal (huvudman)", examples = "Sundsvalls stad")
	private String principal;

	@Schema(description = "Comment", examples = "Bildad efter branden 1888")
	private String comment;

	@Schema(description = "History file reference (served via a dedicated file endpoint when available)", examples = "jurpers_123_historia.xml")
	private String historyFilename;

	@Schema(description = "Category ID", examples = "5")
	private Integer categoryId;

	@Schema(description = "Resolved category name from KATEGORI", examples = "Kommitté")
	private String category;

	@Schema(description = "Options", examples = "6")
	private Integer options;

	@Schema(description = "Deleted date", examples = "2026-01-15")
	private LocalDate deletedDate;

	public static LegalEntity create() {
		return new LegalEntity();
	}

	public Integer getLegalEntityId() {
		return legalEntityId;
	}

	public void setLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
	}

	public LegalEntity withLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public LegalEntity withName(final String name) {
		this.name = name;
		return this;
	}

	public String getAlternativeNames() {
		return alternativeNames;
	}

	public void setAlternativeNames(final String alternativeNames) {
		this.alternativeNames = alternativeNames;
	}

	public LegalEntity withAlternativeNames(final String alternativeNames) {
		this.alternativeNames = alternativeNames;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public LegalEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public LegalEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public LegalEntity withLocation(final String location) {
		this.location = location;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public LegalEntity withStartDate(final String startDate) {
		this.startDate = startDate;
		return this;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public LegalEntity withEndDate(final String endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(final String principal) {
		this.principal = principal;
	}

	public LegalEntity withPrincipal(final String principal) {
		this.principal = principal;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public LegalEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getHistoryFilename() {
		return historyFilename;
	}

	public void setHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
	}

	public LegalEntity withHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
		return this;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public LegalEntity withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public LegalEntity withCategory(final String category) {
		this.category = category;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public LegalEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public LegalEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final LegalEntity that = (LegalEntity) o;
		return Objects.equals(legalEntityId, that.legalEntityId) && Objects.equals(name, that.name) && Objects.equals(alternativeNames, that.alternativeNames)
			&& Objects.equals(topographyId, that.topographyId) && Objects.equals(locationText, that.locationText) && Objects.equals(location, that.location)
			&& Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(principal, that.principal)
			&& Objects.equals(comment, that.comment) && Objects.equals(historyFilename, that.historyFilename) && Objects.equals(categoryId, that.categoryId)
			&& Objects.equals(category, that.category) && Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(legalEntityId, name, alternativeNames, topographyId, locationText, location, startDate, endDate, principal, comment, historyFilename, categoryId, category, options,
			deletedDate);
	}

	@Override
	public String toString() {
		return "LegalEntity{" +
			"legalEntityId=" + legalEntityId +
			", name='" + name + '\'' +
			", alternativeNames='" + alternativeNames + '\'' +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			", startDate='" + startDate + '\'' +
			", endDate='" + endDate + '\'' +
			", principal='" + principal + '\'' +
			", comment='" + comment + '\'' +
			", historyFilename='" + historyFilename + '\'' +
			", categoryId=" + categoryId +
			", category='" + category + '\'' +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
