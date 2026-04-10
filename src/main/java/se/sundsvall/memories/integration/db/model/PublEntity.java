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
@Table(name = "PUBL")
public class PublEntity {

	@Id
	@Column(name = "P_ID")
	private Integer publId;

	@Column(name = "FILNAMN", length = 256)
	private String filnamn;

	@Column(name = "PUBLIKTYP", length = 40)
	private String publiktyp;

	@Column(name = "DATUM", length = 10)
	private String datum;

	@Column(name = "TIDTITEL", length = 128)
	private String tidtitel;

	@Column(name = "TIDNR", length = 5)
	private String tidnr;

	@Column(name = "TIDSIDA", length = 3)
	private String tidsida;

	@Column(name = "BF_J_ID")
	private Integer bfJId;

	@Column(name = "FORLAG_T_ID")
	private Integer forlagTId;

	@Column(name = "FORLAG_OPLATS", length = 64)
	private String forlagOplats;

	@Column(name = "DOKDATUM", length = 10)
	private String dokdatum;

	@Column(name = "DOKTITEL", length = 256)
	private String doktitel;

	@Column(name = "F_E_ID")
	private Integer feId;

	@Column(name = "R_E_ID")
	private Integer reId;

	@Column(name = "U_J_ID")
	private Integer ujId;

	@Column(name = "U_E_ID")
	private Integer ueId;

	@Column(name = "P_T_ID")
	private Integer ptId;

	@Column(name = "P_OPLATS", length = 64)
	private String pubOplats;

	@Column(name = "ME_O_ID")
	private Integer meOId;

	@Column(name = "KOMMENT_PUBL", length = 4000)
	private String kommentPubl;

	@Column(name = "FIL_LITEN", length = 256)
	private String filLiten;

	@Column(name = "FIL_STOR", length = 256)
	private String filStor;

	@Column(name = "FIL_ORIGINAL", length = 256)
	private String filOriginal;

	@Column(name = "FIL_TXT", length = 256)
	private String filTxt;

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

	public static PublEntity create() {
		return new PublEntity();
	}

	public Integer getPublId() {
		return publId;
	}

	public void setPublId(final Integer publId) {
		this.publId = publId;
	}

	public PublEntity withPublId(final Integer publId) {
		this.publId = publId;
		return this;
	}

	public String getFilnamn() {
		return filnamn;
	}

	public void setFilnamn(final String filnamn) {
		this.filnamn = filnamn;
	}

	public PublEntity withFilnamn(final String filnamn) {
		this.filnamn = filnamn;
		return this;
	}

	public String getPubliktyp() {
		return publiktyp;
	}

	public void setPubliktyp(final String publiktyp) {
		this.publiktyp = publiktyp;
	}

	public PublEntity withPubliktyp(final String publiktyp) {
		this.publiktyp = publiktyp;
		return this;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(final String datum) {
		this.datum = datum;
	}

	public PublEntity withDatum(final String datum) {
		this.datum = datum;
		return this;
	}

	public String getTidtitel() {
		return tidtitel;
	}

	public void setTidtitel(final String tidtitel) {
		this.tidtitel = tidtitel;
	}

	public PublEntity withTidtitel(final String tidtitel) {
		this.tidtitel = tidtitel;
		return this;
	}

	public String getTidnr() {
		return tidnr;
	}

	public void setTidnr(final String tidnr) {
		this.tidnr = tidnr;
	}

	public PublEntity withTidnr(final String tidnr) {
		this.tidnr = tidnr;
		return this;
	}

	public String getTidsida() {
		return tidsida;
	}

	public void setTidsida(final String tidsida) {
		this.tidsida = tidsida;
	}

	public PublEntity withTidsida(final String tidsida) {
		this.tidsida = tidsida;
		return this;
	}

	public Integer getBfJId() {
		return bfJId;
	}

	public void setBfJId(final Integer bfJId) {
		this.bfJId = bfJId;
	}

	public PublEntity withBfJId(final Integer bfJId) {
		this.bfJId = bfJId;
		return this;
	}

	public Integer getForlagTId() {
		return forlagTId;
	}

	public void setForlagTId(final Integer forlagTId) {
		this.forlagTId = forlagTId;
	}

	public PublEntity withForlagTId(final Integer forlagTId) {
		this.forlagTId = forlagTId;
		return this;
	}

	public String getForlagOplats() {
		return forlagOplats;
	}

	public void setForlagOplats(final String forlagOplats) {
		this.forlagOplats = forlagOplats;
	}

	public PublEntity withForlagOplats(final String forlagOplats) {
		this.forlagOplats = forlagOplats;
		return this;
	}

	public String getDokdatum() {
		return dokdatum;
	}

	public void setDokdatum(final String dokdatum) {
		this.dokdatum = dokdatum;
	}

	public PublEntity withDokdatum(final String dokdatum) {
		this.dokdatum = dokdatum;
		return this;
	}

	public String getDoktitel() {
		return doktitel;
	}

	public void setDoktitel(final String doktitel) {
		this.doktitel = doktitel;
	}

	public PublEntity withDoktitel(final String doktitel) {
		this.doktitel = doktitel;
		return this;
	}

	public Integer getFeId() {
		return feId;
	}

	public void setFeId(final Integer feId) {
		this.feId = feId;
	}

	public PublEntity withFeId(final Integer feId) {
		this.feId = feId;
		return this;
	}

	public Integer getReId() {
		return reId;
	}

	public void setReId(final Integer reId) {
		this.reId = reId;
	}

	public PublEntity withReId(final Integer reId) {
		this.reId = reId;
		return this;
	}

	public Integer getUjId() {
		return ujId;
	}

	public void setUjId(final Integer ujId) {
		this.ujId = ujId;
	}

	public PublEntity withUjId(final Integer ujId) {
		this.ujId = ujId;
		return this;
	}

	public Integer getUeId() {
		return ueId;
	}

	public void setUeId(final Integer ueId) {
		this.ueId = ueId;
	}

	public PublEntity withUeId(final Integer ueId) {
		this.ueId = ueId;
		return this;
	}

	public Integer getPtId() {
		return ptId;
	}

	public void setPtId(final Integer ptId) {
		this.ptId = ptId;
	}

	public PublEntity withPtId(final Integer ptId) {
		this.ptId = ptId;
		return this;
	}

	public String getPubOplats() {
		return pubOplats;
	}

	public void setPubOplats(final String pubOplats) {
		this.pubOplats = pubOplats;
	}

	public PublEntity withPubOplats(final String pubOplats) {
		this.pubOplats = pubOplats;
		return this;
	}

	public Integer getMeOId() {
		return meOId;
	}

	public void setMeOId(final Integer meOId) {
		this.meOId = meOId;
	}

	public PublEntity withMeOId(final Integer meOId) {
		this.meOId = meOId;
		return this;
	}

	public String getKommentPubl() {
		return kommentPubl;
	}

	public void setKommentPubl(final String kommentPubl) {
		this.kommentPubl = kommentPubl;
	}

	public PublEntity withKommentPubl(final String kommentPubl) {
		this.kommentPubl = kommentPubl;
		return this;
	}

	public String getFilLiten() {
		return filLiten;
	}

	public void setFilLiten(final String filLiten) {
		this.filLiten = filLiten;
	}

	public PublEntity withFilLiten(final String filLiten) {
		this.filLiten = filLiten;
		return this;
	}

	public String getFilStor() {
		return filStor;
	}

	public void setFilStor(final String filStor) {
		this.filStor = filStor;
	}

	public PublEntity withFilStor(final String filStor) {
		this.filStor = filStor;
		return this;
	}

	public String getFilOriginal() {
		return filOriginal;
	}

	public void setFilOriginal(final String filOriginal) {
		this.filOriginal = filOriginal;
	}

	public PublEntity withFilOriginal(final String filOriginal) {
		this.filOriginal = filOriginal;
		return this;
	}

	public String getFilTxt() {
		return filTxt;
	}

	public void setFilTxt(final String filTxt) {
		this.filTxt = filTxt;
	}

	public PublEntity withFilTxt(final String filTxt) {
		this.filTxt = filTxt;
		return this;
	}

	public String getXmltext() {
		return xmltext;
	}

	public void setXmltext(final String xmltext) {
		this.xmltext = xmltext;
	}

	public PublEntity withXmltext(final String xmltext) {
		this.xmltext = xmltext;
		return this;
	}

	public String getFilXtra() {
		return filXtra;
	}

	public void setFilXtra(final String filXtra) {
		this.filXtra = filXtra;
	}

	public PublEntity withFilXtra(final String filXtra) {
		this.filXtra = filXtra;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public PublEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public PublEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public String getFilFormat() {
		return filFormat;
	}

	public void setFilFormat(final String filFormat) {
		this.filFormat = filFormat;
	}

	public PublEntity withFilFormat(final String filFormat) {
		this.filFormat = filFormat;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public PublEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PublEntity that = (PublEntity) o;
		return Objects.equals(publId, that.publId) && Objects.equals(filnamn, that.filnamn) && Objects.equals(publiktyp, that.publiktyp) && Objects.equals(datum, that.datum)
			&& Objects.equals(tidtitel, that.tidtitel) && Objects.equals(tidnr, that.tidnr) && Objects.equals(tidsida, that.tidsida) && Objects.equals(bfJId, that.bfJId)
			&& Objects.equals(forlagTId, that.forlagTId) && Objects.equals(forlagOplats, that.forlagOplats) && Objects.equals(dokdatum, that.dokdatum) && Objects.equals(doktitel, that.doktitel)
			&& Objects.equals(feId, that.feId) && Objects.equals(reId, that.reId) && Objects.equals(ujId, that.ujId) && Objects.equals(ueId, that.ueId)
			&& Objects.equals(ptId, that.ptId) && Objects.equals(pubOplats, that.pubOplats) && Objects.equals(meOId, that.meOId) && Objects.equals(kommentPubl, that.kommentPubl)
			&& Objects.equals(filLiten, that.filLiten) && Objects.equals(filStor, that.filStor) && Objects.equals(filOriginal, that.filOriginal) && Objects.equals(filTxt, that.filTxt)
			&& Objects.equals(xmltext, that.xmltext) && Objects.equals(filXtra, that.filXtra) && Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options)
			&& Objects.equals(filFormat, that.filFormat) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publId, filnamn, publiktyp, datum, tidtitel, tidnr, tidsida, bfJId, forlagTId, forlagOplats, dokdatum, doktitel, feId, reId, ujId, ueId, ptId, pubOplats, meOId,
			kommentPubl, filLiten, filStor, filOriginal, filTxt, xmltext, filXtra, nodeId, options, filFormat, deletedDate);
	}

	@Override
	public String toString() {
		return "PublEntity{" +
			"publId=" + publId +
			", filnamn='" + filnamn + '\'' +
			", publiktyp='" + publiktyp + '\'' +
			", datum='" + datum + '\'' +
			", tidtitel='" + tidtitel + '\'' +
			", tidnr='" + tidnr + '\'' +
			", tidsida='" + tidsida + '\'' +
			", bfJId=" + bfJId +
			", forlagTId=" + forlagTId +
			", forlagOplats='" + forlagOplats + '\'' +
			", dokdatum='" + dokdatum + '\'' +
			", doktitel='" + doktitel + '\'' +
			", feId=" + feId +
			", reId=" + reId +
			", ujId=" + ujId +
			", ueId=" + ueId +
			", ptId=" + ptId +
			", pubOplats='" + pubOplats + '\'' +
			", meOId=" + meOId +
			", kommentPubl='" + kommentPubl + '\'' +
			", filLiten='" + filLiten + '\'' +
			", filStor='" + filStor + '\'' +
			", filOriginal='" + filOriginal + '\'' +
			", filTxt='" + filTxt + '\'' +
			", xmltext='" + xmltext + '\'' +
			", filXtra='" + filXtra + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", filFormat='" + filFormat + '\'' +
			", deletedDate=" + deletedDate +
			'}';
	}
}
