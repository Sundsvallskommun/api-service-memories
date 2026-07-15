package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "PERSON")
public class PersonEntity {

	@Id
	@Column(name = "P_ID")
	private Integer personId;

	@Column(name = "PNR", length = 10)
	private String personNumber;

	@Column(name = "ENAMN", length = 510)
	private String lastName;

	@Column(name = "FNAMN", length = 510)
	private String firstName;

	@Column(name = "KON", length = 510)
	private String gender;

	@Column(name = "FODDAT", length = 10)
	private String birthDate;

	@Column(name = "FODFRS", length = 510)
	private String birthParish;

	@Column(name = "DODDAT", length = 10)
	private String deathDate;

	@Column(name = "YRKEE", length = 510)
	private String occupation;

	@Column(name = "YAGARE", length = 510)
	private String relatedPersonName;

	@Column(name = "YRKER", length = 100)
	private String relatedPersonOccupation;

	@Column(name = "FRNFRS", length = 510)
	private String movedInParish;

	@Column(name = "TILFRS", length = 510)
	private String movedOutParish;

	@Column(name = "KALLA", length = 100)
	private String sources;

	@Column(name = "KOMMENT_PERS", length = 4000)
	private String comment;

	@Column(name = "BIOGRAFI", length = 256)
	private String biographyFilename;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static PersonEntity create() {
		return new PersonEntity();
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(final Integer personId) {
		this.personId = personId;
	}

	public PersonEntity withPersonId(final Integer personId) {
		this.personId = personId;
		return this;
	}

	public String getPersonNumber() {
		return personNumber;
	}

	public void setPersonNumber(final String personNumber) {
		this.personNumber = personNumber;
	}

	public PersonEntity withPersonNumber(final String personNumber) {
		this.personNumber = personNumber;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public PersonEntity withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public PersonEntity withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public PersonEntity withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final String birthDate) {
		this.birthDate = birthDate;
	}

	public PersonEntity withBirthDate(final String birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public String getBirthParish() {
		return birthParish;
	}

	public void setBirthParish(final String birthParish) {
		this.birthParish = birthParish;
	}

	public PersonEntity withBirthParish(final String birthParish) {
		this.birthParish = birthParish;
		return this;
	}

	public String getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(final String deathDate) {
		this.deathDate = deathDate;
	}

	public PersonEntity withDeathDate(final String deathDate) {
		this.deathDate = deathDate;
		return this;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(final String occupation) {
		this.occupation = occupation;
	}

	public PersonEntity withOccupation(final String occupation) {
		this.occupation = occupation;
		return this;
	}

	public String getRelatedPersonName() {
		return relatedPersonName;
	}

	public void setRelatedPersonName(final String relatedPersonName) {
		this.relatedPersonName = relatedPersonName;
	}

	public PersonEntity withRelatedPersonName(final String relatedPersonName) {
		this.relatedPersonName = relatedPersonName;
		return this;
	}

	public String getRelatedPersonOccupation() {
		return relatedPersonOccupation;
	}

	public void setRelatedPersonOccupation(final String relatedPersonOccupation) {
		this.relatedPersonOccupation = relatedPersonOccupation;
	}

	public PersonEntity withRelatedPersonOccupation(final String relatedPersonOccupation) {
		this.relatedPersonOccupation = relatedPersonOccupation;
		return this;
	}

	public String getMovedInParish() {
		return movedInParish;
	}

	public void setMovedInParish(final String movedInParish) {
		this.movedInParish = movedInParish;
	}

	public PersonEntity withMovedInParish(final String movedInParish) {
		this.movedInParish = movedInParish;
		return this;
	}

	public String getMovedOutParish() {
		return movedOutParish;
	}

	public void setMovedOutParish(final String movedOutParish) {
		this.movedOutParish = movedOutParish;
	}

	public PersonEntity withMovedOutParish(final String movedOutParish) {
		this.movedOutParish = movedOutParish;
		return this;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(final String sources) {
		this.sources = sources;
	}

	public PersonEntity withSources(final String sources) {
		this.sources = sources;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public PersonEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getBiographyFilename() {
		return biographyFilename;
	}

	public void setBiographyFilename(final String biographyFilename) {
		this.biographyFilename = biographyFilename;
	}

	public PersonEntity withBiographyFilename(final String biographyFilename) {
		this.biographyFilename = biographyFilename;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public PersonEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public PersonEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PersonEntity that = (PersonEntity) o;
		return Objects.equals(personId, that.personId) && Objects.equals(personNumber, that.personNumber) && Objects.equals(lastName, that.lastName)
			&& Objects.equals(firstName, that.firstName) && Objects.equals(gender, that.gender) && Objects.equals(birthDate, that.birthDate)
			&& Objects.equals(birthParish, that.birthParish) && Objects.equals(deathDate, that.deathDate) && Objects.equals(occupation, that.occupation)
			&& Objects.equals(relatedPersonName, that.relatedPersonName) && Objects.equals(relatedPersonOccupation, that.relatedPersonOccupation)
			&& Objects.equals(movedInParish, that.movedInParish) && Objects.equals(movedOutParish, that.movedOutParish) && Objects.equals(sources, that.sources)
			&& Objects.equals(comment, that.comment) && Objects.equals(biographyFilename, that.biographyFilename) && Objects.equals(options, that.options)
			&& Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personId, personNumber, lastName, firstName, gender, birthDate, birthParish, deathDate, occupation, relatedPersonName, relatedPersonOccupation, movedInParish,
			movedOutParish, sources, comment, biographyFilename, options, deletedDate);
	}

	@Override
	public String toString() {
		return "PersonEntity{" +
			"personId=" + personId +
			", personNumber='" + personNumber + '\'' +
			", lastName='" + lastName + '\'' +
			", firstName='" + firstName + '\'' +
			", gender='" + gender + '\'' +
			", birthDate='" + birthDate + '\'' +
			", birthParish='" + birthParish + '\'' +
			", deathDate='" + deathDate + '\'' +
			", occupation='" + occupation + '\'' +
			", relatedPersonName='" + relatedPersonName + '\'' +
			", relatedPersonOccupation='" + relatedPersonOccupation + '\'' +
			", movedInParish='" + movedInParish + '\'' +
			", movedOutParish='" + movedOutParish + '\'' +
			", sources='" + sources + '\'' +
			", comment='" + comment + '\'' +
			", biographyFilename='" + biographyFilename + '\'' +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
