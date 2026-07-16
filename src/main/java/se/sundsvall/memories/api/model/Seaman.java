package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Seaman (sjömanshus register) model")
public class Seaman {

	@Schema(description = "Record number (POSTNR)", examples = "123")
	private Integer id;

	@Schema(description = "First name", examples = "Anton")
	private String firstName;

	@Schema(description = "Last name (primary)", examples = "Nordin")
	private String lastName1;

	@Schema(description = "Last name (alternate)", examples = "Sjöberg")
	private String lastName2;

	@Schema(description = "Register id number", examples = "4711")
	private Integer idNumber;

	@Schema(description = "Birth date (stored as free text)", examples = "1852-03-14")
	private String birthDate;

	@Schema(description = "Age", examples = "28")
	private String age;

	@Schema(description = "Birth parish", examples = "Sundsvall")
	private String birthParish;

	@Schema(description = "Birth place", examples = "Sundsvall")
	private String birthPlace;

	@Schema(description = "Home parish", examples = "Njurunda")
	private String homeParish;

	@Schema(description = "Home place", examples = "Njurunda")
	private String homePlace;

	@Schema(description = "Civil status", examples = "Gift")
	private String civilStatus;

	@Schema(description = "Father", examples = "Erik Nordin")
	private String father;

	@Schema(description = "Mother", examples = "Anna Nordin")
	private String mother;

	@Schema(description = "Seamen's house", examples = "Sundsvalls sjömanshus")
	private String seamensHouse;

	@Schema(description = "Enrollment number", examples = "112")
	private String enrollmentNumber;

	@Schema(description = "Enrollment date", examples = "1875-04-01")
	private String enrollmentDate;

	@Schema(description = "Rank / position", examples = "Matros")
	private String rank;

	@Schema(description = "Sign-on place", examples = "Sundsvall")
	private String signOnPlace;

	@Schema(description = "Sign-on date", examples = "1876-05-01")
	private String signOnDate;

	@Schema(description = "Sign-off place", examples = "Göteborg")
	private String signOffPlace;

	@Schema(description = "Sign-off date", examples = "1877-09-01")
	private String signOffDate;

	@Schema(description = "Ship", examples = "Briggen Freja")
	private String ship;

	@Schema(description = "Home port", examples = "Sundsvall")
	private String homePort;

	@Schema(description = "Ship type", examples = "Brigg")
	private String shipType;

	@Schema(description = "Ship owner (redare)", examples = "Rederi AB Nord")
	private String shipOwner;

	@Schema(description = "Captain", examples = "Olof Berg")
	private String captain;

	@Schema(description = "Destination", examples = "London")
	private String destination;

	@Schema(description = "Other", examples = "Övrig anteckning")
	private String other;

	@Schema(description = "Note (anmärkning)", examples = "Avmönstrad")
	private String note;

	@Schema(description = "Archive", examples = "Sundsvalls sjömanshus arkiv")
	private String archive;

	@Schema(description = "Volume", examples = "A1:3")
	private String volume;

	@Schema(description = "Archive number", examples = "SE/HLA/1234")
	private String archiveNumber;

	@Schema(description = "Page", examples = "42")
	private String page;

	public static Seaman create() {
		return new Seaman();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Seaman withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public Seaman withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName1() {
		return lastName1;
	}

	public void setLastName1(final String lastName1) {
		this.lastName1 = lastName1;
	}

	public Seaman withLastName1(final String lastName1) {
		this.lastName1 = lastName1;
		return this;
	}

	public String getLastName2() {
		return lastName2;
	}

	public void setLastName2(final String lastName2) {
		this.lastName2 = lastName2;
	}

	public Seaman withLastName2(final String lastName2) {
		this.lastName2 = lastName2;
		return this;
	}

	public Integer getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(final Integer idNumber) {
		this.idNumber = idNumber;
	}

	public Seaman withIdNumber(final Integer idNumber) {
		this.idNumber = idNumber;
		return this;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final String birthDate) {
		this.birthDate = birthDate;
	}

	public Seaman withBirthDate(final String birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public String getAge() {
		return age;
	}

	public void setAge(final String age) {
		this.age = age;
	}

	public Seaman withAge(final String age) {
		this.age = age;
		return this;
	}

	public String getBirthParish() {
		return birthParish;
	}

	public void setBirthParish(final String birthParish) {
		this.birthParish = birthParish;
	}

	public Seaman withBirthParish(final String birthParish) {
		this.birthParish = birthParish;
		return this;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public Seaman withBirthPlace(final String birthPlace) {
		this.birthPlace = birthPlace;
		return this;
	}

	public String getHomeParish() {
		return homeParish;
	}

	public void setHomeParish(final String homeParish) {
		this.homeParish = homeParish;
	}

	public Seaman withHomeParish(final String homeParish) {
		this.homeParish = homeParish;
		return this;
	}

	public String getHomePlace() {
		return homePlace;
	}

	public void setHomePlace(final String homePlace) {
		this.homePlace = homePlace;
	}

	public Seaman withHomePlace(final String homePlace) {
		this.homePlace = homePlace;
		return this;
	}

	public String getCivilStatus() {
		return civilStatus;
	}

	public void setCivilStatus(final String civilStatus) {
		this.civilStatus = civilStatus;
	}

	public Seaman withCivilStatus(final String civilStatus) {
		this.civilStatus = civilStatus;
		return this;
	}

	public String getFather() {
		return father;
	}

	public void setFather(final String father) {
		this.father = father;
	}

	public Seaman withFather(final String father) {
		this.father = father;
		return this;
	}

	public String getMother() {
		return mother;
	}

	public void setMother(final String mother) {
		this.mother = mother;
	}

	public Seaman withMother(final String mother) {
		this.mother = mother;
		return this;
	}

	public String getSeamensHouse() {
		return seamensHouse;
	}

	public void setSeamensHouse(final String seamensHouse) {
		this.seamensHouse = seamensHouse;
	}

	public Seaman withSeamensHouse(final String seamensHouse) {
		this.seamensHouse = seamensHouse;
		return this;
	}

	public String getEnrollmentNumber() {
		return enrollmentNumber;
	}

	public void setEnrollmentNumber(final String enrollmentNumber) {
		this.enrollmentNumber = enrollmentNumber;
	}

	public Seaman withEnrollmentNumber(final String enrollmentNumber) {
		this.enrollmentNumber = enrollmentNumber;
		return this;
	}

	public String getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(final String enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public Seaman withEnrollmentDate(final String enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
		return this;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(final String rank) {
		this.rank = rank;
	}

	public Seaman withRank(final String rank) {
		this.rank = rank;
		return this;
	}

	public String getSignOnPlace() {
		return signOnPlace;
	}

	public void setSignOnPlace(final String signOnPlace) {
		this.signOnPlace = signOnPlace;
	}

	public Seaman withSignOnPlace(final String signOnPlace) {
		this.signOnPlace = signOnPlace;
		return this;
	}

	public String getSignOnDate() {
		return signOnDate;
	}

	public void setSignOnDate(final String signOnDate) {
		this.signOnDate = signOnDate;
	}

	public Seaman withSignOnDate(final String signOnDate) {
		this.signOnDate = signOnDate;
		return this;
	}

	public String getSignOffPlace() {
		return signOffPlace;
	}

	public void setSignOffPlace(final String signOffPlace) {
		this.signOffPlace = signOffPlace;
	}

	public Seaman withSignOffPlace(final String signOffPlace) {
		this.signOffPlace = signOffPlace;
		return this;
	}

	public String getSignOffDate() {
		return signOffDate;
	}

	public void setSignOffDate(final String signOffDate) {
		this.signOffDate = signOffDate;
	}

	public Seaman withSignOffDate(final String signOffDate) {
		this.signOffDate = signOffDate;
		return this;
	}

	public String getShip() {
		return ship;
	}

	public void setShip(final String ship) {
		this.ship = ship;
	}

	public Seaman withShip(final String ship) {
		this.ship = ship;
		return this;
	}

	public String getHomePort() {
		return homePort;
	}

	public void setHomePort(final String homePort) {
		this.homePort = homePort;
	}

	public Seaman withHomePort(final String homePort) {
		this.homePort = homePort;
		return this;
	}

	public String getShipType() {
		return shipType;
	}

	public void setShipType(final String shipType) {
		this.shipType = shipType;
	}

	public Seaman withShipType(final String shipType) {
		this.shipType = shipType;
		return this;
	}

	public String getShipOwner() {
		return shipOwner;
	}

	public void setShipOwner(final String shipOwner) {
		this.shipOwner = shipOwner;
	}

	public Seaman withShipOwner(final String shipOwner) {
		this.shipOwner = shipOwner;
		return this;
	}

	public String getCaptain() {
		return captain;
	}

	public void setCaptain(final String captain) {
		this.captain = captain;
	}

	public Seaman withCaptain(final String captain) {
		this.captain = captain;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
	}

	public Seaman withDestination(final String destination) {
		this.destination = destination;
		return this;
	}

	public String getOther() {
		return other;
	}

	public void setOther(final String other) {
		this.other = other;
	}

	public Seaman withOther(final String other) {
		this.other = other;
		return this;
	}

	public String getNote() {
		return note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public Seaman withNote(final String note) {
		this.note = note;
		return this;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(final String archive) {
		this.archive = archive;
	}

	public Seaman withArchive(final String archive) {
		this.archive = archive;
		return this;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(final String volume) {
		this.volume = volume;
	}

	public Seaman withVolume(final String volume) {
		this.volume = volume;
		return this;
	}

	public String getArchiveNumber() {
		return archiveNumber;
	}

	public void setArchiveNumber(final String archiveNumber) {
		this.archiveNumber = archiveNumber;
	}

	public Seaman withArchiveNumber(final String archiveNumber) {
		this.archiveNumber = archiveNumber;
		return this;
	}

	public String getPage() {
		return page;
	}

	public void setPage(final String page) {
		this.page = page;
	}

	public Seaman withPage(final String page) {
		this.page = page;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Seaman that = (Seaman) o;
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
		return "Seaman{" +
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
