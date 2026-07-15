package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class CensusRecordMapperTest {

	private static CensusRecordEntity sampleEntity() {
		return CensusRecordEntity.create()
			.withId(123)
			.withObjectNumber("SE/1234")
			.withSource("MTL")
			.withPropertyNumber1("Norrmalm 3")
			.withPropertyPart1("1/2")
			.withHouseholdNumber("3")
			.withOccupationRelation("Bonde")
			.withRelationCode("H")
			.withFirstName("Anton")
			.withLastName("Nordin")
			.withGender("man")
			.withBirthYear("1852")
			.withNote("Flyttade in 1875");
	}

	@Test
	void toCensusRecord() {
		final var result = CensusRecordMapper.toCensusRecord(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(123);
		assertThat(result.getObjectNumber()).isEqualTo("SE/1234");
		assertThat(result.getSource()).isEqualTo("MTL");
		assertThat(result.getPropertyNumber1()).isEqualTo("Norrmalm 3");
		assertThat(result.getPropertyPart1()).isEqualTo("1/2");
		assertThat(result.getHouseholdNumber()).isEqualTo("3");
		assertThat(result.getOccupationRelation()).isEqualTo("Bonde");
		assertThat(result.getRelationCode()).isEqualTo("H");
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getLastName()).isEqualTo("Nordin");
		assertThat(result.getGender()).isEqualTo("man");
		assertThat(result.getBirthYear()).isEqualTo("1852");
		assertThat(result.getNote()).isEqualTo("Flyttade in 1875");
	}

	@Test
	void toCensusRecordWhenNull() {
		assertThat(CensusRecordMapper.toCensusRecord(null)).isNull();
	}

	@Test
	void toCensusRecordList() {
		final var result = CensusRecordMapper.toCensusRecordList(List.of(sampleEntity(), CensusRecordEntity.create().withId(200).withLastName("Berg")));

		assertThat(result).hasSize(2)
			.extracting("id", "lastName")
			.containsExactly(tuple(123, "Nordin"), tuple(200, "Berg"));
	}

	@Test
	void toCensusRecordListWhenNull() {
		assertThat(CensusRecordMapper.toCensusRecordList(null)).isEqualTo(emptyList());
	}
}
