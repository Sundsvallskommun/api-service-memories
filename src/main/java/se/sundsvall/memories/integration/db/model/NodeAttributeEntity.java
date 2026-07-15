package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity for the {@code TBL_NODEATTRIBUTES} table. The generic {@code FIELDn} columns are mapped to semantic names per
 * the schema comments: FIELD1→JURPERS, FIELD2→PERSON, FIELD3→INSTITUTION, FIELD4→KATEGORI, FIELD5→TOPOGRAFI,
 * FIELD9→OCM; the remaining fields hold volume/series/holdings metadata.
 */
@Entity
@Table(name = "TBL_NODEATTRIBUTES")
public class NodeAttributeEntity {

	@Id
	@Column(name = "NODEID")
	private Integer nodeId;

	@Column(name = "FIELD1")
	private Integer legalEntityId;

	@Column(name = "FIELD2")
	private Integer personId;

	@Column(name = "FIELD3")
	private Integer institutionId;

	@Column(name = "FIELD4")
	private Integer categoryId;

	@Column(name = "FIELD5")
	private Integer topographyId;

	@Column(name = "FIELD6", length = 100)
	private String volumeNumber;

	@Column(name = "FIELD7")
	private BigDecimal shelfMeters;

	@Column(name = "FIELD8")
	private Integer volumeCount;

	@Column(name = "FIELD9")
	private Integer subjectId;

	@Column(name = "FIELD10", length = 100)
	private String oldSeriesSignum;

	@Column(name = "FIELD11", length = 100)
	private String newSeriesSignum;

	@Column(name = "FIELD12", length = 100)
	private String accessionNumber;

	@Column(name = "FIELD13", length = 100)
	private String holdingsCode;

	@Column(name = "FIELD14", length = 100)
	private String undeterminedPlace;

	@Column(name = "FIELD15", length = 100)
	private String volumePlacement;

	@Column(name = "SUBITEMS")
	private Integer subItems;

	@Column(name = "FIELD16", length = 256)
	private String blobFilename;

	@Column(name = "FIELD16FORMAT", length = 4)
	private String blobFormat;

	public static NodeAttributeEntity create() {
		return new NodeAttributeEntity();
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public NodeAttributeEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getLegalEntityId() {
		return legalEntityId;
	}

	public void setLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
	}

	public NodeAttributeEntity withLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
		return this;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(final Integer personId) {
		this.personId = personId;
	}

	public NodeAttributeEntity withPersonId(final Integer personId) {
		this.personId = personId;
		return this;
	}

	public Integer getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
	}

	public NodeAttributeEntity withInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
		return this;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public NodeAttributeEntity withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public NodeAttributeEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getVolumeNumber() {
		return volumeNumber;
	}

	public void setVolumeNumber(final String volumeNumber) {
		this.volumeNumber = volumeNumber;
	}

	public NodeAttributeEntity withVolumeNumber(final String volumeNumber) {
		this.volumeNumber = volumeNumber;
		return this;
	}

	public BigDecimal getShelfMeters() {
		return shelfMeters;
	}

	public void setShelfMeters(final BigDecimal shelfMeters) {
		this.shelfMeters = shelfMeters;
	}

	public NodeAttributeEntity withShelfMeters(final BigDecimal shelfMeters) {
		this.shelfMeters = shelfMeters;
		return this;
	}

	public Integer getVolumeCount() {
		return volumeCount;
	}

	public void setVolumeCount(final Integer volumeCount) {
		this.volumeCount = volumeCount;
	}

	public NodeAttributeEntity withVolumeCount(final Integer volumeCount) {
		this.volumeCount = volumeCount;
		return this;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
	}

	public NodeAttributeEntity withSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	public String getOldSeriesSignum() {
		return oldSeriesSignum;
	}

	public void setOldSeriesSignum(final String oldSeriesSignum) {
		this.oldSeriesSignum = oldSeriesSignum;
	}

	public NodeAttributeEntity withOldSeriesSignum(final String oldSeriesSignum) {
		this.oldSeriesSignum = oldSeriesSignum;
		return this;
	}

	public String getNewSeriesSignum() {
		return newSeriesSignum;
	}

	public void setNewSeriesSignum(final String newSeriesSignum) {
		this.newSeriesSignum = newSeriesSignum;
	}

	public NodeAttributeEntity withNewSeriesSignum(final String newSeriesSignum) {
		this.newSeriesSignum = newSeriesSignum;
		return this;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public NodeAttributeEntity withAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
		return this;
	}

	public String getHoldingsCode() {
		return holdingsCode;
	}

	public void setHoldingsCode(final String holdingsCode) {
		this.holdingsCode = holdingsCode;
	}

	public NodeAttributeEntity withHoldingsCode(final String holdingsCode) {
		this.holdingsCode = holdingsCode;
		return this;
	}

	public String getUndeterminedPlace() {
		return undeterminedPlace;
	}

	public void setUndeterminedPlace(final String undeterminedPlace) {
		this.undeterminedPlace = undeterminedPlace;
	}

	public NodeAttributeEntity withUndeterminedPlace(final String undeterminedPlace) {
		this.undeterminedPlace = undeterminedPlace;
		return this;
	}

	public String getVolumePlacement() {
		return volumePlacement;
	}

	public void setVolumePlacement(final String volumePlacement) {
		this.volumePlacement = volumePlacement;
	}

	public NodeAttributeEntity withVolumePlacement(final String volumePlacement) {
		this.volumePlacement = volumePlacement;
		return this;
	}

	public Integer getSubItems() {
		return subItems;
	}

	public void setSubItems(final Integer subItems) {
		this.subItems = subItems;
	}

	public NodeAttributeEntity withSubItems(final Integer subItems) {
		this.subItems = subItems;
		return this;
	}

	public String getBlobFilename() {
		return blobFilename;
	}

	public void setBlobFilename(final String blobFilename) {
		this.blobFilename = blobFilename;
	}

	public NodeAttributeEntity withBlobFilename(final String blobFilename) {
		this.blobFilename = blobFilename;
		return this;
	}

	public String getBlobFormat() {
		return blobFormat;
	}

	public void setBlobFormat(final String blobFormat) {
		this.blobFormat = blobFormat;
	}

	public NodeAttributeEntity withBlobFormat(final String blobFormat) {
		this.blobFormat = blobFormat;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final NodeAttributeEntity that = (NodeAttributeEntity) o;
		return Objects.equals(nodeId, that.nodeId) && Objects.equals(legalEntityId, that.legalEntityId) && Objects.equals(personId, that.personId)
			&& Objects.equals(institutionId, that.institutionId) && Objects.equals(categoryId, that.categoryId) && Objects.equals(topographyId, that.topographyId)
			&& Objects.equals(volumeNumber, that.volumeNumber) && Objects.equals(shelfMeters, that.shelfMeters) && Objects.equals(volumeCount, that.volumeCount)
			&& Objects.equals(subjectId, that.subjectId) && Objects.equals(oldSeriesSignum, that.oldSeriesSignum) && Objects.equals(newSeriesSignum, that.newSeriesSignum)
			&& Objects.equals(accessionNumber, that.accessionNumber) && Objects.equals(holdingsCode, that.holdingsCode) && Objects.equals(undeterminedPlace, that.undeterminedPlace)
			&& Objects.equals(volumePlacement, that.volumePlacement) && Objects.equals(subItems, that.subItems) && Objects.equals(blobFilename, that.blobFilename)
			&& Objects.equals(blobFormat, that.blobFormat);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeId, legalEntityId, personId, institutionId, categoryId, topographyId, volumeNumber, shelfMeters, volumeCount, subjectId, oldSeriesSignum, newSeriesSignum,
			accessionNumber, holdingsCode, undeterminedPlace, volumePlacement, subItems, blobFilename, blobFormat);
	}

	@Override
	public String toString() {
		return "NodeAttributeEntity{" +
			"nodeId=" + nodeId +
			", legalEntityId=" + legalEntityId +
			", personId=" + personId +
			", institutionId=" + institutionId +
			", categoryId=" + categoryId +
			", topographyId=" + topographyId +
			", volumeNumber='" + volumeNumber + '\'' +
			", shelfMeters=" + shelfMeters +
			", volumeCount=" + volumeCount +
			", subjectId=" + subjectId +
			", oldSeriesSignum='" + oldSeriesSignum + '\'' +
			", newSeriesSignum='" + newSeriesSignum + '\'' +
			", accessionNumber='" + accessionNumber + '\'' +
			", holdingsCode='" + holdingsCode + '\'' +
			", undeterminedPlace='" + undeterminedPlace + '\'' +
			", volumePlacement='" + volumePlacement + '\'' +
			", subItems=" + subItems +
			", blobFilename='" + blobFilename + '\'' +
			", blobFormat='" + blobFormat + '\'' +
			'}';
	}
}
