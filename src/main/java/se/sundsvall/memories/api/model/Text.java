package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Text model")
public class Text {

	@Schema(description = "Text ID", examples = "1001")
	private Integer textId;

	@Schema(description = "Original filename from archive", examples = "minne_1920.xml")
	private String filename;

	@Schema(description = "Document start date", examples = "1920-01-01")
	private String documentDate;

	@Schema(description = "Document end date (for date ranges)", examples = "1920-12-31")
	private String documentEndDate;

	@Schema(description = "Document title", examples = "Minne från Sundsvall 1920")
	private String documentTitle;

	@Schema(description = "Document location (free-text fallback from D_OPLATS)", examples = "Sundsvall")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over locationText when set)", examples = "Sundsvall")
	private String location;

	@Schema(description = "OCM subject ID (D_O_ID)", examples = "20")
	private Integer subjectId;

	@Schema(description = "Resolved subject label from OCM (Ämne)", examples = "Musik")
	private String subject;

	@Schema(description = "Comment / description", examples = "Memoir transcribed from handwritten notes")
	private String comment;

	@Schema(description = "Thumbnail file name", examples = "TEXT.id_1001_fil_liten.jpeg")
	private String thumbnailFilename;

	@Schema(description = "Large image file name", examples = "TEXT.id_1001_fil_stor.jpeg")
	private String largeImageFilename;

	@Schema(description = "OCR/text file name", examples = "TEXT.id_1001_fil_txt.xml")
	private String ocrFilename;

	@Schema(description = "Full OCR/XML text (only returned on detail lookup)")
	private String xmltext;

	@Schema(description = "Extra media files associated with this text (only returned on detail lookup)")
	private List<TextMediaFile> mediaFiles;

	public static Text create() {
		return new Text();
	}

	public Integer getTextId() {
		return textId;
	}

	public void setTextId(final Integer textId) {
		this.textId = textId;
	}

	public Text withTextId(final Integer textId) {
		this.textId = textId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Text withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(final String documentDate) {
		this.documentDate = documentDate;
	}

	public Text withDocumentDate(final String documentDate) {
		this.documentDate = documentDate;
		return this;
	}

	public String getDocumentEndDate() {
		return documentEndDate;
	}

	public void setDocumentEndDate(final String documentEndDate) {
		this.documentEndDate = documentEndDate;
	}

	public Text withDocumentEndDate(final String documentEndDate) {
		this.documentEndDate = documentEndDate;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Text withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public Text withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Text withLocation(final String location) {
		this.location = location;
		return this;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Text withSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public Text withSubject(final String subject) {
		this.subject = subject;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Text withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public Text withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public Text withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOcrFilename() {
		return ocrFilename;
	}

	public void setOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
	}

	public Text withOcrFilename(final String ocrFilename) {
		this.ocrFilename = ocrFilename;
		return this;
	}

	public String getXmltext() {
		return xmltext;
	}

	public void setXmltext(final String xmltext) {
		this.xmltext = xmltext;
	}

	public Text withXmltext(final String xmltext) {
		this.xmltext = xmltext;
		return this;
	}

	public List<TextMediaFile> getMediaFiles() {
		return mediaFiles;
	}

	public void setMediaFiles(final List<TextMediaFile> mediaFiles) {
		this.mediaFiles = mediaFiles;
	}

	public Text withMediaFiles(final List<TextMediaFile> mediaFiles) {
		this.mediaFiles = mediaFiles;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Text that = (Text) o;
		return Objects.equals(textId, that.textId) && Objects.equals(filename, that.filename) && Objects.equals(documentDate, that.documentDate)
			&& Objects.equals(documentEndDate, that.documentEndDate) && Objects.equals(documentTitle, that.documentTitle) && Objects.equals(locationText, that.locationText)
			&& Objects.equals(location, that.location) && Objects.equals(subjectId, that.subjectId) && Objects.equals(subject, that.subject)
			&& Objects.equals(comment, that.comment) && Objects.equals(thumbnailFilename, that.thumbnailFilename)
			&& Objects.equals(largeImageFilename, that.largeImageFilename) && Objects.equals(ocrFilename, that.ocrFilename) && Objects.equals(xmltext, that.xmltext)
			&& Objects.equals(mediaFiles, that.mediaFiles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(textId, filename, documentDate, documentEndDate, documentTitle, locationText, location, subjectId, subject, comment,
			thumbnailFilename, largeImageFilename, ocrFilename, xmltext, mediaFiles);
	}

	@Override
	public String toString() {
		return "Text{" +
			"textId=" + textId +
			", filename='" + filename + '\'' +
			", documentDate='" + documentDate + '\'' +
			", documentEndDate='" + documentEndDate + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			", subjectId=" + subjectId +
			", subject='" + subject + '\'' +
			", comment='" + comment + '\'' +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", ocrFilename='" + ocrFilename + '\'' +
			", xmltext='" + xmltext + '\'' +
			", mediaFiles=" + mediaFiles +
			'}';
	}
}
