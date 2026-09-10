package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.Topography;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class TopographyMapperTest {

	@Test
	void toTopography() {
		final var result = TopographyMapper.toTopography(TopographyEntity.create()
			.withId(1)
			.withName("Anundsjö")
			.withCode("228471")
			.withPlace("Bredbyn")
			.withMunicipality("Örnsköldsvik"));

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTopographyId()).isEqualTo(1);
		assertThat(result.getDisplayName()).isEqualTo("Bredbyn, Anundsjö");
		assertThat(result.getName()).isEqualTo("Anundsjö");
		assertThat(result.getCode()).isEqualTo("228471");
		assertThat(result.getPlace()).isEqualTo("Bredbyn");
		assertThat(result.getMunicipality()).isEqualTo("Örnsköldsvik");
	}

	/** The display name falls back the way an object's location does, so the two agree on what a place is called. */
	@Test
	void toTopographyFallsBackToThePlaceForTheDisplayName() {
		final var result = TopographyMapper.toTopography(TopographyEntity.create().withId(2).withName(" ").withPlace("Timrå kommun"));

		assertThat(result.getDisplayName()).isEqualTo("Timrå kommun");
		assertThat(result.getName()).isEqualTo(" ");
	}

	@Test
	void toTopographyWhenNull() {
		assertThat(TopographyMapper.toTopography(null)).isNull();
	}

	@Test
	void toTopographyList() {
		final var result = TopographyMapper.toTopographyList(List.of(
			TopographyEntity.create().withId(2).withName("Timrå"),
			TopographyEntity.create().withId(1).withName("Sundsvall")));

		assertThat(result).extracting(Topography::getTopographyId, Topography::getDisplayName)
			.containsExactly(tuple(2, "Timrå"), tuple(1, "Sundsvall"));
	}

	@Test
	void toTopographyListWhenNull() {
		assertThat(TopographyMapper.toTopographyList(null)).isEqualTo(emptyList());
	}
}
