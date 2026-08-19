package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "FILM")
public class FilmEntity {

	@Id
	@Column(name = "FILM_ID")
	private Integer id;

	@Column(name = "FILNAMN", length = 256)
	private String filename;

	@Column(name = "FILM_OBJ_FIL", length = 256)
	private String objectFilePath;

	@Column(name = "OBJTYP", length = 8)
	private String objectType;

	@Column(name = "DATUM", length = 256)
	private String date;

	@Column(name = "DOKTITEL", length = 2256)
	private String documentTitle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILM_T_ID")
	private TopographyEntity topography;

	@Column(name = "FILM_OPLATS", length = 64)
	private String locationText;

	@Column(name = "FILM_O_ID")
	private Integer organizationId;

	/**
	 * The upphovsman (originator) — a person, a legal entity, or neither. Both columns default to a sentinel row rather
	 * than to {@code NULL}, which {@link se.sundsvall.memories.service.mapper.CreatorMapper} reads as absent.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILM_U_E_ID")
	private PersonEntity creatorPerson;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILM_U_J_ID")
	private LegalEntityEntity creatorLegalEntity;

	@Column(name = "KOMMENT_FILM", length = 4000)
	private String comment;

	@Column(name = "FILM_MIME_TYPE", length = 50)
	private String filmMimeType;

	@Column(name = "NODEID")
	private Integer nodeId;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static FilmEntity create() {
		return new FilmEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public FilmEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public FilmEntity withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getObjectFilePath() {
		return objectFilePath;
	}

	public void setObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}

	public FilmEntity withObjectFilePath(final String objectFilePath) {
		this.objectFilePath = objectFilePath;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public FilmEntity withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public FilmEntity withDate(final String date) {
		this.date = date;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public FilmEntity withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public TopographyEntity getTopography() {
		return topography;
	}

	public void setTopography(final TopographyEntity topography) {
		this.topography = topography;
	}

	public FilmEntity withTopography(final TopographyEntity topography) {
		this.topography = topography;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public FilmEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(final Integer organizationId) {
		this.organizationId = organizationId;
	}

	public FilmEntity withOrganizationId(final Integer organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public FilmEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getFilmMimeType() {
		return filmMimeType;
	}

	public void setFilmMimeType(final String filmMimeType) {
		this.filmMimeType = filmMimeType;
	}

	public FilmEntity withFilmMimeType(final String filmMimeType) {
		this.filmMimeType = filmMimeType;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public FilmEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public FilmEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public FilmEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	public PersonEntity getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(final PersonEntity creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public LegalEntityEntity getCreatorLegalEntity() {
		return creatorLegalEntity;
	}

	public void setCreatorLegalEntity(final LegalEntityEntity creatorLegalEntity) {
		this.creatorLegalEntity = creatorLegalEntity;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final FilmEntity that = (FilmEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(filename, that.filename) && Objects.equals(objectFilePath, that.objectFilePath)
			&& Objects.equals(objectType, that.objectType) && Objects.equals(date, that.date) && Objects.equals(documentTitle, that.documentTitle)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(organizationId, that.organizationId)
			&& Objects.equals(comment, that.comment)
			&& Objects.equals(filmMimeType, that.filmMimeType) && Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, filename, objectFilePath, objectType, date, documentTitle, locationText, organizationId, comment, filmMimeType, nodeId, options,
			deletedDate);
	}

	@Override
	public String toString() {
		return "FilmEntity{" +
			"id=" + id +
			", filename='" + filename + '\'' +
			", objectFilePath='" + objectFilePath + '\'' +
			", objectType='" + objectType + '\'' +
			", date='" + date + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", locationText='" + locationText + '\'' +
			", organizationId=" + organizationId +
			", comment='" + comment + '\'' +
			", filmMimeType='" + filmMimeType + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
