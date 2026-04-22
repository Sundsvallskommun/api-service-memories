package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "LJUD")
public class AudioEntity {

	@Id
	@Column(name = "LJUD_ID")
	private Integer audioId;

	@Column(name = "FILNAMN", length = 256)
	private String filename;

	@Column(name = "LJUD_OBJ_FIL", length = 256)
	private String objectFilePath;

	@Column(name = "OBJTYP", length = 9)
	private String objectType;

	@Column(name = "DATUM", length = 10)
	private String date;

	@Column(name = "DOKTITEL", length = 256)
	private String documentTitle;

	@Column(name = "LJUD_T_ID")
	private Integer topographyId;

	@Column(name = "LJUD_OPLATS", length = 64)
	private String locationText;

	@Column(name = "LJUD_O_ID")
	private Integer subjectId;

	@Column(name = "LJUD_U_E_ID")
	private Integer authorPersonId;

	@Column(name = "LJUD_U_J_ID")
	private Integer authorEntityId;

	@Column(name = "KOMMENT_LJUD", length = 4000)
	private String comment;

	@Column(name = "LJUD_MIME_TYPE", length = 50)
	private String audioMimeType;

	@Column(name = "NODEID")
	private Integer nodeId;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static AudioEntity create() {
		return new AudioEntity();
	}

	public Integer getAudioId() {
		return audioId;
	}

	public void setAudioId(final Integer audioId) {
		this.audioId = audioId;
	}

	public AudioEntity withAudioId(final Integer audioId) {
		this.audioId = audioId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public AudioEntity withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getObjectFilePath() {
		return objectFilePath;
	}

	public void setObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}

	public AudioEntity withObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public AudioEntity withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public AudioEntity withDate(final String date) {
		this.date = date;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public AudioEntity withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public AudioEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public AudioEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
	}

	public AudioEntity withSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	public Integer getAuthorPersonId() {
		return authorPersonId;
	}

	public void setAuthorPersonId(final Integer authorPersonId) {
		this.authorPersonId = authorPersonId;
	}

	public AudioEntity withAuthorPersonId(final Integer authorPersonId) {
		this.authorPersonId = authorPersonId;
		return this;
	}

	public Integer getAuthorEntityId() {
		return authorEntityId;
	}

	public void setAuthorEntityId(final Integer authorEntityId) {
		this.authorEntityId = authorEntityId;
	}

	public AudioEntity withAuthorEntityId(final Integer authorEntityId) {
		this.authorEntityId = authorEntityId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public AudioEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getAudioMimeType() {
		return audioMimeType;
	}

	public void setAudioMimeType(final String audioMimeType) {
		this.audioMimeType = audioMimeType;
	}

	public AudioEntity withAudioMimeType(final String audioMimeType) {
		this.audioMimeType = audioMimeType;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public AudioEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public AudioEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public AudioEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final AudioEntity that = (AudioEntity) o;
		return Objects.equals(audioId, that.audioId) && Objects.equals(filename, that.filename) && Objects.equals(objectFilePath, that.objectFilePath)
			&& Objects.equals(objectType, that.objectType) && Objects.equals(date, that.date) && Objects.equals(documentTitle, that.documentTitle)
			&& Objects.equals(topographyId, that.topographyId) && Objects.equals(locationText, that.locationText) && Objects.equals(subjectId, that.subjectId)
			&& Objects.equals(authorPersonId, that.authorPersonId) && Objects.equals(authorEntityId, that.authorEntityId) && Objects.equals(comment, that.comment)
			&& Objects.equals(audioMimeType, that.audioMimeType) && Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(audioId, filename, objectFilePath, objectType, date, documentTitle, topographyId, locationText, subjectId, authorPersonId, authorEntityId, comment, audioMimeType, nodeId,
			options, deletedDate);
	}

	@Override
	public String toString() {
		return "AudioEntity{" +
			"audioId=" + audioId +
			", filename='" + filename + '\'' +
			", objectFilePath='" + objectFilePath + '\'' +
			", objectType='" + objectType + '\'' +
			", date='" + date + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", subjectId=" + subjectId +
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
