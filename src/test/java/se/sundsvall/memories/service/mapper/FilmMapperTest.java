package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class FilmMapperTest {

	private static final Function<Integer, String> NULL_LOOKUP = id -> null;

	private static Stream<Arguments> toFilmArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(
				FilmEntity.create()
					.withFilmId(1)
					.withFilename("test.mp4")
					.withObjectFilePath("/path/test.mp4")
					.withObjectType("VIDEO")
					.withDate("2020-01-01")
					.withDocumentTitle("Test film")
					.withTopographyId(2)
					.withLocationText("Sundsvall")
					.withOrganizationId(3)
					.withSubEntityId(4)
					.withUnitId(5)
					.withComment("A comment")
					.withFilmMimeType("video/mp4")
					.withNodeId(6)
					.withOptions(0)
					.withDeletedDate(LocalDate.of(2026, 1, 15)),
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
					.withSubEntityId(4)
					.withUnitId(5)
					.withComment("A comment")
					.withFilmMimeType("video/mp4")
					.withNodeId(6)
					.withOptions(0)
					.withDeletedDate(LocalDate.of(2026, 1, 15))));
	}

	@ParameterizedTest
	@MethodSource("toFilmArguments")
	void toFilm(final FilmEntity input, final Film expected) {
		final var result = FilmMapper.toFilm(input, "Sundsvall kommun");

		if (expected == null) {
			assertThat(result).isNull();
		} else {
			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}
	}

	@Test
	void toFilmList() {
		final var entities = List.of(
			FilmEntity.create().withFilmId(1).withTopographyId(10).withDocumentTitle("Film A"),
			FilmEntity.create().withFilmId(2).withTopographyId(20).withDocumentTitle("Film B"));
		final Function<Integer, String> lookup = id -> id == 10 ? "Sundsvall" : "Timrå";

		final var result = FilmMapper.toFilmList(entities, lookup);

		assertThat(result)
			.extracting(Film::getFilmId, Film::getDocumentTitle, Film::getLocation)
			.containsExactly(tuple(1, "Film A", "Sundsvall"), tuple(2, "Film B", "Timrå"));
	}

	@Test
	void toFilmListWithNull() {
		assertThat(FilmMapper.toFilmList(null, NULL_LOOKUP)).isEmpty();
	}

}
