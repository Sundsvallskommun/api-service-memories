package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
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

	private static Stream<Arguments> toFilmArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(
				FilmEntity.create()
					.withFilmId(1L)
					.withFilnamn("test.mp4")
					.withFilmObjFil("/path/test.mp4")
					.withObjtyp("VIDEO")
					.withDatum("2020-01-01")
					.withDoktitel("Test film")
					.withFilmTId(2L)
					.withFilmOplats("Sundsvall")
					.withFilmOId(3L)
					.withFilmUEId(4L)
					.withFilmUId(5L)
					.withKommentFilm("A comment")
					.withFilmMimeType("video/mp4")
					.withAsv("ASV001")
					.withNodeId(6L)
					.withOptions(0L)
					.withDeletedDate(LocalDate.of(2026, 1, 15)),
				Film.create()
					.withFilmId(1L)
					.withFilnamn("test.mp4")
					.withFilmObjFil("/path/test.mp4")
					.withObjtyp("VIDEO")
					.withDatum("2020-01-01")
					.withDoktitel("Test film")
					.withFilmTId(2L)
					.withFilmOplats("Sundsvall")
					.withFilmOId(3L)
					.withFilmUEId(4L)
					.withFilmUId(5L)
					.withKommentFilm("A comment")
					.withFilmMimeType("video/mp4")
					.withAsv("ASV001")
					.withNodeId(6L)
					.withOptions(0L)
					.withDeletedDate(LocalDate.of(2026, 1, 15))));
	}

	@Test
	void toFilmList() {
		final var entities = List.of(
			FilmEntity.create().withFilmId(1L).withDoktitel("Film A"),
			FilmEntity.create().withFilmId(2L).withDoktitel("Film B"));

		final var result = FilmMapper.toFilmList(entities);

		assertThat(result)
			.extracting(Film::getFilmId, Film::getDoktitel)
			.containsExactly(tuple(1L, "Film A"), tuple(2L, "Film B"));
	}

	@Test
	void toFilmListWithNull() {
		assertThat(FilmMapper.toFilmList(null)).isEmpty();
	}
}
