package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "The originator (upphovsman) of an object — a person, a legal entity, or both. Absent when the object has none.")
public class Creator {

	@Schema(description = "ID of the originator when it is a person", examples = "1")
	private Integer personId;

	@Schema(description = "Name of the originator when it is a person", examples = "Anton Nordin")
	private String person;

	@Schema(description = "ID of the originator when it is a legal entity", examples = "10")
	private Integer legalEntityId;

	@Schema(description = "Name of the originator when it is a legal entity", examples = "Nödhjälpskommittén 1888-1889")
	private String legalEntity;

	public static Creator create() {
		return new Creator();
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(final Integer personId) {
		this.personId = personId;
	}

	public Creator withPersonId(final Integer personId) {
		this.personId = personId;
		return this;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(final String person) {
		this.person = person;
	}

	public Creator withPerson(final String person) {
		this.person = person;
		return this;
	}

	public Integer getLegalEntityId() {
		return legalEntityId;
	}

	public void setLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
	}

	public Creator withLegalEntityId(final Integer legalEntityId) {
		this.legalEntityId = legalEntityId;
		return this;
	}

	public String getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(final String legalEntity) {
		this.legalEntity = legalEntity;
	}

	public Creator withLegalEntity(final String legalEntity) {
		this.legalEntity = legalEntity;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Creator creator = (Creator) o;
		return Objects.equals(personId, creator.personId) && Objects.equals(person, creator.person)
			&& Objects.equals(legalEntityId, creator.legalEntityId) && Objects.equals(legalEntity, creator.legalEntity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personId, person, legalEntityId, legalEntity);
	}

	@Override
	public String toString() {
		return "Creator{" +
			"personId=" + personId +
			", person='" + person + '\'' +
			", legalEntityId=" + legalEntityId +
			", legalEntity='" + legalEntity + '\'' +
			'}';
	}
}
