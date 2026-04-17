package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Publication model")
public class Publication {

	@Schema(description = "Publication ID", examples = "207")
	private Integer publId;

	@Schema(description = "Original filename from archive", examples = "F21051/1841-02-18_Alfwar och Skämt nr 8.xml")
	private String filnamn;

	@Schema(description = "Publication type (free text)", examples = "")
	private String publiktyp;

	@Schema(description = "Publication date", examples = "1841-02-18")
	private String datum;

	@Schema(description = "Periodical title (newspaper/magazine name)", examples = "Alfwar och Skämt")
	private String tidtitel;

	@Schema(description = "Periodical issue number", examples = "8")
	private String tidnr;

	@Schema(description = "Periodical page number", examples = "3")
	private String tidsida;

	@Schema(description = "Publisher location", examples = "Sundsvall")
	private String forlagOplats;

	@Schema(description = "Document title", examples = "Sida 3 Alfwar och Skämt nr 8 1841")
	private String doktitel;

	@Schema(description = "Publication location (free-text fallback from P_OPLATS)", examples = "Sundsvall")
	private String pubOplats;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over pubOplats when set)", examples = "Sundsvall")
	private String plats;

	@Schema(description = "Comment / description", examples = "Tidningsnummer från 1841")
	private String kommentPubl;

	@Schema(description = "Thumbnail file name", examples = "PUBL.id_207_fil_liten.jpeg")
	private String filLiten;

	@Schema(description = "Large image file name", examples = "PUBL.id_207_fil_stor.jpeg")
	private String filStor;

	@Schema(description = "OCR/text file name", examples = "PUBL.id_207_fil_txt.xml")
	private String filTxt;

	@Schema(description = "Full OCR/XML text (only returned on detail lookup)")
	private String xmltext;

	public static Publication create() {
		return new Publication();
	}

	public Integer getPublId() {
		return publId;
	}

	public void setPublId(final Integer publId) {
		this.publId = publId;
	}

	public Publication withPublId(final Integer publId) {
		this.publId = publId;
		return this;
	}

	public String getFilnamn() {
		return filnamn;
	}

	public void setFilnamn(final String filnamn) {
		this.filnamn = filnamn;
	}

	public Publication withFilnamn(final String filnamn) {
		this.filnamn = filnamn;
		return this;
	}

	public String getPubliktyp() {
		return publiktyp;
	}

	public void setPubliktyp(final String publiktyp) {
		this.publiktyp = publiktyp;
	}

	public Publication withPubliktyp(final String publiktyp) {
		this.publiktyp = publiktyp;
		return this;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(final String datum) {
		this.datum = datum;
	}

	public Publication withDatum(final String datum) {
		this.datum = datum;
		return this;
	}

	public String getTidtitel() {
		return tidtitel;
	}

	public void setTidtitel(final String tidtitel) {
		this.tidtitel = tidtitel;
	}

	public Publication withTidtitel(final String tidtitel) {
		this.tidtitel = tidtitel;
		return this;
	}

	public String getTidnr() {
		return tidnr;
	}

	public void setTidnr(final String tidnr) {
		this.tidnr = tidnr;
	}

	public Publication withTidnr(final String tidnr) {
		this.tidnr = tidnr;
		return this;
	}

	public String getTidsida() {
		return tidsida;
	}

	public void setTidsida(final String tidsida) {
		this.tidsida = tidsida;
	}

	public Publication withTidsida(final String tidsida) {
		this.tidsida = tidsida;
		return this;
	}

	public String getForlagOplats() {
		return forlagOplats;
	}

	public void setForlagOplats(final String forlagOplats) {
		this.forlagOplats = forlagOplats;
	}

	public Publication withForlagOplats(final String forlagOplats) {
		this.forlagOplats = forlagOplats;
		return this;
	}

	public String getDoktitel() {
		return doktitel;
	}

	public void setDoktitel(final String doktitel) {
		this.doktitel = doktitel;
	}

	public Publication withDoktitel(final String doktitel) {
		this.doktitel = doktitel;
		return this;
	}

	public String getPubOplats() {
		return pubOplats;
	}

	public void setPubOplats(final String pubOplats) {
		this.pubOplats = pubOplats;
	}

	public Publication withPubOplats(final String pubOplats) {
		this.pubOplats = pubOplats;
		return this;
	}

	public String getPlats() {
		return plats;
	}

	public void setPlats(final String plats) {
		this.plats = plats;
	}

	public Publication withPlats(final String plats) {
		this.plats = plats;
		return this;
	}

	public String getKommentPubl() {
		return kommentPubl;
	}

	public void setKommentPubl(final String kommentPubl) {
		this.kommentPubl = kommentPubl;
	}

	public Publication withKommentPubl(final String kommentPubl) {
		this.kommentPubl = kommentPubl;
		return this;
	}

	public String getFilLiten() {
		return filLiten;
	}

	public void setFilLiten(final String filLiten) {
		this.filLiten = filLiten;
	}

	public Publication withFilLiten(final String filLiten) {
		this.filLiten = filLiten;
		return this;
	}

	public String getFilStor() {
		return filStor;
	}

	public void setFilStor(final String filStor) {
		this.filStor = filStor;
	}

	public Publication withFilStor(final String filStor) {
		this.filStor = filStor;
		return this;
	}

	public String getFilTxt() {
		return filTxt;
	}

	public void setFilTxt(final String filTxt) {
		this.filTxt = filTxt;
	}

	public Publication withFilTxt(final String filTxt) {
		this.filTxt = filTxt;
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
		return Objects.equals(publId, that.publId) && Objects.equals(filnamn, that.filnamn) && Objects.equals(publiktyp, that.publiktyp)
			&& Objects.equals(datum, that.datum) && Objects.equals(tidtitel, that.tidtitel) && Objects.equals(tidnr, that.tidnr) && Objects.equals(tidsida, that.tidsida)
			&& Objects.equals(forlagOplats, that.forlagOplats) && Objects.equals(doktitel, that.doktitel) && Objects.equals(pubOplats, that.pubOplats) && Objects.equals(plats, that.plats)
			&& Objects.equals(kommentPubl, that.kommentPubl) && Objects.equals(filLiten, that.filLiten) && Objects.equals(filStor, that.filStor)
			&& Objects.equals(filTxt, that.filTxt) && Objects.equals(xmltext, that.xmltext);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publId, filnamn, publiktyp, datum, tidtitel, tidnr, tidsida, forlagOplats, doktitel, pubOplats, plats, kommentPubl, filLiten, filStor, filTxt, xmltext);
	}

	@Override
	public String toString() {
		return "Publication{" +
			"publId=" + publId +
			", filnamn='" + filnamn + '\'' +
			", publiktyp='" + publiktyp + '\'' +
			", datum='" + datum + '\'' +
			", tidtitel='" + tidtitel + '\'' +
			", tidnr='" + tidnr + '\'' +
			", tidsida='" + tidsida + '\'' +
			", forlagOplats='" + forlagOplats + '\'' +
			", doktitel='" + doktitel + '\'' +
			", pubOplats='" + pubOplats + '\'' +
			", plats='" + plats + '\'' +
			", kommentPubl='" + kommentPubl + '\'' +
			", filLiten='" + filLiten + '\'' +
			", filStor='" + filStor + '\'' +
			", filTxt='" + filTxt + '\'' +
			", xmltext='" + xmltext + '\'' +
			'}';
	}
}
