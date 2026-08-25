package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static java.time.Month.JANUARY;
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

	/**
	 * U_E_ID defaults to 0 and U_J_ID to 1, the sentinel rows that mean "no originator". Both rows exist and load
	 * perfectly well, so without this the API would report an originator named "Ingen" on nearly every object.
	 */
	/**
	 * A placeholder on one side must not hide a real originator on the other.
	 */
	@Test
	void placeholderRowIsIgnoredBesideARealOriginator() {
		final var result = CreatorMapper.toCreator(
			PersonEntity.create().withPersonId(0).withLastName("Ingen"),
			LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén 1888-1889"));

		assertThat(result).isNotNull();
		assertThat(result.getPersonId()).isNull();
		assertThat(result.getPerson()).isNull();
		assertThat(result.getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
	}

	/**
	 * Either half of the name can be missing in the archive, and a person with only a surname is named by it rather
	 * than by a string with a stray space in it.
	 */
	@Test
	void personNameFallsBackToWhicheverHalfExists() {
		assertThat(name(PersonEntity.create().withPersonId(2).withLastName("Nordin"))).isEqualTo("Nordin");
		assertThat(name(PersonEntity.create().withPersonId(3).withFirstName("Anton"))).isEqualTo("Anton");
		assertThat(name(PersonEntity.create().withPersonId(4).withFirstName("  ").withLastName(""))).isNull();
		assertThat(name(PersonEntity.create().withPersonId(5))).isNull();
	}

	private static String name(final PersonEntity person) {
		return CreatorMapper.toCreator(person, null).getPerson();
	}

	/**
	 * A deleted register record answers 404 on its own endpoint, so naming it here would publish a reference the API
	 * refuses to resolve — and the name of a record the archive has deleted.
	 */
	@Test
	void deletedOriginatorIsReadAsAbsent() {
		final var deletedPerson = PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin")
			.withDeletedDate(LocalDate.of(2026, JANUARY, 1));
		final var deletedLegalEntity = LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén")
			.withDeletedDate(LocalDate.of(2026, JANUARY, 1));

		assertThat(CreatorMapper.toCreator(deletedPerson, deletedLegalEntity)).isNull();

		final var oneDeleted = CreatorMapper.toCreator(deletedPerson,
			LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén"));

		assertThat(oneDeleted).isNotNull();
		assertThat(oneDeleted.getPersonId()).isNull();
		assertThat(oneDeleted.getPerson()).isNull();
		assertThat(oneDeleted.getLegalEntity()).isEqualTo("Nödhjälpskommittén");
	}
}
