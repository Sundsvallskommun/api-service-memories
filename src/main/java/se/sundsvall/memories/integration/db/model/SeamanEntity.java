package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entity for the {@code SJOMAN} seamen's-register table. The table has no {@code OPTIONS} (publish) column and no
 * gender column, so seamen carry no publish or gender filter. The primary key is {@code POSTNR}.
 */
@Entity
@Table(name = "SJOMAN")
public class SeamanEntity {

	@Id
	@Column(name = "POSTNR")
	private Integer id;

	@Column(name = "FORNAMN", length = 50)
	private String firstName;

	@Column(name = "EFTERNAMN1", length = 40)
	private String lastName1;

	@Column(name = "EFTERNAMN2", length = 40)
	private String lastName2;

	@Column(name = "IDNR")
	private Integer idNumber;

	@Column(name = "FODDAT", length = 20)
	private String birthDate;

	@Column(name = "ALDER", length = 20)
	private String age;

	@Column(name = "FODFORS", length = 80)
	private String birthParish;

	@Column(name = "FODPLATS", length = 80)
	private String birthPlace;

	@Column(name = "HEMFORS", length = 80)
	private String homeParish;

	@Column(name = "HEMPLATS", length = 80)
	private String homePlace;

	@Column(name = "CIVILSTAND", length = 10)
	private String civilStatus;

	@Column(name = "FAR", length = 100)
	private String father;

	@Column(name = "MOR", length = 80)
	private String mother;

	@Column(name = "SJOMANSHUS", length = 40)
	private String seamensHouse;

	@Column(name = "INSKRNR", length = 14)
	private String enrollmentNumber;

	@Column(name = "INSKRDAT", length = 20)
	private String enrollmentDate;

	@Column(name = "BEFATTN", length = 60)
	private String rank;

	@Column(name = "PAMONSTORT", length = 60)
	private String signOnPlace;

	@Column(name = "PAMONSTDAT", length = 20)
	private String signOnDate;

	@Column(name = "AVMONSTORT", length = 60)
	private String signOffPlace;

	@Column(name = "AVMONDAT", length = 20)
	private String signOffDate;

	@Column(name = "FARTYG", length = 50)
	private String ship;

	@Column(name = "HEMMAHAMN", length = 60)
	private String homePort;

	@Column(name = "TYP", length = 50)
	private String shipType;

	@Column(name = "REDARE", length = 80)
	private String shipOwner;

	@Column(name = "KAPTEN", length = 80)
	private String captain;

	@Column(name = "DESTINATION", length = 80)
	private String destination;

	@Column(name = "OVRIGT", length = 2000)
	private String other;

	@Column(name = "ANM", length = 80)
	private String note;

	@Column(name = "ARKIV", length = 100)
	private String archive;

	@Column(name = "VOLYM", length = 30)
	private String volume;

	@Column(name = "ARKISNR", length = 22)
	private String archiveNumber;

	@Column(name = "SIDA", length = 30)
	private String page;

	public static SeamanEntity create() {
		return new SeamanEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public SeamanEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public SeamanEntity withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName1() {
		return lastName1;
	}

	public void setLastName1(final String lastName1) {
		this.lastName1 = lastName1;
	}

	public SeamanEntity withLastName1(final String lastName1) {
		this.lastName1 = lastName1;
		return this;
	}

	public String getLastName2() {
		return lastName2;
	}

	public void setLastName2(final String lastName2) {
		this.lastName2 = lastName2;
	}

	public SeamanEntity withLastName2(final String lastName2) {
		this.lastName2 = lastName2;
		return this;
	}

	public Integer getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(final Integer idNumber) {
		this.idNumber = idNumber;
	}

	public SeamanEntity withIdNumber(final Integer idNumber) {
		this.idNumber = idNumber;
		return this;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final String birthDate) {
		this.birthDate = birthDate;
	}

	public SeamanEntity withBirthDate(final String birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public String getAge() {
		return age;
	}

	public void setAge(final String age) {
		this.age = age;
	}

	public SeamanEntity withAge(final String age) {
		this.age = age;
		return this;
	}

	public String getBirthParish() {
		return birthParish;
	}

	public void setBirthParish(final String birthParish) {
		this.birthParish = birthParish;
	}

	public SeamanEntity withBirthParish(final String birthParish) {
		this.birthParish = birthParish;
		return this;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public SeamanEntity withBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
		return this;
	}

	public String getHomeParish() {
		return homeParish;
	}

	public void setHomeParish(final String homeParish) {
		this.homeParish = homeParish;
	}

	public SeamanEntity withHomeParish(final String homeParish) {
		this.homeParish = homeParish;
		return this;
	}

	public String getHomePlace() {
		return homePlace;
	}

	public void setHomePlace(final String homePlace) {
		this.homePlace = homePlace;
	}

	public SeamanEntity withHomePlace(final String homePlace) {
		this.homePlace = homePlace;
		return this;
	}

	public String getCivilStatus() {
		return civilStatus;
	}

	public void setCivilStatus(final String civilStatus) {
		this.civilStatus = civilStatus;
	}

	public SeamanEntity withCivilStatus(final String civilStatus) {
		this.civilStatus = civilStatus;
		return this;
	}

	public String getFather() {
		return father;
	}

	public void setFather(final String father) {
		this.father = father;
	}

	public SeamanEntity withFather(final String father) {
		this.father = father;
		return this;
	}

	public String getMother() {
		return mother;
	}

	public void setMother(final String mother) {
		this.mother = mother;
	}

	public SeamanEntity withMother(final String mother) {
		this.mother = mother;
		return this;
	}

	public String getSeamensHouse() {
		return seamensHouse;
	}

	public void setSeamensHouse(final String seamensHouse) {
		this.seamensHouse = seamensHouse;
	}

	public SeamanEntity withSeamensHouse(final String seamensHouse) {
		this.seamensHouse = seamensHouse;
		return this;
	}

	public String getEnrollmentNumber() {
		return enrollmentNumber;
	}

	public void setEnrollmentNumber(final String enrollmentNumber) {
		this.enrollmentNumber = enrollmentNumber;
	}

	public SeamanEntity withEnrollmentNumber(final String enrollmentNumber) {
		this.enrollmentNumber = enrollmentNumber;
		return this;
	}

	public String getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(final String enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public SeamanEntity withEnrollmentDate(final String enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
		return this;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(final String rank) {
		this.rank = rank;
	}

	public SeamanEntity withRank(final String rank) {
		this.rank = rank;
		return this;
	}

	public String getSignOnPlace() {
		return signOnPlace;
	}

	public void setSignOnPlace(final String signOnPlace) {
		this.signOnPlace = signOnPlace;
	}

	public SeamanEntity withSignOnPlace(final String signOnPlace) {
		this.signOnPlace = signOnPlace;
		return this;
	}

	public String getSignOnDate() {
		return signOnDate;
	}

	public void setSignOnDate(final String signOnDate) {
		this.signOnDate = signOnDate;
	}

	public SeamanEntity withSignOnDate(final String signOnDate) {
		this.signOnDate = signOnDate;
		return this;
	}

	public String getSignOffPlace() {
		return signOffPlace;
	}

	public void setSignOffPlace(final String signOffPlace) {
		this.signOffPlace = signOffPlace;
	}

	public SeamanEntity withSignOffPlace(final String signOffPlace) {
		this.signOffPlace = signOffPlace;
		return this;
	}

	public String getSignOffDate() {
		return signOffDate;
	}

	public void setSignOffDate(final String signOffDate) {
		this.signOffDate = signOffDate;
	}

	public SeamanEntity withSignOffDate(final String signOffDate) {
		this.signOffDate = signOffDate;
		return this;
	}

	public String getShip() {
		return ship;
	}

	public void setShip(final String ship) {
		this.ship = ship;
	}

	public SeamanEntity withShip(final String ship) {
		this.ship = ship;
		return this;
	}

	public String getHomePort() {
		return homePort;
	}

	public void setHomePort(final String homePort) {
		this.homePort = homePort;
	}

	public SeamanEntity withHomePort(final String homePort) {
		this.homePort = homePort;
		return this;
	}

	public String getShipType() {
		return shipType;
	}

	public void setShipType(final String shipType) {
		this.shipType = shipType;
	}

	public SeamanEntity withShipType(final String shipType) {
		this.shipType = shipType;
		return this;
	}

	public String getShipOwner() {
		return shipOwner;
	}

	public void setShipOwner(final String shipOwner) {
		this.shipOwner = shipOwner;
	}

	public SeamanEntity withShipOwner(final String shipOwner) {
		this.shipOwner = shipOwner;
		return this;
	}

	public String getCaptain() {
		return captain;
	}

	public void setCaptain(final String captain) {
		this.captain = captain;
	}

	public SeamanEntity withCaptain(final String captain) {
		this.captain = captain;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
	}

	public SeamanEntity withDestination(final String destination) {
		this.destination = destination;
		return this;
	}

	public String getOther() {
		return other;
	}

	public void setOther(final String other) {
		this.other = other;
	}

	public SeamanEntity withOther(final String other) {
		this.other = other;
		return this;
	}

	public String getNote() {
		return note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public SeamanEntity withNote(final String note) {
		this.note = note;
		return this;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(final String archive) {
		this.archive = archive;
	}

	public SeamanEntity withArchive(final String archive) {
		this.archive = archive;
		return this;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(final String volume) {
		this.volume = volume;
	}

	public SeamanEntity withVolume(final String volume) {
		this.volume = volume;
		return this;
	}

	public String getArchiveNumber() {
		return archiveNumber;
	}

	public void setArchiveNumber(final String archiveNumber) {
		this.archiveNumber = archiveNumber;
	}

	public SeamanEntity withArchiveNumber(final String archiveNumber) {
		this.archiveNumber = archiveNumber;
		return this;
	}

	public String getPage() {
		return page;
	}

	public void setPage(final String page) {
		this.page = page;
	}

	public SeamanEntity withPage(final String page) {
		this.page = page;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final SeamanEntity that = (SeamanEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName1, that.lastName1) && Objects.equals(lastName2, that.lastName2)
			&& Objects.equals(idNumber, that.idNumber) && Objects.equals(birthDate, that.birthDate) && Objects.equals(age, that.age) && Objects.equals(birthParish, that.birthParish)
			&& Objects.equals(birthPlace, that.birthPlace) && Objects.equals(homeParish, that.homeParish) && Objects.equals(homePlace, that.homePlace) && Objects.equals(civilStatus, that.civilStatus)
			&& Objects.equals(father, that.father) && Objects.equals(mother, that.mother) && Objects.equals(seamensHouse, that.seamensHouse) && Objects.equals(enrollmentNumber, that.enrollmentNumber)
			&& Objects.equals(enrollmentDate, that.enrollmentDate) && Objects.equals(rank, that.rank) && Objects.equals(signOnPlace, that.signOnPlace) && Objects.equals(signOnDate, that.signOnDate)
			&& Objects.equals(signOffPlace, that.signOffPlace) && Objects.equals(signOffDate, that.signOffDate) && Objects.equals(ship, that.ship) && Objects.equals(homePort, that.homePort)
			&& Objects.equals(shipType, that.shipType) && Objects.equals(shipOwner, that.shipOwner) && Objects.equals(captain, that.captain) && Objects.equals(destination, that.destination)
			&& Objects.equals(other, that.other) && Objects.equals(note, that.note) && Objects.equals(archive, that.archive) && Objects.equals(volume, that.volume)
			&& Objects.equals(archiveNumber, that.archiveNumber) && Objects.equals(page, that.page);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName1, lastName2, idNumber, birthDate, age, birthParish, birthPlace, homeParish, homePlace, civilStatus, father, mother, seamensHouse, enrollmentNumber,
			enrollmentDate, rank, signOnPlace, signOnDate, signOffPlace, signOffDate, ship, homePort, shipType, shipOwner, captain, destination, other, note, archive, volume, archiveNumber, page);
	}

	@Override
	public String toString() {
		return "SeamanEntity{" +
			"id=" + id +
			", firstName='" + firstName + '\'' +
			", lastName1='" + lastName1 + '\'' +
			", lastName2='" + lastName2 + '\'' +
			", idNumber=" + idNumber +
			", birthDate='" + birthDate + '\'' +
			", age='" + age + '\'' +
			", birthParish='" + birthParish + '\'' +
			", birthPlace='" + birthPlace + '\'' +
			", homeParish='" + homeParish + '\'' +
			", homePlace='" + homePlace + '\'' +
			", civilStatus='" + civilStatus + '\'' +
			", father='" + father + '\'' +
			", mother='" + mother + '\'' +
			", seamensHouse='" + seamensHouse + '\'' +
			", enrollmentNumber='" + enrollmentNumber + '\'' +
			", enrollmentDate='" + enrollmentDate + '\'' +
			", rank='" + rank + '\'' +
			", signOnPlace='" + signOnPlace + '\'' +
			", signOnDate='" + signOnDate + '\'' +
			", signOffPlace='" + signOffPlace + '\'' +
			", signOffDate='" + signOffDate + '\'' +
			", ship='" + ship + '\'' +
			", homePort='" + homePort + '\'' +
			", shipType='" + shipType + '\'' +
			", shipOwner='" + shipOwner + '\'' +
			", captain='" + captain + '\'' +
			", destination='" + destination + '\'' +
			", other='" + other + '\'' +
			", note='" + note + '\'' +
			", archive='" + archive + '\'' +
			", volume='" + volume + '\'' +
			", archiveNumber='" + archiveNumber + '\'' +
			", page='" + page + '\'' +
			'}';
	}
}
