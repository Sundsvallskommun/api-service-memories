package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.Institution;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class InstitutionMapperTest {

	@Test
	void toInstitution() {
		final var result = InstitutionMapper.toInstitution(InstitutionEntity.create()
			.withId(3)
			.withName("Sundsvalls museum")
			.withCode("SVM")
			.withDescription("Kommunalt museum")
			.withUrl("https://sundsvallsmuseum.se")
			.withEmail("museet@sundsvall.se"));

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getInstitutionId()).isEqualTo(3);
		assertThat(result.getName()).isEqualTo("Sundsvalls museum");
		assertThat(result.getCode()).isEqualTo("SVM");
		assertThat(result.getDescription()).isEqualTo("Kommunalt museum");
		assertThat(result.getUrl()).isEqualTo("https://sundsvallsmuseum.se");
		assertThat(result.getEmail()).isEqualTo("museet@sundsvall.se");
	}

	@Test
	void toInstitutionWhenNull() {
		assertThat(InstitutionMapper.toInstitution(null)).isNull();
	}

	@Test
	void toInstitutionList() {
		final var result = InstitutionMapper.toInstitutionList(List.of(
			InstitutionEntity.create().withId(3).withName("Sundsvalls museum"),
			InstitutionEntity.create().withId(2).withName("Föreningsarkivet Västernorrland")));

		assertThat(result).extracting(Institution::getInstitutionId, Institution::getName)
			.containsExactly(tuple(3, "Sundsvalls museum"), tuple(2, "Föreningsarkivet Västernorrland"));
	}

	@Test
	void toInstitutionListWhenNull() {
		assertThat(InstitutionMapper.toInstitutionList(null)).isEqualTo(emptyList());
	}
}
