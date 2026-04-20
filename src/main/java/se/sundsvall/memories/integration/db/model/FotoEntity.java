package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "FOTO")
public class FotoEntity {

	@Id
	@Column(name = "F_ID")
	private Integer fotoId;

	@Column(name = "F_T_ID")
	private Integer fotoTId;

	@Column(name = "FILNAMN", length = 256)
	private String filnamn;

	@Column(name = "ACCNR", length = 15)
	private String accNr;

	@Column(name = "REFKOD", length = 15)
	private String refKod;

	@Column(name = "INVENTNR", length = 15)
	private String inventNr;

	@Column(name = "TIDIGNR", length = 30)
	private String tidigNr;

	@Column(name = "DOKTITEL", length = 256)
	private String doktitel;

	@Column(name = "SAKORD", length = 128)
	private String sakord;

	@Column(name = "KOMMENT_FF", length = 4000)
	private String kommentFf;

	@Column(name = "TIDIG", length = 10)
	private String tidig;

	@Column(name = "SENAST", length = 10)
	private String senast;

	@Column(name = "OBSDATUM", length = 10)
	private String obsdatum;

	@Column(name = "F_OPLATS", length = 64)
	private String fotoOplats;

	@Column(name = "FORPLATS", length = 35)
	private String forplats;

	@Column(name = "OBJTYP", length = 10)
	private String objtyp;

	@Column(name = "SVVITFARG", length = 10)
	private String svvitfarg;

	@Column(name = "NEGPOS", length = 10)
	private String negPos;

	@Column(name = "GENPAS", length = 10)
	private String genPas;

	@Column(name = "BILDBAR", length = 10)
	private String bildbar;

	@Column(name = "MATERIAL", length = 50)
	private String material;

	@Column(name = "TEKNIK", length = 50)
	private String teknik;

	@Column(name = "FUNKTION", length = 500)
	private String funktion;

	@Column(name = "HOJD", length = 20)
	private String hojd;

	@Column(name = "BREDD", length = 20)
	private String bredd;

	@Column(name = "DIAM", length = 20)
	private String diam;

	@Column(name = "RAM", length = 10)
	private String ram;

	@Column(name = "TILLSKAT", length = 60)
	private String tillSkat;

	@Column(name = "TILLSTAND", length = 1000)
	private String tillstand;

	@Column(name = "OBSNAMN", length = 50)
	private String obsnamn;

	@Column(name = "ATGARD", length = 1000)
	private String atgard;

	@Column(name = "ATDDATUM", length = 10)
	private String atdDatum;

	@Column(name = "SIGN", length = 30)
	private String sign;

	@Column(name = "G_RATTIGH", length = 1000)
	private String givRattigh;

	@Column(name = "G_FORBEH", length = 3)
	private String givForbeh;

	@Column(name = "ANVANDO", length = 1000)
	private String anvando;

	@Column(name = "KOMMENT_UPPH", length = 1000)
	private String kommentUpph;

	@Column(name = "FIL_LITEN", length = 256)
	private String filLiten;

	@Column(name = "FIL_STOR", length = 256)
	private String filStor;

	@Column(name = "FIL_ORIGINAL", length = 256)
	private String filOriginal;

	@Column(name = "NODEID")
	private Integer nodeId;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static FotoEntity create() {
		return new FotoEntity();
	}

	public Integer getFotoId() {
		return fotoId;
	}

	public void setFotoId(final Integer fotoId) {
		this.fotoId = fotoId;
	}

	public FotoEntity withFotoId(final Integer fotoId) {
		this.fotoId = fotoId;
		return this;
	}

	public Integer getFotoTId() {
		return fotoTId;
	}

	public void setFotoTId(final Integer fotoTId) {
		this.fotoTId = fotoTId;
	}

	public FotoEntity withFotoTId(final Integer fotoTId) {
		this.fotoTId = fotoTId;
		return this;
	}

	public String getFilnamn() {
		return filnamn;
	}

	public void setFilnamn(final String filnamn) {
		this.filnamn = filnamn;
	}

	public FotoEntity withFilnamn(final String filnamn) {
		this.filnamn = filnamn;
		return this;
	}

	public String getAccNr() {
		return accNr;
	}

	public void setAccNr(final String accNr) {
		this.accNr = accNr;
	}

	public FotoEntity withAccNr(final String accNr) {
		this.accNr = accNr;
		return this;
	}

	public String getRefKod() {
		return refKod;
	}

	public void setRefKod(final String refKod) {
		this.refKod = refKod;
	}

	public FotoEntity withRefKod(final String refKod) {
		this.refKod = refKod;
		return this;
	}

	public String getInventNr() {
		return inventNr;
	}

	public void setInventNr(final String inventNr) {
		this.inventNr = inventNr;
	}

	public FotoEntity withInventNr(final String inventNr) {
		this.inventNr = inventNr;
		return this;
	}

	public String getTidigNr() {
		return tidigNr;
	}

	public void setTidigNr(final String tidigNr) {
		this.tidigNr = tidigNr;
	}

	public FotoEntity withTidigNr(final String tidigNr) {
		this.tidigNr = tidigNr;
		return this;
	}

	public String getDoktitel() {
		return doktitel;
	}

	public void setDoktitel(final String doktitel) {
		this.doktitel = doktitel;
	}

	public FotoEntity withDoktitel(final String doktitel) {
		this.doktitel = doktitel;
		return this;
	}

	public String getSakord() {
		return sakord;
	}

	public void setSakord(final String sakord) {
		this.sakord = sakord;
	}

	public FotoEntity withSakord(final String sakord) {
		this.sakord = sakord;
		return this;
	}

	public String getKommentFf() {
		return kommentFf;
	}

	public void setKommentFf(final String kommentFf) {
		this.kommentFf = kommentFf;
	}

	public FotoEntity withKommentFf(final String kommentFf) {
		this.kommentFf = kommentFf;
		return this;
	}

	public String getTidig() {
		return tidig;
	}

	public void setTidig(final String tidig) {
		this.tidig = tidig;
	}

	public FotoEntity withTidig(final String tidig) {
		this.tidig = tidig;
		return this;
	}

	public String getSenast() {
		return senast;
	}

	public void setSenast(final String senast) {
		this.senast = senast;
	}

	public FotoEntity withSenast(final String senast) {
		this.senast = senast;
		return this;
	}

	public String getObsdatum() {
		return obsdatum;
	}

	public void setObsdatum(final String obsdatum) {
		this.obsdatum = obsdatum;
	}

	public FotoEntity withObsdatum(final String obsdatum) {
		this.obsdatum = obsdatum;
		return this;
	}

	public String getFotoOplats() {
		return fotoOplats;
	}

	public void setFotoOplats(final String fotoOplats) {
		this.fotoOplats = fotoOplats;
	}

	public FotoEntity withFotoOplats(final String fotoOplats) {
		this.fotoOplats = fotoOplats;
		return this;
	}

	public String getForplats() {
		return forplats;
	}

	public void setForplats(final String forplats) {
		this.forplats = forplats;
	}

	public FotoEntity withForplats(final String forplats) {
		this.forplats = forplats;
		return this;
	}

	public String getObjtyp() {
		return objtyp;
	}

	public void setObjtyp(final String objtyp) {
		this.objtyp = objtyp;
	}

	public FotoEntity withObjtyp(final String objtyp) {
		this.objtyp = objtyp;
		return this;
	}

	public String getSvvitfarg() {
		return svvitfarg;
	}

	public void setSvvitfarg(final String svvitfarg) {
		this.svvitfarg = svvitfarg;
	}

	public FotoEntity withSvvitfarg(final String svvitfarg) {
		this.svvitfarg = svvitfarg;
		return this;
	}

	public String getNegPos() {
		return negPos;
	}

	public void setNegPos(final String negPos) {
		this.negPos = negPos;
	}

	public FotoEntity withNegPos(final String negPos) {
		this.negPos = negPos;
		return this;
	}

	public String getGenPas() {
		return genPas;
	}

	public void setGenPas(final String genPas) {
		this.genPas = genPas;
	}

	public FotoEntity withGenPas(final String genPas) {
		this.genPas = genPas;
		return this;
	}

	public String getBildbar() {
		return bildbar;
	}

	public void setBildbar(final String bildbar) {
		this.bildbar = bildbar;
	}

	public FotoEntity withBildbar(final String bildbar) {
		this.bildbar = bildbar;
		return this;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(final String material) {
		this.material = material;
	}

	public FotoEntity withMaterial(final String material) {
		this.material = material;
		return this;
	}

	public String getTeknik() {
		return teknik;
	}

	public void setTeknik(final String teknik) {
		this.teknik = teknik;
	}

	public FotoEntity withTeknik(final String teknik) {
		this.teknik = teknik;
		return this;
	}

	public String getFunktion() {
		return funktion;
	}

	public void setFunktion(final String funktion) {
		this.funktion = funktion;
	}

	public FotoEntity withFunktion(final String funktion) {
		this.funktion = funktion;
		return this;
	}

	public String getHojd() {
		return hojd;
	}

	public void setHojd(final String hojd) {
		this.hojd = hojd;
	}

	public FotoEntity withHojd(final String hojd) {
		this.hojd = hojd;
		return this;
	}

	public String getBredd() {
		return bredd;
	}

	public void setBredd(final String bredd) {
		this.bredd = bredd;
	}

	public FotoEntity withBredd(final String bredd) {
		this.bredd = bredd;
		return this;
	}

	public String getDiam() {
		return diam;
	}

	public void setDiam(final String diam) {
		this.diam = diam;
	}

	public FotoEntity withDiam(final String diam) {
		this.diam = diam;
		return this;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(final String ram) {
		this.ram = ram;
	}

	public FotoEntity withRam(final String ram) {
		this.ram = ram;
		return this;
	}

	public String getTillSkat() {
		return tillSkat;
	}

	public void setTillSkat(final String tillSkat) {
		this.tillSkat = tillSkat;
	}

	public FotoEntity withTillSkat(final String tillSkat) {
		this.tillSkat = tillSkat;
		return this;
	}

	public String getTillstand() {
		return tillstand;
	}

	public void setTillstand(final String tillstand) {
		this.tillstand = tillstand;
	}

	public FotoEntity withTillstand(final String tillstand) {
		this.tillstand = tillstand;
		return this;
	}

	public String getObsnamn() {
		return obsnamn;
	}

	public void setObsnamn(final String obsnamn) {
		this.obsnamn = obsnamn;
	}

	public FotoEntity withObsnamn(final String obsnamn) {
		this.obsnamn = obsnamn;
		return this;
	}

	public String getAtgard() {
		return atgard;
	}

	public void setAtgard(final String atgard) {
		this.atgard = atgard;
	}

	public FotoEntity withAtgard(final String atgard) {
		this.atgard = atgard;
		return this;
	}

	public String getAtdDatum() {
		return atdDatum;
	}

	public void setAtdDatum(final String atdDatum) {
		this.atdDatum = atdDatum;
	}

	public FotoEntity withAtdDatum(final String atdDatum) {
		this.atdDatum = atdDatum;
		return this;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(final String sign) {
		this.sign = sign;
	}

	public FotoEntity withSign(final String sign) {
		this.sign = sign;
		return this;
	}

	public String getGivRattigh() {
		return givRattigh;
	}

	public void setGivRattigh(final String givRattigh) {
		this.givRattigh = givRattigh;
	}

	public FotoEntity withGivRattigh(final String givRattigh) {
		this.givRattigh = givRattigh;
		return this;
	}

	public String getGivForbeh() {
		return givForbeh;
	}

	public void setGivForbeh(final String givForbeh) {
		this.givForbeh = givForbeh;
	}

	public FotoEntity withGivForbeh(final String givForbeh) {
		this.givForbeh = givForbeh;
		return this;
	}

	public String getAnvando() {
		return anvando;
	}

	public void setAnvando(final String anvando) {
		this.anvando = anvando;
	}

	public FotoEntity withAnvando(final String anvando) {
		this.anvando = anvando;
		return this;
	}

	public String getKommentUpph() {
		return kommentUpph;
	}

	public void setKommentUpph(final String kommentUpph) {
		this.kommentUpph = kommentUpph;
	}

	public FotoEntity withKommentUpph(final String kommentUpph) {
		this.kommentUpph = kommentUpph;
		return this;
	}

	public String getFilLiten() {
		return filLiten;
	}

	public void setFilLiten(final String filLiten) {
		this.filLiten = filLiten;
	}

	public FotoEntity withFilLiten(final String filLiten) {
		this.filLiten = filLiten;
		return this;
	}

	public String getFilStor() {
		return filStor;
	}

	public void setFilStor(final String filStor) {
		this.filStor = filStor;
	}

	public FotoEntity withFilStor(final String filStor) {
		this.filStor = filStor;
		return this;
	}

	public String getFilOriginal() {
		return filOriginal;
	}

	public void setFilOriginal(final String filOriginal) {
		this.filOriginal = filOriginal;
	}

	public FotoEntity withFilOriginal(final String filOriginal) {
		this.filOriginal = filOriginal;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public FotoEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public FotoEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public FotoEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final FotoEntity that = (FotoEntity) o;
		return Objects.equals(fotoId, that.fotoId) && Objects.equals(fotoTId, that.fotoTId) && Objects.equals(filnamn, that.filnamn) && Objects.equals(accNr, that.accNr) && Objects.equals(refKod, that.refKod)
			&& Objects.equals(inventNr, that.inventNr) && Objects.equals(tidigNr, that.tidigNr) && Objects.equals(doktitel, that.doktitel) && Objects.equals(sakord, that.sakord)
			&& Objects.equals(kommentFf, that.kommentFf) && Objects.equals(tidig, that.tidig) && Objects.equals(senast, that.senast) && Objects.equals(obsdatum, that.obsdatum)
			&& Objects.equals(fotoOplats, that.fotoOplats) && Objects.equals(forplats, that.forplats) && Objects.equals(objtyp, that.objtyp) && Objects.equals(svvitfarg, that.svvitfarg)
			&& Objects.equals(negPos, that.negPos) && Objects.equals(genPas, that.genPas) && Objects.equals(bildbar, that.bildbar) && Objects.equals(material, that.material)
			&& Objects.equals(teknik, that.teknik) && Objects.equals(funktion, that.funktion) && Objects.equals(hojd, that.hojd) && Objects.equals(bredd, that.bredd)
			&& Objects.equals(diam, that.diam) && Objects.equals(ram, that.ram) && Objects.equals(tillSkat, that.tillSkat) && Objects.equals(tillstand, that.tillstand)
			&& Objects.equals(obsnamn, that.obsnamn) && Objects.equals(atgard, that.atgard) && Objects.equals(atdDatum, that.atdDatum) && Objects.equals(sign, that.sign)
			&& Objects.equals(givRattigh, that.givRattigh) && Objects.equals(givForbeh, that.givForbeh) && Objects.equals(anvando, that.anvando) && Objects.equals(kommentUpph, that.kommentUpph)
			&& Objects.equals(filLiten, that.filLiten) && Objects.equals(filStor, that.filStor) && Objects.equals(filOriginal, that.filOriginal)
			&& Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fotoId, fotoTId, filnamn, accNr, refKod, inventNr, tidigNr, doktitel, sakord, kommentFf, tidig, senast, obsdatum, fotoOplats, forplats, objtyp, svvitfarg, negPos, genPas,
			bildbar, material, teknik, funktion, hojd, bredd, diam, ram, tillSkat, tillstand, obsnamn, atgard, atdDatum, sign, givRattigh, givForbeh, anvando, kommentUpph,
			filLiten, filStor, filOriginal, nodeId, options, deletedDate);
	}

	@Override
	public String toString() {
		return "FotoEntity{" +
			"fotoId=" + fotoId +
			", fotoTId=" + fotoTId +
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
			", filOriginal='" + filOriginal + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
