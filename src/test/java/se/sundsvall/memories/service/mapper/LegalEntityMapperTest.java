package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class LegalEntityMapperTest {

	private static LegalEntityEntity sampleEntity() {
		return LegalEntityEntity.create()
			.withLegalEntityId(123)
			.withName("Nödhjälpskommittén 1888-1889")
			.withAlternativeNames("Nödhjälpskommittén")
			.withTopography(TopographyEntity.create().withId(42).withName("Sundsvalls kommun"))
			.withLocationText("Sundsvall")
			.withStartDate("1888")
			.withEndDate("1889")
			.withPrincipal("Sundsvalls stad")
			.withComment("Bildad efter branden 1888")
			.withHistoryFilename("jurpers_123_historia.xml")
			.withCategory(CategoryEntity.create().withCategoryId(5).withName("Kommitté"))
			.withOptions(6);
	}

	@Test
	void toLegalEntityResolvesLocationAndCategory() {
		final var result = LegalEntityMapper.toLegalEntity(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getLegalEntityId()).isEqualTo(123);
		assertThat(result.getName()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getAlternativeNames()).isEqualTo("Nödhjälpskommittén");
		assertThat(result.getTopographyId()).isEqualTo(42);
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getStartDate()).isEqualTo("1888");
		assertThat(result.getEndDate()).isEqualTo("1889");
		assertThat(result.getPrincipal()).isEqualTo("Sundsvalls stad");
		assertThat(result.getHistoryFilename()).isEqualTo("jurpers_123_historia.xml");
		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getCategory()).isEqualTo("Kommitté");
	}

	/**
	 * Both associations are {@code null} when the legal entity has no place or category, and when the FK points at a row
	 * that does not exist, so the ids and the resolved names must all come out null.
	 */
	@Test
	void toLegalEntityWithoutAssociations() {
		final var result = LegalEntityMapper.toLegalEntity(LegalEntityEntity.create().withLegalEntityId(200).withName("Berg AB"));

		assertThat(result.getTopographyId()).isNull();
		assertThat(result.getLocation()).isNull();
		assertThat(result.getCategoryId()).isNull();
		assertThat(result.getCategory()).isNull();
	}

	@Test
	void toLegalEntityWhenNull() {
		assertThat(LegalEntityMapper.toLegalEntity(null)).isNull();
	}

	@Test
	void toLegalEntityList() {
		final var result = LegalEntityMapper.toLegalEntityList(
			List.of(sampleEntity(), LegalEntityEntity.create().withLegalEntityId(200).withName("Berg AB")));

		assertThat(result).hasSize(2)
			.extracting("legalEntityId", "name", "location", "category")
			.containsExactly(
				tuple(123, "Nödhjälpskommittén 1888-1889", "Sundsvalls kommun", "Kommitté"),
				tuple(200, "Berg AB", null, null));
	}

	@Test
	void toLegalEntityListWhenNull() {
		assertThat(LegalEntityMapper.toLegalEntityList(null)).isEqualTo(emptyList());
	}
}
