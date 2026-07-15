package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.SeamanEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class SeamanMapperTest {

	private static SeamanEntity sampleEntity() {
		return SeamanEntity.create()
			.withId(123)
			.withFirstName("Anton")
			.withLastName1("Nordin")
			.withLastName2("Sjöberg")
			.withIdNumber(4711)
			.withBirthDate("1852-03-14")
			.withBirthParish("Sundsvall")
			.withSeamensHouse("Sundsvalls sjömanshus")
			.withRank("Matros")
			.withShip("Briggen Freja")
			.withCaptain("Olof Berg")
			.withArchive("Sundsvalls sjömanshus arkiv")
			.withVolume("A1:3")
			.withArchiveNumber("SE/HLA/1234")
			.withPage("42");
	}

	@Test
	void toSeaman() {
		final var result = SeamanMapper.toSeaman(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(123);
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getLastName1()).isEqualTo("Nordin");
		assertThat(result.getLastName2()).isEqualTo("Sjöberg");
		assertThat(result.getIdNumber()).isEqualTo(4711);
		assertThat(result.getBirthDate()).isEqualTo("1852-03-14");
		assertThat(result.getBirthParish()).isEqualTo("Sundsvall");
		assertThat(result.getSeamensHouse()).isEqualTo("Sundsvalls sjömanshus");
		assertThat(result.getRank()).isEqualTo("Matros");
		assertThat(result.getShip()).isEqualTo("Briggen Freja");
		assertThat(result.getCaptain()).isEqualTo("Olof Berg");
		assertThat(result.getArchive()).isEqualTo("Sundsvalls sjömanshus arkiv");
		assertThat(result.getVolume()).isEqualTo("A1:3");
		assertThat(result.getArchiveNumber()).isEqualTo("SE/HLA/1234");
		assertThat(result.getPage()).isEqualTo("42");
	}

	@Test
	void toSeamanWhenNull() {
		assertThat(SeamanMapper.toSeaman(null)).isNull();
	}

	@Test
	void toSeamanList() {
		final var result = SeamanMapper.toSeamanList(List.of(sampleEntity(), SeamanEntity.create().withId(200).withLastName1("Berg")));

		assertThat(result).hasSize(2)
			.extracting("id", "lastName1")
			.containsExactly(tuple(123, "Nordin"), tuple(200, "Berg"));
	}

	@Test
	void toSeamanListWhenNull() {
		assertThat(SeamanMapper.toSeamanList(null)).isEqualTo(emptyList());
	}
}
