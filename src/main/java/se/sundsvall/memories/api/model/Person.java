package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Person model")
public class Person {

	@Schema(description = "Person ID", examples = "123")
	private Integer personId;

	@Schema(description = "Person number (indiko)", examples = "42")
	private String personNumber;

	@Schema(description = "Last name", examples = "Nordin")
	private String lastName;

	@Schema(description = "First name", examples = "Anton")
	private String firstName;

	@Schema(description = "Gender", examples = "man")
	private String gender;

	@Schema(description = "Birth date (stored as free text, not necessarily a valid date)", examples = "1852-03-14")
	private String birthDate;

	@Schema(description = "Birth parish", examples = "Sundsvall")
	private String birthParish;

	@Schema(description = "Death date (stored as free text, not necessarily a valid date)", examples = "1921-11-02")
	private String deathDate;

	@Schema(description = "Occupation", examples = "Handlare")
	private String occupation;

	@Schema(description = "Related person name (e.g. spouse/father)", examples = "Erik Nordin")
	private String relatedPersonName;

	@Schema(description = "Related person's occupation", examples = "Bonde")
	private String relatedPersonOccupation;

	@Schema(description = "Parish moved in from", examples = "Selånger")
	private String movedInParish;

	@Schema(description = "Parish moved out to", examples = "Njurunda")
	private String movedOutParish;

	@Schema(description = "Sources", examples = "Kyrkoarkiv")
	private String sources;

	@Schema(description = "Comment", examples = "Flyttade till Sundsvall 1875")
	private String comment;

	@Schema(description = "Biography file reference (served via a dedicated file endpoint when available)", examples = "person_123_biografi.xml")
	private String biographyFilename;

	@Schema(description = "Options", examples = "6")
	private Integer options;

	@Schema(description = "Deleted date", examples = "2026-01-15")
	private LocalDate deletedDate;

	public static Person create() {
		return new Person();
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(final Integer personId) {
		this.personId = personId;
	}

	public Person withPersonId(final Integer personId) {
		this.personId = personId;
		return this;
	}

	public String getPersonNumber() {
		return personNumber;
	}

	public void setPersonNumber(final String personNumber) {
		this.personNumber = personNumber;
	}

	public Person withPersonNumber(final String personNumber) {
		this.personNumber = personNumber;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public Person withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public Person withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public Person withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final String birthDate) {
		this.birthDate = birthDate;
	}

	public Person withBirthDate(final String birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public String getBirthParish() {
		return birthParish;
	}

	public void setBirthParish(final String birthParish) {
		this.birthParish = birthParish;
	}

	public Person withBirthParish(final String birthParish) {
		this.birthParish = birthParish;
		return this;
	}

	public String getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(final String deathDate) {
		this.deathDate = deathDate;
	}

	public Person withDeathDate(final String deathDate) {
		this.deathDate = deathDate;
		return this;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(final String occupation) {
		this.occupation = occupation;
	}

	public Person withOccupation(final String occupation) {
		this.occupation = occupation;
		return this;
	}

	public String getRelatedPersonName() {
		return relatedPersonName;
	}

	public void setRelatedPersonName(final String relatedPersonName) {
		this.relatedPersonName = relatedPersonName;
	}

	public Person withRelatedPersonName(final String relatedPersonName) {
		this.relatedPersonName = relatedPersonName;
		return this;
	}

	public String getRelatedPersonOccupation() {
		return relatedPersonOccupation;
	}

	public void setRelatedPersonOccupation(final String relatedPersonOccupation) {
		this.relatedPersonOccupation = relatedPersonOccupation;
	}

	public Person withRelatedPersonOccupation(final String relatedPersonOccupation) {
		this.relatedPersonOccupation = relatedPersonOccupation;
		return this;
	}

	public String getMovedInParish() {
		return movedInParish;
	}

	public void setMovedInParish(final String movedInParish) {
		this.movedInParish = movedInParish;
	}

	public Person withMovedInParish(final String movedInParish) {
		this.movedInParish = movedInParish;
		return this;
	}

	public String getMovedOutParish() {
		return movedOutParish;
	}

	public void setMovedOutParish(final String movedOutParish) {
		this.movedOutParish = movedOutParish;
	}

	public Person withMovedOutParish(final String movedOutParish) {
		this.movedOutParish = movedOutParish;
		return this;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(final String sources) {
		this.sources = sources;
	}

	public Person withSources(final String sources) {
		this.sources = sources;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Person withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getBiographyFilename() {
		return biographyFilename;
	}

	public void setBiographyFilename(final String biographyFilename) {
		this.biographyFilename = biographyFilename;
	}

	public Person withBiographyFilename(final String biographyFilename) {
		this.biographyFilename = biographyFilename;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public Person withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public Person withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Person person = (Person) o;
		return Objects.equals(personId, person.personId) && Objects.equals(personNumber, person.personNumber) && Objects.equals(lastName, person.lastName)
			&& Objects.equals(firstName, person.firstName) && Objects.equals(gender, person.gender) && Objects.equals(birthDate, person.birthDate)
			&& Objects.equals(birthParish, person.birthParish) && Objects.equals(deathDate, person.deathDate) && Objects.equals(occupation, person.occupation)
			&& Objects.equals(relatedPersonName, person.relatedPersonName) && Objects.equals(relatedPersonOccupation, person.relatedPersonOccupation)
			&& Objects.equals(movedInParish, person.movedInParish) && Objects.equals(movedOutParish, person.movedOutParish) && Objects.equals(sources, person.sources)
			&& Objects.equals(comment, person.comment) && Objects.equals(biographyFilename, person.biographyFilename) && Objects.equals(options, person.options)
			&& Objects.equals(deletedDate, person.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personId, personNumber, lastName, firstName, gender, birthDate, birthParish, deathDate, occupation, relatedPersonName, relatedPersonOccupation, movedInParish,
			movedOutParish, sources, comment, biographyFilename, options, deletedDate);
	}

	@Override
	public String toString() {
		return "Person{" +
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
