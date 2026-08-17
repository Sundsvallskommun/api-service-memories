package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PersonMapperTest {

	private static PersonEntity sampleEntity() {
		return PersonEntity.create()
			.withPersonId(123)
			.withPersonNumber("42")
			.withLastName("Nordin")
			.withFirstName("Anton")
			.withGender("man")
			.withBirthDate("1852-03-14")
			.withBirthParish("Sundsvall")
			.withDeathDate("1921-11-02")
			.withOccupation("Handlare")
			.withRelatedPersonName("Erik Nordin")
			.withRelatedPersonOccupation("Bonde")
			.withMovedInParish("Selånger")
			.withMovedOutParish("Njurunda")
			.withSources("Kyrkoarkiv")
			.withComment("Flyttade till Sundsvall 1875")
			.withBiographyFilename("person_123_biografi.xml")
			.withOptions(6)
			.withDeletedDate(LocalDate.of(2026, Month.JANUARY, 15));
	}

	@Test
	void toPerson() {
		final var result = PersonMapper.toPerson(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getPersonId()).isEqualTo(123);
		assertThat(result.getPersonNumber()).isEqualTo("42");
		assertThat(result.getLastName()).isEqualTo("Nordin");
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getGender()).isEqualTo("man");
		assertThat(result.getBirthDate()).isEqualTo("1852-03-14");
		assertThat(result.getBirthParish()).isEqualTo("Sundsvall");
		assertThat(result.getDeathDate()).isEqualTo("1921-11-02");
		assertThat(result.getOccupation()).isEqualTo("Handlare");
		assertThat(result.getRelatedPersonName()).isEqualTo("Erik Nordin");
		assertThat(result.getRelatedPersonOccupation()).isEqualTo("Bonde");
		assertThat(result.getMovedInParish()).isEqualTo("Selånger");
		assertThat(result.getMovedOutParish()).isEqualTo("Njurunda");
		assertThat(result.getSources()).isEqualTo("Kyrkoarkiv");
		assertThat(result.getComment()).isEqualTo("Flyttade till Sundsvall 1875");
		assertThat(result.getBiographyFilename()).isEqualTo("person_123_biografi.xml");
		assertThat(result.getOptions()).isEqualTo(6);
		assertThat(result.getDeletedDate()).isEqualTo(LocalDate.of(2026, Month.JANUARY, 15));
	}

	@Test
	void toPersonWhenNull() {
		assertThat(PersonMapper.toPerson(null)).isNull();
	}

	@Test
	void toPersonList() {
		final var result = PersonMapper.toPersonList(List.of(sampleEntity(), PersonEntity.create().withPersonId(200).withLastName("Berg")));

		assertThat(result).hasSize(2)
			.extracting("personId", "lastName")
			.containsExactly(tuple(123, "Nordin"), tuple(200, "Berg"));
	}

	@Test
	void toPersonListWhenNull() {
		assertThat(PersonMapper.toPersonList(null)).isEqualTo(emptyList());
	}
}
