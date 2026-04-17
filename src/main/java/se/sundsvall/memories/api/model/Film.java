package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Film model")
public class Film {

	@Schema(description = "Film ID", examples = "123")
	private Integer filmId;

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
	private Integer filmTId;

	@Schema(description = "Film location (free-text fallback from FILM_OPLATS)", examples = "Sundsvall")
	private String filmOplats;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over filmOplats when set)", examples = "Sundsvall")
	private String plats;

	@Schema(description = "Film organization ID", examples = "1")
	private Integer filmOId;

	@Schema(description = "Film sub-entity ID", examples = "0")
	private Integer filmUEId;

	@Schema(description = "Film unit ID", examples = "1")
	private Integer filmUId;

	@Schema(description = "Comment", examples = "En film om midsommarfirande")
	private String kommentFilm;

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

	public Integer getFilmTId() {
		return filmTId;
	}

	public void setFilmTId(final Integer filmTId) {
		this.filmTId = filmTId;
	}

	public Film withFilmTId(final Integer filmTId) {
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

	public String getPlats() {
		return plats;
	}

	public void setPlats(final String plats) {
		this.plats = plats;
	}

	public Film withPlats(final String plats) {
		this.plats = plats;
		return this;
	}

	public Integer getFilmOId() {
		return filmOId;
	}

	public void setFilmOId(final Integer filmOId) {
		this.filmOId = filmOId;
	}

	public Film withFilmOId(final Integer filmOId) {
		this.filmOId = filmOId;
		return this;
	}

	public Integer getFilmUEId() {
		return filmUEId;
	}

	public void setFilmUEId(final Integer filmUEId) {
		this.filmUEId = filmUEId;
	}

	public Film withFilmUEId(final Integer filmUEId) {
		this.filmUEId = filmUEId;
		return this;
	}

	public Integer getFilmUId() {
		return filmUId;
	}

	public void setFilmUId(final Integer filmUId) {
		this.filmUId = filmUId;
	}

	public Film withFilmUId(final Integer filmUId) {
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
		return Objects.equals(filmId, film.filmId) && Objects.equals(filnamn, film.filnamn) && Objects.equals(filmObjFil, film.filmObjFil) && Objects.equals(objtyp, film.objtyp) && Objects.equals(datum,
			film.datum) && Objects.equals(doktitel, film.doktitel) && Objects.equals(filmTId, film.filmTId) && Objects.equals(filmOplats, film.filmOplats) && Objects.equals(plats, film.plats) && Objects.equals(filmOId, film.filmOId)
			&& Objects.equals(filmUEId, film.filmUEId) && Objects.equals(filmUId, film.filmUId) && Objects.equals(kommentFilm, film.kommentFilm) && Objects.equals(filmMimeType, film.filmMimeType)
			&& Objects.equals(nodeId, film.nodeId) && Objects.equals(options, film.options) && Objects.equals(deletedDate, film.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filmId, filnamn, filmObjFil, objtyp, datum, doktitel, filmTId, filmOplats, plats, filmOId, filmUEId, filmUId, kommentFilm, filmMimeType, nodeId, options, deletedDate);
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
			", plats='" + plats + '\'' +
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
