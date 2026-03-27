package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Film model")
public class Film {

	@Schema(description = "Film ID", examples = "123")
	private Long filmId;

	@Schema(description = "Filename", examples = "film001.mp4")
	private String filnamn;

	@Schema(description = "Film object file path", examples = "/path/to/file.mp4")
	private String filmObjFil;

	@Schema(description = "Object type", examples = "VIDEO")
	private String objtyp;

	@Schema(description = "Date", examples = "1985-06-15")
	private String datum;

	@Schema(description = "Document title", examples = "Midsommarfirande i Sundsvall")
	private String doktitel;

	@Schema(description = "Film type ID", examples = "1")
	private Long filmTId;

	@Schema(description = "Film location", examples = "Sundsvall")
	private String filmOplats;

	@Schema(description = "Film organization ID", examples = "1")
	private Long filmOId;

	@Schema(description = "Film sub-entity ID", examples = "0")
	private Long filmUEId;

	@Schema(description = "Film unit ID", examples = "1")
	private Long filmUId;

	@Schema(description = "Comment", examples = "En film om midsommarfirande")
	private String kommentFilm;

	@Schema(description = "MIME type", examples = "video/mp4")
	private String filmMimeType;

	@Schema(description = "Archive creator", examples = "ASV001")
	private String asv;

	@Schema(description = "Node ID", examples = "456")
	private Long nodeId;

	@Schema(description = "Options", examples = "0")
	private Long options;

	@Schema(description = "Deleted date", examples = "2026-01-15")
	private LocalDate deletedDate;

	public static Film create() {
		return new Film();
	}

	public Long getFilmId() {
		return filmId;
	}

	public void setFilmId(final Long filmId) {
		this.filmId = filmId;
	}

	public Film withFilmId(final Long filmId) {
		this.filmId = filmId;
		return this;
	}

	public String getFilnamn() {
		return filnamn;
	}

	public void setFilnamn(final String filnamn) {
		this.filnamn = filnamn;
	}

	public Film withFilnamn(final String filnamn) {
		this.filnamn = filnamn;
		return this;
	}

	public String getFilmObjFil() {
		return filmObjFil;
	}

	public void setFilmObjFil(final String filmObjFil) {
		this.filmObjFil = filmObjFil;
	}

	public Film withFilmObjFil(final String filmObjFil) {
		this.filmObjFil = filmObjFil;
		return this;
	}

	public String getObjtyp() {
		return objtyp;
	}

	public void setObjtyp(final String objtyp) {
		this.objtyp = objtyp;
	}

	public Film withObjtyp(final String objtyp) {
		this.objtyp = objtyp;
		return this;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(final String datum) {
		this.datum = datum;
	}

	public Film withDatum(final String datum) {
		this.datum = datum;
		return this;
	}

	public String getDoktitel() {
		return doktitel;
	}

	public void setDoktitel(final String doktitel) {
		this.doktitel = doktitel;
	}

	public Film withDoktitel(final String doktitel) {
		this.doktitel = doktitel;
		return this;
	}

	public Long getFilmTId() {
		return filmTId;
	}

	public void setFilmTId(final Long filmTId) {
		this.filmTId = filmTId;
	}

	public Film withFilmTId(final Long filmTId) {
		this.filmTId = filmTId;
		return this;
	}

	public String getFilmOplats() {
		return filmOplats;
	}

	public void setFilmOplats(final String filmOplats) {
		this.filmOplats = filmOplats;
	}

	public Film withFilmOplats(final String filmOplats) {
		this.filmOplats = filmOplats;
		return this;
	}

	public Long getFilmOId() {
		return filmOId;
	}

	public void setFilmOId(final Long filmOId) {
		this.filmOId = filmOId;
	}

	public Film withFilmOId(final Long filmOId) {
		this.filmOId = filmOId;
		return this;
	}

	public Long getFilmUEId() {
		return filmUEId;
	}

	public void setFilmUEId(final Long filmUEId) {
		this.filmUEId = filmUEId;
	}

	public Film withFilmUEId(final Long filmUEId) {
		this.filmUEId = filmUEId;
		return this;
	}

	public Long getFilmUId() {
		return filmUId;
	}

	public void setFilmUId(final Long filmUId) {
		this.filmUId = filmUId;
	}

	public Film withFilmUId(final Long filmUId) {
		this.filmUId = filmUId;
		return this;
	}

	public String getKommentFilm() {
		return kommentFilm;
	}

	public void setKommentFilm(final String kommentFilm) {
		this.kommentFilm = kommentFilm;
	}

	public Film withKommentFilm(final String kommentFilm) {
		this.kommentFilm = kommentFilm;
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

	public String getAsv() {
		return asv;
	}

	public void setAsv(final String asv) {
		this.asv = asv;
	}

	public Film withAsv(final String asv) {
		this.asv = asv;
		return this;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Long nodeId) {
		this.nodeId = nodeId;
	}

	public Film withNodeId(final Long nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Long getOptions() {
		return options;
	}

	public void setOptions(final Long options) {
		this.options = options;
	}

	public Film withOptions(final Long options) {
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
		final Film that = (Film) o;
		return Objects.equals(filmId, that.filmId) && Objects.equals(filnamn, that.filnamn) && Objects.equals(filmObjFil, that.filmObjFil) && Objects.equals(objtyp, that.objtyp)
			&& Objects.equals(datum, that.datum) && Objects.equals(doktitel, that.doktitel) && Objects.equals(filmTId, that.filmTId) && Objects.equals(filmOplats, that.filmOplats)
			&& Objects.equals(filmOId, that.filmOId) && Objects.equals(filmUEId, that.filmUEId) && Objects.equals(filmUId, that.filmUId) && Objects.equals(kommentFilm, that.kommentFilm)
			&& Objects.equals(filmMimeType, that.filmMimeType) && Objects.equals(asv, that.asv) && Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options)
			&& Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filmId, filnamn, filmObjFil, objtyp, datum, doktitel, filmTId, filmOplats, filmOId, filmUEId, filmUId, kommentFilm, filmMimeType, asv, nodeId, options, deletedDate);
	}

	@Override
	public String toString() {
		return "Film{" +
			"filmId=" + filmId +
			", filnamn='" + filnamn + '\'' +
			", filmObjFil='" + filmObjFil + '\'' +
			", objtyp='" + objtyp + '\'' +
			", datum='" + datum + '\'' +
			", doktitel='" + doktitel + '\'' +
			", filmTId=" + filmTId +
			", filmOplats='" + filmOplats + '\'' +
			", filmOId=" + filmOId +
			", filmUEId=" + filmUEId +
			", filmUId=" + filmUId +
			", kommentFilm='" + kommentFilm + '\'' +
			", filmMimeType='" + filmMimeType + '\'' +
			", asv='" + asv + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
