package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class FilmMapperTest {

	private static Stream<Arguments> toFilmArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(
				FilmEntity.create()
					.withId(1)
					.withFilename("test.mp4")
					.withObjectFilePath("/path/test.mp4")
					.withObjectType("VIDEO")
					.withDate("2020-01-01")
					.withDocumentTitle("Test film")
					.withTopography(TopographyEntity.create().withId(2).withName("Sundsvall kommun"))
					.withLocationText("Sundsvall")
					.withOrganizationId(3)
					.withComment("A comment")
					.withFilmMimeType("video/mp4")
					.withNodeId(6)
					.withOptions(0)
					.withDeletedDate(LocalDate.of(2026, Month.JANUARY, 15)),
				Film.create()
					.withFilmId(1)
					.withFilename("test.mp4")
					.withObjectFilePath("/path/test.mp4")
					.withObjectType("VIDEO")
					.withDate("2020-01-01")
					.withDocumentTitle("Test film")
					.withTopographyId(2)
					.withLocationText("Sundsvall")
					.withLocation("Sundsvall kommun")
					.withOrganizationId(3)
					.withComment("A comment")
					.withFilmMimeType("video/mp4")
					.withNodeId(6)
					.withOptions(0)
					.withDeletedDate(LocalDate.of(2026, Month.JANUARY, 15))));
	}

	@ParameterizedTest
	@MethodSource("toFilmArguments")
	void toFilm(final FilmEntity input, final Film expected) {
		final var result = FilmMapper.toFilm(input);

		if (expected == null) {
			assertThat(result).isNull();
		} else {
			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}
	}

	@Test
	void toFilmFallsBackThroughTheTopographyDisplayName() {
		final var entity = FilmEntity.create().withId(1)
			.withTopography(TopographyEntity.create().withId(2).withName("").withPlace("Indal"));

		assertThat(FilmMapper.toFilm(entity).getLocation()).isEqualTo("Indal");
	}

	@Test
	void toFilmWithoutTopographyHasNeitherLocationNorTopographyId() {
		// Both a film without a place and a film whose FILM_T_ID points at a missing row arrive here as a null
		// association — see FilmSpecificationTest for the dangling foreign key case. Since topographyId is read
		// through the association too, the two can never disagree.
		final var entity = FilmEntity.create().withId(1).withLocationText("Sundsvall");

		final var result = FilmMapper.toFilm(entity);

		assertThat(result.getLocation()).isNull();
		assertThat(result.getTopographyId()).isNull();
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toFilmReadsTopographyIdThroughTheAssociation() {
		final var entity = FilmEntity.create().withId(1)
			.withTopography(TopographyEntity.create().withId(42).withName("Indal"));

		final var result = FilmMapper.toFilm(entity);

		assertThat(result.getTopographyId()).isEqualTo(42);
		assertThat(result.getLocation()).isEqualTo("Indal");
	}

	@Test
	void toFilmList() {
		final var entities = List.of(
			FilmEntity.create().withId(1).withDocumentTitle("Film A")
				.withTopography(TopographyEntity.create().withId(10).withName("Sundsvall")),
			FilmEntity.create().withId(2).withDocumentTitle("Film B")
				.withTopography(TopographyEntity.create().withId(20).withName("Timrå")),
			FilmEntity.create().withId(3).withDocumentTitle("Film C"));

		final var result = FilmMapper.toFilmList(entities);

		assertThat(result)
			.extracting(Film::getFilmId, Film::getDocumentTitle, Film::getLocation)
			.containsExactly(tuple(1, "Film A", "Sundsvall"), tuple(2, "Film B", "Timrå"), tuple(3, "Film C", null));
	}

	@Test
	void toFilmListWithNull() {
		assertThat(FilmMapper.toFilmList(null)).isEmpty();
	}
}
