package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Film model")
public class Film {

	@Schema(description = "Film ID", examples = "123")
	private Integer filmId;

	@Schema(description = "Filename", examples = "film001.mp4")
	private String filename;

	@Schema(description = "Film object file path", examples = "/path/to/file.mp4")
	private String objectFilePath;

	@Schema(description = "Object type", examples = "VIDEO")
	private String objectType;

	@Schema(description = "Date", examples = "1985-06-15")
	private String date;

	@Schema(description = "Document title", examples = "Midsummer celebration in Sundsvall")
	private String documentTitle;

	@Schema(description = "Topography ID", examples = "1")
	private Integer topographyId;

	@Schema(description = "Film location (free-text fallback from FILM_OPLATS)", examples = "Sundsvall")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over locationText when set)", examples = "Sundsvall")
	private String location;

	@Schema(description = "Film organization ID", examples = "1")
	private Integer organizationId;

	@Schema(description = "Film sub-entity ID", examples = "0")
	private Integer subEntityId;

	@Schema(description = "Film unit ID", examples = "1")
	private Integer unitId;

	@Schema(description = "Comment", examples = "A film about midsummer celebrations")
	private String comment;

	@Schema(description = "MIME type", examples = "video/mp4")
	private String filmMimeType;

	@Schema(description = "Node ID", examples = "456")
	private Integer nodeId;

	@Schema(description = "Options", examples = "0")
	private Integer options;

	@Schema(description = "Deleted date", examples = "2026-01-15")
	private LocalDate deletedDate;

	public static Film create() {
		return new Film();
	}

	public Integer getFilmId() {
		return filmId;
	}

	public void setFilmId(final Integer filmId) {
		this.filmId = filmId;
	}

	public Film withFilmId(final Integer filmId) {
		this.filmId = filmId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Film withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getObjectFilePath() {
		return objectFilePath;
	}

	public void setObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}

	public Film withObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public Film withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public Film withDate(final String date) {
		this.date = date;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Film withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public Film withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public Film withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Film withLocation(final String location) {
		this.location = location;
		return this;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(final Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Film withOrganizationId(final Integer organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public Integer getSubEntityId() {
		return subEntityId;
	}

	public void setSubEntityId(final Integer subEntityId) {
		this.subEntityId = subEntityId;
	}

	public Film withSubEntityId(final Integer subEntityId) {
		this.subEntityId = subEntityId;
		return this;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(final Integer unitId) {
		this.unitId = unitId;
	}

	public Film withUnitId(final Integer unitId) {
		this.unitId = unitId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Film withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getFilmMimeType() {
		return filmMimeType;
	}

	public void setFilmMimeType(final String filmMimeType) {
		this.filmMimeType = filmMimeType;
	}

	public Film withFilmMimeType(final String filmMimeType) {
		this.filmMimeType = filmMimeType;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public Film withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public Film withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public Film withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Film film = (Film) o;
		return Objects.equals(filmId, film.filmId) && Objects.equals(filename, film.filename) && Objects.equals(objectFilePath, film.objectFilePath)
			&& Objects.equals(objectType, film.objectType) && Objects.equals(date, film.date) && Objects.equals(documentTitle, film.documentTitle)
			&& Objects.equals(topographyId, film.topographyId) && Objects.equals(locationText, film.locationText) && Objects.equals(location, film.location)
			&& Objects.equals(organizationId, film.organizationId) && Objects.equals(subEntityId, film.subEntityId) && Objects.equals(unitId, film.unitId)
			&& Objects.equals(comment, film.comment) && Objects.equals(filmMimeType, film.filmMimeType)
			&& Objects.equals(nodeId, film.nodeId) && Objects.equals(options, film.options) && Objects.equals(deletedDate, film.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filmId, filename, objectFilePath, objectType, date, documentTitle, topographyId, locationText, location, organizationId, subEntityId, unitId, comment, filmMimeType, nodeId, options,
			deletedDate);
	}

	@Override
	public String toString() {
		return "Film{" +
			"filmId=" + filmId +
			", filename='" + filename + '\'' +
			", objectFilePath='" + objectFilePath + '\'' +
			", objectType='" + objectType + '\'' +
			", date='" + date + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			", organizationId=" + organizationId +
			", subEntityId=" + subEntityId +
			", unitId=" + unitId +
			", comment='" + comment + '\'' +
			", filmMimeType='" + filmMimeType + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
