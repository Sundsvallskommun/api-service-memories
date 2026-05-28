package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "PUBL")
public class PublicationEntity {

	@Id
	@Column(name = "P_ID")
	private Integer publicationId;

	@Column(name = "FILNAMN", length = 256)
	private String filename;

	@Column(name = "PUBLIKTYP", length = 40)
	private String publicationType;

	@Column(name = "DATUM", length = 10)
	private String date;

	@Column(name = "TIDTITEL", length = 128)
	private String periodicalTitle;

	@Column(name = "TIDNR", length = 5)
	private String issueNumber;

	@Column(name = "TIDSIDA", length = 3)
	private String pageNumber;

	@Column(name = "BF_J_ID")
	private Integer bfJId;

	@Column(name = "FORLAG_T_ID")
	private Integer publisherTopographyId;

	@Column(name = "FORLAG_OPLATS", length = 64)
	private String publisherLocation;

	@Column(name = "DOKDATUM", length = 10)
	private String documentDate;

	@Column(name = "DOKTITEL", length = 256)
	private String documentTitle;

	@Column(name = "F_E_ID")
	private Integer feId;

	@Column(name = "R_E_ID")
	private Integer reId;

	@Column(name = "U_J_ID")
	private Integer ujId;

	@Column(name = "U_E_ID")
	private Integer ueId;

	@Column(name = "P_T_ID")
	private Integer publicationTypeId;

	@Column(name = "P_OPLATS", length = 64)
	private String locationText;

	@Column(name = "ME_O_ID")
	private Integer meOId;

	@Column(name = "KOMMENT_PUBL", length = 4000)
	private String comment;

	@Column(name = "FIL_LITEN", length = 256)
	private String thumbnailFilename;

	@Column(name = "FIL_STOR", length = 256)
	private String largeImageFilename;

	@Column(name = "FIL_ORIGINAL", length = 256)
	private String originalFilename;

	@Column(name = "FIL_TXT", length = 256)
	private String ocrFilename;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "XMLTEXT", columnDefinition = "longtext")
	private String xmltext;

	@Column(name = "FIL_XTRA", length = 256)
	private String filXtra;

	@Column(name = "NODEID")
	private Integer nodeId;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "FIL_FORMAT", length = 4)
	private String filFormat;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static PublicationEntity create() {
		return new PublicationEntity();
	}

	public Integer getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(final Integer publicationId) {
		this.publicationId = publicationId;
	}

	public PublicationEntity withPublicationId(final Integer publicationId) {
		this.publicationId = publicationId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public PublicationEntity withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(final String publicationType) {
		this.publicationType = publicationType;
	}

	public PublicationEntity withPublicationType(final String publicationType) {
		this.publicationType = publicationType;
		return this;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public PublicationEntity withDate(final String date) {
		this.date = date;
		return this;
	}

	public String getPeriodicalTitle() {
		return periodicalTitle;
	}

	public void setPeriodicalTitle(final String periodicalTitle) {
		this.periodicalTitle = periodicalTitle;
	}

	public PublicationEntity withPeriodicalTitle(final String periodicalTitle) {
		this.periodicalTitle = periodicalTitle;
		return this;
	}

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
	}

	public PublicationEntity withIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
		return this;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(final String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PublicationEntity withPageNumber(final String pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}

	public Integer getBfJId() {
		return bfJId;
	}

	public void setBfJId(final Integer bfJId) {
		this.bfJId = bfJId;
	}

	public PublicationEntity withBfJId(final Integer bfJId) {
		this.bfJId = bfJId;
		return this;
	}

	public Integer getPublisherTopographyId() {
		return publisherTopographyId;
	}

	public void setPublisherTopographyId(final Integer publisherTopographyId) {
		this.publisherTopographyId = publisherTopographyId;
	}

	public PublicationEntity withPublisherTopographyId(final Integer publisherTopographyId) {
		this.publisherTopographyId = publisherTopographyId;
		return this;
	}

	public String getPublisherLocation() {
		return publisherLocation;
	}

	public void setPublisherLocation(final String publisherLocation) {
		this.publisherLocation = publisherLocation;
	}

	public PublicationEntity withPublisherLocation(final String publisherLocation) {
		this.publisherLocation = publisherLocation;
		return this;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(final String documentDate) {
		this.documentDate = documentDate;
	}

	public PublicationEntity withDocumentDate(final String documentDate) {
		this.documentDate = documentDate;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public PublicationEntity withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public Integer getFeId() {
		return feId;
	}

	public void setFeId(final Integer feId) {
		this.feId = feId;
	}

	public PublicationEntity withFeId(final Integer feId) {
		this.feId = feId;
		return this;
	}

	public Integer getReId() {
		return reId;
	}

	public void setReId(final Integer reId) {
		this.reId = reId;
	}

	public PublicationEntity withReId(final Integer reId) {
		this.reId = reId;
		return this;
	}

	public Integer getUjId() {
		return ujId;
	}

	public void setUjId(final Integer ujId) {
		this.ujId = ujId;
	}

	public PublicationEntity withUjId(final Integer ujId) {
		this.ujId = ujId;
		return this;
	}

	public Integer getUeId() {
		return ueId;
	}

	public void setUeId(final Integer ueId) {
		this.ueId = ueId;
	}

	public PublicationEntity withUeId(final Integer ueId) {
		this.ueId = ueId;
		return this;
	}

	public Integer getPublicationTypeId() {
		return publicationTypeId;
	}

	public void setPublicationTypeId(final Integer publicationTypeId) {
		this.publicationTypeId = publicationTypeId;
	}

	public PublicationEntity withPublicationTypeId(final Integer publicationTypeId) {
		this.publicationTypeId = publicationTypeId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public PublicationEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public Integer getMeOId() {
		return meOId;
	}

	public void setMeOId(final Integer meOId) {
		this.meOId = meOId;
	}

	public PublicationEntity withMeOId(final Integer meOId) {
		this.meOId = meOId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public PublicationEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public PublicationEntity withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public PublicationEntity withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public PublicationEntity withOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
		return this;
	}

	public String getOcrFilename() {
		return ocrFilename;
	}

	public void setOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
	}

	public PublicationEntity withOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
		return this;
	}

	public String getXmltext() {
		return xmltext;
	}

	public void setXmltext(final String xmltext) {
		this.xmltext = xmltext;
	}

	public PublicationEntity withXmltext(final String xmltext) {
		this.xmltext = xmltext;
		return this;
	}

	public String getFilXtra() {
		return filXtra;
	}

	public void setFilXtra(final String filXtra) {
		this.filXtra = filXtra;
	}

	public PublicationEntity withFilXtra(final String filXtra) {
		this.filXtra = filXtra;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public PublicationEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public PublicationEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public String getFilFormat() {
		return filFormat;
	}

	public void setFilFormat(final String filFormat) {
		this.filFormat = filFormat;
	}

	public PublicationEntity withFilFormat(final String filFormat) {
		this.filFormat = filFormat;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public PublicationEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PublicationEntity that = (PublicationEntity) o;
		return Objects.equals(publicationId, that.publicationId) && Objects.equals(filename, that.filename) && Objects.equals(publicationType, that.publicationType)
			&& Objects.equals(date, that.date) && Objects.equals(periodicalTitle, that.periodicalTitle) && Objects.equals(issueNumber, that.issueNumber)
			&& Objects.equals(pageNumber, that.pageNumber) && Objects.equals(bfJId, that.bfJId) && Objects.equals(publisherTopographyId, that.publisherTopographyId)
			&& Objects.equals(publisherLocation, that.publisherLocation) && Objects.equals(documentDate, that.documentDate) && Objects.equals(documentTitle, that.documentTitle)
			&& Objects.equals(feId, that.feId) && Objects.equals(reId, that.reId) && Objects.equals(ujId, that.ujId) && Objects.equals(ueId, that.ueId)
			&& Objects.equals(publicationTypeId, that.publicationTypeId) && Objects.equals(locationText, that.locationText) && Objects.equals(meOId, that.meOId)
			&& Objects.equals(comment, that.comment) && Objects.equals(thumbnailFilename, that.thumbnailFilename) && Objects.equals(largeImageFilename, that.largeImageFilename)
			&& Objects.equals(originalFilename, that.originalFilename) && Objects.equals(ocrFilename, that.ocrFilename) && Objects.equals(xmltext, that.xmltext)
			&& Objects.equals(filXtra, that.filXtra) && Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options)
			&& Objects.equals(filFormat, that.filFormat) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publicationId, filename, publicationType, date, periodicalTitle, issueNumber, pageNumber, bfJId, publisherTopographyId, publisherLocation, documentDate,
			documentTitle, feId, reId, ujId, ueId, publicationTypeId, locationText, meOId, comment, thumbnailFilename, largeImageFilename, originalFilename, ocrFilename,
			xmltext, filXtra, nodeId, options, filFormat, deletedDate);
	}

	@Override
	public String toString() {
		return "PublicationEntity{" +
			"publicationId=" + publicationId +
			", filename='" + filename + '\'' +
			", publicationType='" + publicationType + '\'' +
			", date='" + date + '\'' +
			", periodicalTitle='" + periodicalTitle + '\'' +
			", issueNumber='" + issueNumber + '\'' +
			", pageNumber='" + pageNumber + '\'' +
			", bfJId=" + bfJId +
			", publisherTopographyId=" + publisherTopographyId +
			", publisherLocation='" + publisherLocation + '\'' +
			", documentDate='" + documentDate + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", feId=" + feId +
			", reId=" + reId +
			", ujId=" + ujId +
			", ueId=" + ueId +
			", publicationTypeId=" + publicationTypeId +
			", locationText='" + locationText + '\'' +
			", meOId=" + meOId +
			", comment='" + comment + '\'' +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", originalFilename='" + originalFilename + '\'' +
			", ocrFilename='" + ocrFilename + '\'' +
			", xmltext='" + xmltext + '\'' +
			", filXtra='" + filXtra + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", filFormat='" + filFormat + '\'' +
			", deletedDate=" + deletedDate +
			'}';
	}
}
