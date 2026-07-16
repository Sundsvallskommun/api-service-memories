package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "JURPERS")
public class LegalEntityEntity {

	@Id
	@Column(name = "J_ID")
	private Integer legalEntityId;

	@Column(name = "JURPERS", length = 256)
	private String name;

	@Column(name = "ALTNAMN", length = 256)
	private String alternativeNames;

	@Column(name = "T_ID")
	private Integer topographyId;

	@Column(name = "OPLATS", length = 64)
	private String locationText;

	@Column(name = "STARTDATUM", length = 10)
	private String startDate;

	@Column(name = "SLUTDATUM", length = 10)
	private String endDate;

	@Column(name = "HUVUDMAN", length = 256)
	private String principal;

	@Column(name = "KOMMENT_JURPERS", length = 4000)
	private String comment;

	@Column(name = "HISTORIA", length = 256)
	private String historyFilename;

	@Column(name = "KAT_ID")
	private Integer categoryId;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static LegalEntityEntity create() {
		return new LegalEntityEntity();
	}

	public Integer getLegalEntityId() {
		return legalEntityId;
	}

	public void setLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
	}

	public LegalEntityEntity withLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public LegalEntityEntity withName(final String name) {
		this.name = name;
		return this;
	}

	public String getAlternativeNames() {
		return alternativeNames;
	}

	public void setAlternativeNames(final String alternativeNames) {
		this.alternativeNames = alternativeNames;
	}

	public LegalEntityEntity withAlternativeNames(final String alternativeNames) {
		this.alternativeNames = alternativeNames;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public LegalEntityEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public LegalEntityEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public LegalEntityEntity withStartDate(final String startDate) {
		this.startDate = startDate;
		return this;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public LegalEntityEntity withEndDate(final String endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(final String principal) {
		this.principal = principal;
	}

	public LegalEntityEntity withPrincipal(final String principal) {
		this.principal = principal;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public LegalEntityEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getHistoryFilename() {
		return historyFilename;
	}

	public void setHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
	}

	public LegalEntityEntity withHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
		return this;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public LegalEntityEntity withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public LegalEntityEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public LegalEntityEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final LegalEntityEntity that = (LegalEntityEntity) o;
		return Objects.equals(legalEntityId, that.legalEntityId) && Objects.equals(name, that.name) && Objects.equals(alternativeNames, that.alternativeNames)
			&& Objects.equals(topographyId, that.topographyId) && Objects.equals(locationText, that.locationText) && Objects.equals(startDate, that.startDate)
			&& Objects.equals(endDate, that.endDate) && Objects.equals(principal, that.principal) && Objects.equals(comment, that.comment)
			&& Objects.equals(historyFilename, that.historyFilename) && Objects.equals(categoryId, that.categoryId) && Objects.equals(options, that.options)
			&& Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(legalEntityId, name, alternativeNames, topographyId, locationText, startDate, endDate, principal, comment, historyFilename, categoryId, options, deletedDate);
	}

	@Override
	public String toString() {
		return "LegalEntityEntity{" +
			"legalEntityId=" + legalEntityId +
			", name='" + name + '\'' +
			", alternativeNames='" + alternativeNames + '\'' +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", startDate='" + startDate + '\'' +
			", endDate='" + endDate + '\'' +
			", principal='" + principal + '\'' +
			", comment='" + comment + '\'' +
			", historyFilename='" + historyFilename + '\'' +
			", categoryId=" + categoryId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
