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
@Table(name = "TEXT")
public class TextEntity {

	@Id
	@Column(name = "ID")
	private Integer textId;

	@Column(name = "DOKDATUM", length = 10)
	private String documentDate;

	@Column(name = "DOKDATUM_SLUT", length = 10)
	private String documentEndDate;

	@Column(name = "DOKTITEL", length = 256)
	private String documentTitle;

	@Column(name = "U_E_ID")
	private Integer ueId;

	@Column(name = "U_T_ID")
	private Integer ueTopographyId;

	@Column(name = "D_T_ID")
	private Integer topographyId;

	@Column(name = "D_OPLATS", length = 64)
	private String locationText;

	@Column(name = "KOMMENT_DOC", length = 4000)
	private String comment;

	@Column(name = "FILNAMN", length = 256)
	private String filename;

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

	public static TextEntity create() {
		return new TextEntity();
	}

	public Integer getTextId() {
		return textId;
	}

	public void setTextId(final Integer textId) {
		this.textId = textId;
	}

	public TextEntity withTextId(final Integer textId) {
		this.textId = textId;
		return this;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(final String documentDate) {
		this.documentDate = documentDate;
	}

	public TextEntity withDocumentDate(final String documentDate) {
		this.documentDate = documentDate;
		return this;
	}

	public String getDocumentEndDate() {
		return documentEndDate;
	}

	public void setDocumentEndDate(final String documentEndDate) {
		this.documentEndDate = documentEndDate;
	}

	public TextEntity withDocumentEndDate(final String documentEndDate) {
		this.documentEndDate = documentEndDate;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public TextEntity withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public Integer getUeId() {
		return ueId;
	}

	public void setUeId(final Integer ueId) {
		this.ueId = ueId;
	}

	public TextEntity withUeId(final Integer ueId) {
		this.ueId = ueId;
		return this;
	}

	public Integer getUeTopographyId() {
		return ueTopographyId;
	}

	public void setUeTopographyId(final Integer ueTopographyId) {
		this.ueTopographyId = ueTopographyId;
	}

	public TextEntity withUeTopographyId(final Integer ueTopographyId) {
		this.ueTopographyId = ueTopographyId;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public TextEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public TextEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public TextEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public TextEntity withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public TextEntity withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public TextEntity withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public TextEntity withOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
		return this;
	}

	public String getOcrFilename() {
		return ocrFilename;
	}

	public void setOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
	}

	public TextEntity withOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
		return this;
	}

	public String getXmltext() {
		return xmltext;
	}

	public void setXmltext(final String xmltext) {
		this.xmltext = xmltext;
	}

	public TextEntity withXmltext(final String xmltext) {
		this.xmltext = xmltext;
		return this;
	}

	public String getFilXtra() {
		return filXtra;
	}

	public void setFilXtra(final String filXtra) {
		this.filXtra = filXtra;
	}

	public TextEntity withFilXtra(final String filXtra) {
		this.filXtra = filXtra;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public TextEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public TextEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public String getFilFormat() {
		return filFormat;
	}

	public void setFilFormat(final String filFormat) {
		this.filFormat = filFormat;
	}

	public TextEntity withFilFormat(final String filFormat) {
		this.filFormat = filFormat;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public TextEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TextEntity that = (TextEntity) o;
		return Objects.equals(textId, that.textId) && Objects.equals(documentDate, that.documentDate) && Objects.equals(documentEndDate, that.documentEndDate)
			&& Objects.equals(documentTitle, that.documentTitle) && Objects.equals(ueId, that.ueId) && Objects.equals(ueTopographyId, that.ueTopographyId)
			&& Objects.equals(topographyId, that.topographyId) && Objects.equals(locationText, that.locationText) && Objects.equals(comment, that.comment)
			&& Objects.equals(filename, that.filename) && Objects.equals(thumbnailFilename, that.thumbnailFilename) && Objects.equals(largeImageFilename, that.largeImageFilename)
			&& Objects.equals(originalFilename, that.originalFilename) && Objects.equals(ocrFilename, that.ocrFilename) && Objects.equals(xmltext, that.xmltext)
			&& Objects.equals(filXtra, that.filXtra) && Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options)
			&& Objects.equals(filFormat, that.filFormat) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(textId, documentDate, documentEndDate, documentTitle, ueId, ueTopographyId, topographyId, locationText, comment, filename,
			thumbnailFilename, largeImageFilename, originalFilename, ocrFilename, xmltext, filXtra, nodeId, options, filFormat, deletedDate);
	}

	@Override
	public String toString() {
		return "TextEntity{" +
			"textId=" + textId +
			", documentDate='" + documentDate + '\'' +
			", documentEndDate='" + documentEndDate + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", ueId=" + ueId +
			", ueTopographyId=" + ueTopographyId +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", comment='" + comment + '\'' +
			", filename='" + filename + '\'' +
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
