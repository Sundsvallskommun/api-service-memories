package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "FILM")
public class FilmEntity {

	@Id
	@Column(name = "FILM_ID")
	private Integer filmId;

	@Column(name = "FILNAMN", length = 256)
	private String filnamn;

	@Column(name = "FILM_OBJ_FIL", length = 256)
	private String filmObjFil;

	@Column(name = "OBJTYP", length = 8)
	private String objtyp;

	@Column(name = "DATUM", length = 256)
	private String datum;

	@Column(name = "DOKTITEL", length = 2256)
	private String doktitel;

	@Column(name = "FILM_T_ID")
	private Integer filmTId;

	@Column(name = "FILM_OPLATS", length = 64)
	private String filmOplats;

	@Column(name = "FILM_O_ID")
	private Integer filmOId;

	@Column(name = "FILM_U_E_ID")
	private Integer filmUEId;

	@Column(name = "FILM_U_J_ID")
	private Integer filmUId;

	@Column(name = "KOMMENT_FILM", length = 4000)
	private String kommentFilm;

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

	public Integer getFilmId() {
		return filmId;
	}

	public void setFilmId(final Integer filmId) {
		this.filmId = filmId;
	}

	public FilmEntity withFilmId(final Integer filmId) {
		this.filmId = filmId;
		return this;
	}

	public String getFilnamn() {
		return filnamn;
	}

	public void setFilnamn(final String filnamn) {
		this.filnamn = filnamn;
	}

	public FilmEntity withFilnamn(final String filnamn) {
		this.filnamn = filnamn;
		return this;
	}

	public String getFilmObjFil() {
		return filmObjFil;
	}

	public void setFilmObjFil(final String filmObjFil) {
		this.filmObjFil = filmObjFil;
	}

	public FilmEntity withFilmObjFil(final String filmObjFil) {
		this.filmObjFil = filmObjFil;
		return this;
	}

	public String getObjtyp() {
		return objtyp;
	}

	public void setObjtyp(final String objtyp) {
		this.objtyp = objtyp;
	}

	public FilmEntity withObjtyp(final String objtyp) {
		this.objtyp = objtyp;
		return this;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(final String datum) {
		this.datum = datum;
	}

	public FilmEntity withDatum(final String datum) {
		this.datum = datum;
		return this;
	}

	public String getDoktitel() {
		return doktitel;
	}

	public void setDoktitel(final String doktitel) {
		this.doktitel = doktitel;
	}

	public FilmEntity withDoktitel(final String doktitel) {
		this.doktitel = doktitel;
		return this;
	}

	public Integer getFilmTId() {
		return filmTId;
	}

	public void setFilmTId(final Integer filmTId) {
		this.filmTId = filmTId;
	}

	public FilmEntity withFilmTId(final Integer filmTId) {
		this.filmTId = filmTId;
		return this;
	}

	public String getFilmOplats() {
		return filmOplats;
	}

	public void setFilmOplats(final String filmOplats) {
		this.filmOplats = filmOplats;
	}

	public FilmEntity withFilmOplats(final String filmOplats) {
		this.filmOplats = filmOplats;
		return this;
	}

	public Integer getFilmOId() {
		return filmOId;
	}

	public void setFilmOId(final Integer filmOId) {
		this.filmOId = filmOId;
	}

	public FilmEntity withFilmOId(final Integer filmOId) {
		this.filmOId = filmOId;
		return this;
	}

	public Integer getFilmUEId() {
		return filmUEId;
	}

	public void setFilmUEId(final Integer filmUEId) {
		this.filmUEId = filmUEId;
	}

	public FilmEntity withFilmUEId(final Integer filmUEId) {
		this.filmUEId = filmUEId;
		return this;
	}

	public Integer getFilmUId() {
		return filmUId;
	}

	public void setFilmUId(final Integer filmUId) {
		this.filmUId = filmUId;
	}

	public FilmEntity withFilmUId(final Integer filmUId) {
		this.filmUId = filmUId;
		return this;
	}

	public String getKommentFilm() {
		return kommentFilm;
	}

	public void setKommentFilm(final String kommentFilm) {
		this.kommentFilm = kommentFilm;
	}

	public FilmEntity withKommentFilm(final String kommentFilm) {
		this.kommentFilm = kommentFilm;
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

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final FilmEntity that = (FilmEntity) o;
		return Objects.equals(filmId, that.filmId) && Objects.equals(filnamn, that.filnamn) && Objects.equals(filmObjFil, that.filmObjFil) && Objects.equals(objtyp, that.objtyp) && Objects.equals(datum,
			that.datum) && Objects.equals(doktitel, that.doktitel) && Objects.equals(filmTId, that.filmTId) && Objects.equals(filmOplats, that.filmOplats) && Objects.equals(filmOId, that.filmOId)
			&& Objects.equals(filmUEId, that.filmUEId) && Objects.equals(filmUId, that.filmUId) && Objects.equals(kommentFilm, that.kommentFilm) && Objects.equals(filmMimeType, that.filmMimeType)
			&& Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filmId, filnamn, filmObjFil, objtyp, datum, doktitel, filmTId, filmOplats, filmOId, filmUEId, filmUId, kommentFilm, filmMimeType, nodeId, options, deletedDate);
	}

	@Override
	public String toString() {
		return "FilmEntity{" +
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
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
