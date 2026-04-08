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

	private static Stream<Arguments> toFilmArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(
				FilmEntity.create()
					.withFilmId(1)
					.withFilnamn("test.mp4")
					.withFilmObjFil("/path/test.mp4")
					.withObjtyp("VIDEO")
					.withDatum("2020-01-01")
					.withDoktitel("Test film")
					.withFilmTId(2)
					.withFilmOplats("Sundsvall")
					.withFilmOId(3)
					.withFilmUEId(4)
					.withFilmUId(5)
					.withKommentFilm("A comment")
					.withFilmMimeType("video/mp4")
					.withNodeId(6)
					.withOptions(0)
					.withDeletedDate(LocalDate.of(2026, 1, 15)),
				Film.create()
					.withFilmId(1)
					.withFilnamn("test.mp4")
					.withFilmObjFil("/path/test.mp4")
					.withObjtyp("VIDEO")
					.withDatum("2020-01-01")
					.withDoktitel("Test film")
					.withFilmTId(2)
					.withFilmOplats("Sundsvall")
					.withFilmOId(3)
					.withFilmUEId(4)
					.withFilmUId(5)
					.withKommentFilm("A comment")
					.withFilmMimeType("video/mp4")
					.withNodeId(6)
					.withOptions(0)
					.withDeletedDate(LocalDate.of(2026, 1, 15))));
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
	void toFilmList() {
		final var entities = List.of(
			FilmEntity.create().withFilmId(1).withDoktitel("Film A"),
			FilmEntity.create().withFilmId(2).withDoktitel("Film B"));

		final var result = FilmMapper.toFilmList(entities);

		assertThat(result)
			.extracting(Film::getFilmId, Film::getDoktitel)
			.containsExactly(tuple(1, "Film A"), tuple(2, "Film B"));
	}

	@Test
	void toFilmListWithNull() {
		assertThat(FilmMapper.toFilmList(null)).isEmpty();
	}

}
