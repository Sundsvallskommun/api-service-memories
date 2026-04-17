package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Photo / Foto model")
public class Foto {

	@Schema(description = "Photo ID", examples = "1234")
	private Integer fotoId;

	@Schema(description = "Original filename from archive")
	private String filnamn;

	@Schema(description = "Accession number")
	private String accNr;

	@Schema(description = "Reference code (negative number)")
	private String refKod;

	@Schema(description = "Inventory number (RAÄ-nr)")
	private String inventNr;

	@Schema(description = "Earlier reference number")
	private String tidigNr;

	@Schema(description = "Document title / description")
	private String doktitel;

	@Schema(description = "Subject keyword")
	private String sakord;

	@Schema(description = "Comment / description")
	private String kommentFf;

	@Schema(description = "Earliest date of time period", examples = "1920")
	private String tidig;

	@Schema(description = "Latest date of time period", examples = "1925")
	private String senast;

	@Schema(description = "Observation date")
	private String obsdatum;

	@Schema(description = "Indeterminable place (free-text fallback from F_OPLATS)")
	private String fotoOplats;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over fotoOplats when set)")
	private String plats;

	@Schema(description = "Storage location")
	private String forplats;

	@Schema(description = "Object type", examples = "Foto")
	private String objtyp;

	@Schema(description = "Black & white / colour")
	private String svvitfarg;

	@Schema(description = "Negative / positive")
	private String negPos;

	@Schema(description = "Transmissive / reflective")
	private String genPas;

	@Schema(description = "Image carrier")
	private String bildbar;

	@Schema(description = "Material")
	private String material;

	@Schema(description = "Technique")
	private String teknik;

	@Schema(description = "Function")
	private String funktion;

	@Schema(description = "Height (cm)")
	private String hojd;

	@Schema(description = "Width (cm)")
	private String bredd;

	@Schema(description = "Diameter")
	private String diam;

	@Schema(description = "Framed / mounted")
	private String ram;

	@Schema(description = "Condition category")
	private String tillSkat;

	@Schema(description = "Condition assessment")
	private String tillstand;

	@Schema(description = "Observer name")
	private String obsnamn;

	@Schema(description = "Treatment / action taken")
	private String atgard;

	@Schema(description = "Treatment date")
	private String atdDatum;

	@Schema(description = "Signature")
	private String sign;

	@Schema(description = "Image rights")
	private String givRattigh;

	@Schema(description = "Restrictions (Yes/No)")
	private String givForbeh;

	@Schema(description = "Restriction comment")
	private String anvando;

	@Schema(description = "Comment about creator/donor")
	private String kommentUpph;

	@Schema(description = "Thumbnail file name")
	private String filLiten;

	@Schema(description = "Large image file name")
	private String filStor;

	public static Foto create() {
		return new Foto();
	}

	public Integer getFotoId() {
		return fotoId;
	}

	public void setFotoId(final Integer fotoId) {
		this.fotoId = fotoId;
	}

	public Foto withFotoId(final Integer fotoId) {
		this.fotoId = fotoId;
		return this;
	}

	public String getFilnamn() {
		return filnamn;
	}

	public void setFilnamn(final String filnamn) {
		this.filnamn = filnamn;
	}

	public Foto withFilnamn(final String filnamn) {
		this.filnamn = filnamn;
		return this;
	}

	public String getAccNr() {
		return accNr;
	}

	public void setAccNr(final String accNr) {
		this.accNr = accNr;
	}

	public Foto withAccNr(final String accNr) {
		this.accNr = accNr;
		return this;
	}

	public String getRefKod() {
		return refKod;
	}

	public void setRefKod(final String refKod) {
		this.refKod = refKod;
	}

	public Foto withRefKod(final String refKod) {
		this.refKod = refKod;
		return this;
	}

	public String getInventNr() {
		return inventNr;
	}

	public void setInventNr(final String inventNr) {
		this.inventNr = inventNr;
	}

	public Foto withInventNr(final String inventNr) {
		this.inventNr = inventNr;
		return this;
	}

	public String getTidigNr() {
		return tidigNr;
	}

	public void setTidigNr(final String tidigNr) {
		this.tidigNr = tidigNr;
	}

	public Foto withTidigNr(final String tidigNr) {
		this.tidigNr = tidigNr;
		return this;
	}

	public String getDoktitel() {
		return doktitel;
	}

	public void setDoktitel(final String doktitel) {
		this.doktitel = doktitel;
	}

	public Foto withDoktitel(final String doktitel) {
		this.doktitel = doktitel;
		return this;
	}

	public String getSakord() {
		return sakord;
	}

	public void setSakord(final String sakord) {
		this.sakord = sakord;
	}

	public Foto withSakord(final String sakord) {
		this.sakord = sakord;
		return this;
	}

	public String getKommentFf() {
		return kommentFf;
	}

	public void setKommentFf(final String kommentFf) {
		this.kommentFf = kommentFf;
	}

	public Foto withKommentFf(final String kommentFf) {
		this.kommentFf = kommentFf;
		return this;
	}

	public String getTidig() {
		return tidig;
	}

	public void setTidig(final String tidig) {
		this.tidig = tidig;
	}

	public Foto withTidig(final String tidig) {
		this.tidig = tidig;
		return this;
	}

	public String getSenast() {
		return senast;
	}

	public void setSenast(final String senast) {
		this.senast = senast;
	}

	public Foto withSenast(final String senast) {
		this.senast = senast;
		return this;
	}

	public String getObsdatum() {
		return obsdatum;
	}

	public void setObsdatum(final String obsdatum) {
		this.obsdatum = obsdatum;
	}

	public Foto withObsdatum(final String obsdatum) {
		this.obsdatum = obsdatum;
		return this;
	}

	public String getFotoOplats() {
		return fotoOplats;
	}

	public void setFotoOplats(final String fotoOplats) {
		this.fotoOplats = fotoOplats;
	}

	public Foto withFotoOplats(final String fotoOplats) {
		this.fotoOplats = fotoOplats;
		return this;
	}

	public String getPlats() {
		return plats;
	}

	public void setPlats(final String plats) {
		this.plats = plats;
	}

	public Foto withPlats(final String plats) {
		this.plats = plats;
		return this;
	}

	public String getForplats() {
		return forplats;
	}

	public void setForplats(final String forplats) {
		this.forplats = forplats;
	}

	public Foto withForplats(final String forplats) {
		this.forplats = forplats;
		return this;
	}

	public String getObjtyp() {
		return objtyp;
	}

	public void setObjtyp(final String objtyp) {
		this.objtyp = objtyp;
	}

	public Foto withObjtyp(final String objtyp) {
		this.objtyp = objtyp;
		return this;
	}

	public String getSvvitfarg() {
		return svvitfarg;
	}

	public void setSvvitfarg(final String svvitfarg) {
		this.svvitfarg = svvitfarg;
	}

	public Foto withSvvitfarg(final String svvitfarg) {
		this.svvitfarg = svvitfarg;
		return this;
	}

	public String getNegPos() {
		return negPos;
	}

	public void setNegPos(final String negPos) {
		this.negPos = negPos;
	}

	public Foto withNegPos(final String negPos) {
		this.negPos = negPos;
		return this;
	}

	public String getGenPas() {
		return genPas;
	}

	public void setGenPas(final String genPas) {
		this.genPas = genPas;
	}

	public Foto withGenPas(final String genPas) {
		this.genPas = genPas;
		return this;
	}

	public String getBildbar() {
		return bildbar;
	}

	public void setBildbar(final String bildbar) {
		this.bildbar = bildbar;
	}

	public Foto withBildbar(final String bildbar) {
		this.bildbar = bildbar;
		return this;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(final String material) {
		this.material = material;
	}

	public Foto withMaterial(final String material) {
		this.material = material;
		return this;
	}

	public String getTeknik() {
		return teknik;
	}

	public void setTeknik(final String teknik) {
		this.teknik = teknik;
	}

	public Foto withTeknik(final String teknik) {
		this.teknik = teknik;
		return this;
	}

	public String getFunktion() {
		return funktion;
	}

	public void setFunktion(final String funktion) {
		this.funktion = funktion;
	}

	public Foto withFunktion(final String funktion) {
		this.funktion = funktion;
		return this;
	}

	public String getHojd() {
		return hojd;
	}

	public void setHojd(final String hojd) {
		this.hojd = hojd;
	}

	public Foto withHojd(final String hojd) {
		this.hojd = hojd;
		return this;
	}

	public String getBredd() {
		return bredd;
	}

	public void setBredd(final String bredd) {
		this.bredd = bredd;
	}

	public Foto withBredd(final String bredd) {
		this.bredd = bredd;
		return this;
	}

	public String getDiam() {
		return diam;
	}

	public void setDiam(final String diam) {
		this.diam = diam;
	}

	public Foto withDiam(final String diam) {
		this.diam = diam;
		return this;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(final String ram) {
		this.ram = ram;
	}

	public Foto withRam(final String ram) {
		this.ram = ram;
		return this;
	}

	public String getTillSkat() {
		return tillSkat;
	}

	public void setTillSkat(final String tillSkat) {
		this.tillSkat = tillSkat;
	}

	public Foto withTillSkat(final String tillSkat) {
		this.tillSkat = tillSkat;
		return this;
	}

	public String getTillstand() {
		return tillstand;
	}

	public void setTillstand(final String tillstand) {
		this.tillstand = tillstand;
	}

	public Foto withTillstand(final String tillstand) {
		this.tillstand = tillstand;
		return this;
	}

	public String getObsnamn() {
		return obsnamn;
	}

	public void setObsnamn(final String obsnamn) {
		this.obsnamn = obsnamn;
	}

	public Foto withObsnamn(final String obsnamn) {
		this.obsnamn = obsnamn;
		return this;
	}

	public String getAtgard() {
		return atgard;
	}

	public void setAtgard(final String atgard) {
		this.atgard = atgard;
	}

	public Foto withAtgard(final String atgard) {
		this.atgard = atgard;
		return this;
	}

	public String getAtdDatum() {
		return atdDatum;
	}

	public void setAtdDatum(final String atdDatum) {
		this.atdDatum = atdDatum;
	}

	public Foto withAtdDatum(final String atdDatum) {
		this.atdDatum = atdDatum;
		return this;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(final String sign) {
		this.sign = sign;
	}

	public Foto withSign(final String sign) {
		this.sign = sign;
		return this;
	}

	public String getGivRattigh() {
		return givRattigh;
	}

	public void setGivRattigh(final String givRattigh) {
		this.givRattigh = givRattigh;
	}

	public Foto withGivRattigh(final String givRattigh) {
		this.givRattigh = givRattigh;
		return this;
	}

	public String getGivForbeh() {
		return givForbeh;
	}

	public void setGivForbeh(final String givForbeh) {
		this.givForbeh = givForbeh;
	}

	public Foto withGivForbeh(final String givForbeh) {
		this.givForbeh = givForbeh;
		return this;
	}

	public String getAnvando() {
		return anvando;
	}

	public void setAnvando(final String anvando) {
		this.anvando = anvando;
	}

	public Foto withAnvando(final String anvando) {
		this.anvando = anvando;
		return this;
	}

	public String getKommentUpph() {
		return kommentUpph;
	}

	public void setKommentUpph(final String kommentUpph) {
		this.kommentUpph = kommentUpph;
	}

	public Foto withKommentUpph(final String kommentUpph) {
		this.kommentUpph = kommentUpph;
		return this;
	}

	public String getFilLiten() {
		return filLiten;
	}

	public void setFilLiten(final String filLiten) {
		this.filLiten = filLiten;
	}

	public Foto withFilLiten(final String filLiten) {
		this.filLiten = filLiten;
		return this;
	}

	public String getFilStor() {
		return filStor;
	}

	public void setFilStor(final String filStor) {
		this.filStor = filStor;
	}

	public Foto withFilStor(final String filStor) {
		this.filStor = filStor;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Foto that = (Foto) o;
		return Objects.equals(fotoId, that.fotoId) && Objects.equals(filnamn, that.filnamn) && Objects.equals(accNr, that.accNr) && Objects.equals(refKod, that.refKod)
			&& Objects.equals(inventNr, that.inventNr) && Objects.equals(tidigNr, that.tidigNr) && Objects.equals(doktitel, that.doktitel) && Objects.equals(sakord, that.sakord)
			&& Objects.equals(kommentFf, that.kommentFf) && Objects.equals(tidig, that.tidig) && Objects.equals(senast, that.senast) && Objects.equals(obsdatum, that.obsdatum)
			&& Objects.equals(fotoOplats, that.fotoOplats) && Objects.equals(forplats, that.forplats) && Objects.equals(objtyp, that.objtyp) && Objects.equals(svvitfarg, that.svvitfarg)
			&& Objects.equals(negPos, that.negPos) && Objects.equals(genPas, that.genPas) && Objects.equals(bildbar, that.bildbar) && Objects.equals(material, that.material)
			&& Objects.equals(teknik, that.teknik) && Objects.equals(funktion, that.funktion) && Objects.equals(hojd, that.hojd) && Objects.equals(bredd, that.bredd)
			&& Objects.equals(diam, that.diam) && Objects.equals(ram, that.ram) && Objects.equals(tillSkat, that.tillSkat) && Objects.equals(tillstand, that.tillstand)
			&& Objects.equals(obsnamn, that.obsnamn) && Objects.equals(atgard, that.atgard) && Objects.equals(atdDatum, that.atdDatum) && Objects.equals(sign, that.sign)
			&& Objects.equals(givRattigh, that.givRattigh) && Objects.equals(givForbeh, that.givForbeh) && Objects.equals(anvando, that.anvando) && Objects.equals(kommentUpph, that.kommentUpph)
			&& Objects.equals(filLiten, that.filLiten) && Objects.equals(filStor, that.filStor) && Objects.equals(plats, that.plats);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fotoId, filnamn, accNr, refKod, inventNr, tidigNr, doktitel, sakord, kommentFf, tidig, senast, obsdatum, fotoOplats, forplats, objtyp, svvitfarg,
			negPos, genPas, bildbar, material, teknik, funktion, hojd, bredd, diam, ram, tillSkat, tillstand, obsnamn, atgard, atdDatum, sign,
			givRattigh, givForbeh, anvando, kommentUpph, filLiten, filStor, plats);
	}

	@Override
	public String toString() {
		return "Foto{" +
			"fotoId=" + fotoId +
			", filnamn='" + filnamn + '\'' +
			", accNr='" + accNr + '\'' +
			", refKod='" + refKod + '\'' +
			", inventNr='" + inventNr + '\'' +
			", tidigNr='" + tidigNr + '\'' +
			", doktitel='" + doktitel + '\'' +
			", sakord='" + sakord + '\'' +
			", kommentFf='" + kommentFf + '\'' +
			", tidig='" + tidig + '\'' +
			", senast='" + senast + '\'' +
			", obsdatum='" + obsdatum + '\'' +
			", fotoOplats='" + fotoOplats + '\'' +
			", forplats='" + forplats + '\'' +
			", objtyp='" + objtyp + '\'' +
			", svvitfarg='" + svvitfarg + '\'' +
			", negPos='" + negPos + '\'' +
			", genPas='" + genPas + '\'' +
			", bildbar='" + bildbar + '\'' +
			", material='" + material + '\'' +
			", teknik='" + teknik + '\'' +
			", funktion='" + funktion + '\'' +
			", hojd='" + hojd + '\'' +
			", bredd='" + bredd + '\'' +
			", diam='" + diam + '\'' +
			", ram='" + ram + '\'' +
			", tillSkat='" + tillSkat + '\'' +
			", tillstand='" + tillstand + '\'' +
			", obsnamn='" + obsnamn + '\'' +
			", atgard='" + atgard + '\'' +
			", atdDatum='" + atdDatum + '\'' +
			", sign='" + sign + '\'' +
			", givRattigh='" + givRattigh + '\'' +
			", givForbeh='" + givForbeh + '\'' +
			", anvando='" + anvando + '\'' +
			", kommentUpph='" + kommentUpph + '\'' +
			", filLiten='" + filLiten + '\'' +
			", filStor='" + filStor + '\'' +
			", plats='" + plats + '\'' +
			'}';
	}
}
