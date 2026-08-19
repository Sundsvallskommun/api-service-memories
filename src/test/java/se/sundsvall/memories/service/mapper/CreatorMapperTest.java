package se.sundsvall.memories.service.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CreatorMapperTest {

	@Test
	void toCreatorCarriesBothRoles() {
		final var result = CreatorMapper.toCreator(
			PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin"),
			LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén 1888-1889"));

		assertThat(result).isNotNull();
		assertThat(result.getPersonId()).isEqualTo(1);
		assertThat(result.getPerson()).isEqualTo("Anton Nordin");
		assertThat(result.getLegalEntityId()).isEqualTo(10);
		assertThat(result.getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
	}

	@Test
	void toCreatorKeepsWhicheverRoleIsSet() {
		final var person = CreatorMapper.toCreator(PersonEntity.create().withPersonId(1).withLastName("Nordin"), null);

		assertThat(person).isNotNull();
		assertThat(person.getPerson()).isEqualTo("Nordin");
		assertThat(person.getLegalEntity()).isNull();
	}

	/**
	 * An object without an originator carries no creator at all, rather than one with four empty fields.
	 */
	@Test
	void toCreatorIsAbsentWhenThereIsNoOriginator() {
		assertThat(CreatorMapper.toCreator(null, null)).isNull();
		assertThat(CreatorMapper.toCreator(
			PersonEntity.create().withPersonId(0).withLastName("Ingen"),
			LegalEntityEntity.create().withLegalEntityId(1).withName("Ingen"))).isNull();
	}

	@Test
	void personIsMappedByIdsAndName() {
		final var person = PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin");

		assertThat(CreatorMapper.personId(person)).isEqualTo(1);
		assertThat(CreatorMapper.personName(person)).isEqualTo("Anton Nordin");
	}

	@Test
	void legalEntityIsMappedByIdAndName() {
		final var legalEntity = LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén 1888-1889");

		assertThat(CreatorMapper.legalEntityId(legalEntity)).isEqualTo(10);
		assertThat(CreatorMapper.legalEntityName(legalEntity)).isEqualTo("Nödhjälpskommittén 1888-1889");
	}

	/**
	 * U_E_ID defaults to 0 and U_J_ID to 1, the sentinel rows that mean "no originator". Both rows exist and load
	 * perfectly well, so without this the API would report an originator named "Ingen" on nearly every object.
	 */
	@Test
	void placeholderRowsAreReadAsNoCreator() {
		final var placeholderPerson = PersonEntity.create().withPersonId(0).withLastName("Ingen");
		final var placeholderLegalEntity = LegalEntityEntity.create().withLegalEntityId(1).withName("Ingen");

		assertThat(CreatorMapper.personId(placeholderPerson)).isNull();
		assertThat(CreatorMapper.personName(placeholderPerson)).isNull();
		assertThat(CreatorMapper.legalEntityId(placeholderLegalEntity)).isNull();
		assertThat(CreatorMapper.legalEntityName(placeholderLegalEntity)).isNull();
	}

	@Test
	void missingAssociationIsReadAsNoCreator() {
		assertThat(CreatorMapper.personId(null)).isNull();
		assertThat(CreatorMapper.personName(null)).isNull();
		assertThat(CreatorMapper.legalEntityId(null)).isNull();
		assertThat(CreatorMapper.legalEntityName(null)).isNull();
	}

	/**
	 * Either half of the name can be missing in the archive, and a person with only a surname is named by it rather
	 * than by a string with a stray space in it.
	 */
	@Test
	void personNameFallsBackToWhicheverHalfExists() {
		assertThat(CreatorMapper.personName(PersonEntity.create().withPersonId(2).withLastName("Nordin"))).isEqualTo("Nordin");
		assertThat(CreatorMapper.personName(PersonEntity.create().withPersonId(3).withFirstName("Anton"))).isEqualTo("Anton");
		assertThat(CreatorMapper.personName(PersonEntity.create().withPersonId(4).withFirstName("  ").withLastName(""))).isNull();
		assertThat(CreatorMapper.personName(PersonEntity.create().withPersonId(5))).isNull();
	}
}
