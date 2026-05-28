package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Publication model")
public class Publication {

	@Schema(description = "Publication ID", examples = "207")
	private Integer publicationId;

	@Schema(description = "Original filename from archive", examples = "F21051/1841-02-18_Alfwar och Skämt nr 8.xml")
	private String filename;

	@Schema(description = "Publication type (denormalized PUBLIKTYP value)", examples = "Tidning")
	private String publicationType;

	@Schema(description = "Publication date", examples = "1841-02-18")
	private String date;

	@Schema(description = "Periodical title (newspaper/magazine name)", examples = "Alfwar och Skämt")
	private String periodicalTitle;

	@Schema(description = "Periodical issue number", examples = "8")
	private String issueNumber;

	@Schema(description = "Periodical page number", examples = "3")
	private String pageNumber;

	@Schema(description = "Publisher location", examples = "Sundsvall")
	private String publisherLocation;

	@Schema(description = "Document title", examples = "Page 3 Alfwar och Skämt nr 8 1841")
	private String documentTitle;

	@Schema(description = "Publication location (free-text fallback from P_OPLATS)", examples = "Sundsvall")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (via P_T_ID; preferred over locationText when set)", examples = "Sundsvall")
	private String location;

	@Schema(description = "Comment / description", examples = "Newspaper issue from 1841")
	private String comment;

	@Schema(description = "Thumbnail file name", examples = "PUBL.id_207_fil_liten.jpeg")
	private String thumbnailFilename;

	@Schema(description = "Large image file name", examples = "PUBL.id_207_fil_stor.jpeg")
	private String largeImageFilename;

	@Schema(description = "OCR/text file name", examples = "PUBL.id_207_fil_txt.xml")
	private String ocrFilename;

	@Schema(description = "Full OCR/XML text (only returned on detail lookup)")
	private String xmltext;

	public static Publication create() {
		return new Publication();
	}

	public Integer getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(final Integer publicationId) {
		this.publicationId = publicationId;
	}

	public Publication withPublicationId(final Integer publicationId) {
		this.publicationId = publicationId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Publication withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(final String publicationType) {
		this.publicationType = publicationType;
	}

	public Publication withPublicationType(final String publicationType) {
		this.publicationType = publicationType;
		return this;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public Publication withDate(final String date) {
		this.date = date;
		return this;
	}

	public String getPeriodicalTitle() {
		return periodicalTitle;
	}

	public void setPeriodicalTitle(final String periodicalTitle) {
		this.periodicalTitle = periodicalTitle;
	}

	public Publication withPeriodicalTitle(final String periodicalTitle) {
		this.periodicalTitle = periodicalTitle;
		return this;
	}

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
	}

	public Publication withIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
		return this;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(final String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Publication withPageNumber(final String pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}

	public String getPublisherLocation() {
		return publisherLocation;
	}

	public void setPublisherLocation(final String publisherLocation) {
		this.publisherLocation = publisherLocation;
	}

	public Publication withPublisherLocation(final String publisherLocation) {
		this.publisherLocation = publisherLocation;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Publication withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public Publication withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Publication withLocation(final String location) {
		this.location = location;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Publication withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public Publication withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public Publication withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOcrFilename() {
		return ocrFilename;
	}

	public void setOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
	}

	public Publication withOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
		return this;
	}

	public String getXmltext() {
		return xmltext;
	}

	public void setXmltext(final String xmltext) {
		this.xmltext = xmltext;
	}

	public Publication withXmltext(final String xmltext) {
		this.xmltext = xmltext;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Publication that = (Publication) o;
		return Objects.equals(publicationId, that.publicationId) && Objects.equals(filename, that.filename) && Objects.equals(publicationType, that.publicationType)
			&& Objects.equals(date, that.date) && Objects.equals(periodicalTitle, that.periodicalTitle) && Objects.equals(issueNumber, that.issueNumber)
			&& Objects.equals(pageNumber, that.pageNumber) && Objects.equals(publisherLocation, that.publisherLocation) && Objects.equals(documentTitle, that.documentTitle)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(location, that.location) && Objects.equals(comment, that.comment)
			&& Objects.equals(thumbnailFilename, that.thumbnailFilename) && Objects.equals(largeImageFilename, that.largeImageFilename)
			&& Objects.equals(ocrFilename, that.ocrFilename) && Objects.equals(xmltext, that.xmltext);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publicationId, filename, publicationType, date, periodicalTitle, issueNumber, pageNumber, publisherLocation, documentTitle,
			locationText, location, comment, thumbnailFilename, largeImageFilename, ocrFilename, xmltext);
	}

	@Override
	public String toString() {
		return "Publication{" +
			"publicationId=" + publicationId +
			", filename='" + filename + '\'' +
			", publicationType='" + publicationType + '\'' +
			", date='" + date + '\'' +
			", periodicalTitle='" + periodicalTitle + '\'' +
			", issueNumber='" + issueNumber + '\'' +
			", pageNumber='" + pageNumber + '\'' +
			", publisherLocation='" + publisherLocation + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			", comment='" + comment + '\'' +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", ocrFilename='" + ocrFilename + '\'' +
			", xmltext='" + xmltext + '\'' +
			'}';
	}
}
