package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Audio model")
public class Audio {

	@Schema(description = "Audio ID", examples = "123")
	private Integer audioId;

	@Schema(description = "Filename", examples = "interview1980.mp3")
	private String filename;

	@Schema(description = "Audio object file path", examples = "/path/to/file.mp3")
	private String objectFilePath;

	@Schema(description = "Object type", examples = "LJUD")
	private String objectType;

	@Schema(description = "Date", examples = "1980-04-12")
	private String date;

	@Schema(description = "Document title", examples = "Interview with the mayor")
	private String documentTitle;

	@Schema(description = "Topography ID", examples = "1")
	private Integer topographyId;

	@Schema(description = "Audio location (free-text fallback from LJUD_OPLATS)", examples = "Sundsvall")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over locationText when set)", examples = "Sundsvall")
	private String location;

	@Schema(description = "OCM subject ID", examples = "1")
	private Integer subjectId;

	@Schema(description = "Resolved subject label from OCM (Ämne)", examples = "Intervju")
	private String subject;

	@Schema(description = "Author (individual person) ID", examples = "0")
	private Integer authorPersonId;

	@Schema(description = "Author (legal entity) ID", examples = "1")
	private Integer authorEntityId;

	@Schema(description = "Comment", examples = "Audio recording of an interview")
	private String comment;

	@Schema(description = "MIME type", examples = "audio/mpeg")
	private String audioMimeType;

	@Schema(description = "Node ID", examples = "456")
	private Integer nodeId;

	@Schema(description = "Options", examples = "0")
	private Integer options;

	@Schema(description = "Deleted date", examples = "2026-01-15")
	private LocalDate deletedDate;

	public static Audio create() {
		return new Audio();
	}

	public Integer getAudioId() {
		return audioId;
	}

	public void setAudioId(final Integer audioId) {
		this.audioId = audioId;
	}

	public Audio withAudioId(final Integer audioId) {
		this.audioId = audioId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Audio withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getObjectFilePath() {
		return objectFilePath;
	}

	public void setObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}

	public Audio withObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public Audio withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public Audio withDate(final String date) {
		this.date = date;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Audio withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public Audio withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public Audio withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Audio withLocation(final String location) {
		this.location = location;
		return this;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Audio withSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public Audio withSubject(final String subject) {
		this.subject = subject;
		return this;
	}

	public Integer getAuthorPersonId() {
		return authorPersonId;
	}

	public void setAuthorPersonId(final Integer authorPersonId) {
		this.authorPersonId = authorPersonId;
	}

	public Audio withAuthorPersonId(final Integer authorPersonId) {
		this.authorPersonId = authorPersonId;
		return this;
	}

	public Integer getAuthorEntityId() {
		return authorEntityId;
	}

	public void setAuthorEntityId(final Integer authorEntityId) {
		this.authorEntityId = authorEntityId;
	}

	public Audio withAuthorEntityId(final Integer authorEntityId) {
		this.authorEntityId = authorEntityId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Audio withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getAudioMimeType() {
		return audioMimeType;
	}

	public void setAudioMimeType(final String audioMimeType) {
		this.audioMimeType = audioMimeType;
	}

	public Audio withAudioMimeType(final String audioMimeType) {
		this.audioMimeType = audioMimeType;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public Audio withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public Audio withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public Audio withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Audio audio = (Audio) o;
		return Objects.equals(audioId, audio.audioId) && Objects.equals(filename, audio.filename) && Objects.equals(objectFilePath, audio.objectFilePath)
			&& Objects.equals(objectType, audio.objectType) && Objects.equals(date, audio.date) && Objects.equals(documentTitle, audio.documentTitle)
			&& Objects.equals(topographyId, audio.topographyId) && Objects.equals(locationText, audio.locationText) && Objects.equals(location, audio.location)
			&& Objects.equals(subjectId, audio.subjectId) && Objects.equals(subject, audio.subject) && Objects.equals(authorPersonId, audio.authorPersonId)
			&& Objects.equals(authorEntityId, audio.authorEntityId) && Objects.equals(comment, audio.comment) && Objects.equals(audioMimeType, audio.audioMimeType)
			&& Objects.equals(nodeId, audio.nodeId) && Objects.equals(options, audio.options) && Objects.equals(deletedDate, audio.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(audioId, filename, objectFilePath, objectType, date, documentTitle, topographyId, locationText, location, subjectId, subject, authorPersonId, authorEntityId, comment,
			audioMimeType, nodeId, options, deletedDate);
	}

	@Override
	public String toString() {
		return "Audio{" +
			"audioId=" + audioId +
			", filename='" + filename + '\'' +
			", objectFilePath='" + objectFilePath + '\'' +
			", objectType='" + objectType + '\'' +
			", date='" + date + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			", subjectId=" + subjectId +
			", subject='" + subject + '\'' +
			", authorPersonId=" + authorPersonId +
			", authorEntityId=" + authorEntityId +
			", comment='" + comment + '\'' +
			", audioMimeType='" + audioMimeType + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
