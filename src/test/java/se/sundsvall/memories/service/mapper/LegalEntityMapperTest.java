package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class LegalEntityMapperTest {

	private static final ReferenceResolver NULL_LOOKUP = id -> null;

	private static LegalEntityEntity sampleEntity() {
		return LegalEntityEntity.create()
			.withLegalEntityId(123)
			.withName("Nödhjälpskommittén 1888-1889")
			.withAlternativeNames("Nödhjälpskommittén")
			.withTopographyId(42)
			.withLocationText("Sundsvall")
			.withStartDate("1888")
			.withEndDate("1889")
			.withPrincipal("Sundsvalls stad")
			.withComment("Bildad efter branden 1888")
			.withHistoryFilename("jurpers_123_historia.xml")
			.withCategoryId(5)
			.withOptions(6);
	}

	@Test
	void toLegalEntityResolvesLocationAndCategory() {
		final var result = LegalEntityMapper.toLegalEntity(sampleEntity(), "Sundsvalls kommun", "Kommitté");

		assertThat(result).isNotNull();
		assertThat(result.getLegalEntityId()).isEqualTo(123);
		assertThat(result.getName()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getAlternativeNames()).isEqualTo("Nödhjälpskommittén");
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getStartDate()).isEqualTo("1888");
		assertThat(result.getEndDate()).isEqualTo("1889");
		assertThat(result.getPrincipal()).isEqualTo("Sundsvalls stad");
		assertThat(result.getHistoryFilename()).isEqualTo("jurpers_123_historia.xml");
		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getCategory()).isEqualTo("Kommitté");
	}

	@Test
	void toLegalEntityWhenNull() {
		assertThat(LegalEntityMapper.toLegalEntity(null, "x", "y")).isNull();
	}

	@Test
	void toLegalEntityListResolvesViaLookups() {
		final var result = LegalEntityMapper.toLegalEntityList(
			List.of(sampleEntity(), LegalEntityEntity.create().withLegalEntityId(200).withName("Berg AB")),
			id -> id == null ? null : "Loc-" + id,
			id -> id == null ? null : "Cat-" + id);

		assertThat(result).hasSize(2)
			.extracting("legalEntityId", "name", "location", "category")
			.containsExactly(
				tuple(123, "Nödhjälpskommittén 1888-1889", "Loc-42", "Cat-5"),
				tuple(200, "Berg AB", null, null));
	}

	@Test
	void toLegalEntityListWhenNull() {
		assertThat(LegalEntityMapper.toLegalEntityList(null, NULL_LOOKUP, NULL_LOOKUP)).isEqualTo(emptyList());
	}
}
