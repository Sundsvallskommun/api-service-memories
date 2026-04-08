package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class FilmMapper {

	private FilmMapper() {}

	public static Film toFilm(final FilmEntity entity) {
		return ofNullable(entity)
			.map(e -> Film.create()
				.withFilmId(e.getFilmId())
				.withFilnamn(e.getFilnamn())
				.withFilmObjFil(e.getFilmObjFil())
				.withObjtyp(e.getObjtyp())
				.withDatum(e.getDatum())
				.withDoktitel(e.getDoktitel())
				.withFilmTId(e.getFilmTId())
				.withFilmOplats(e.getFilmOplats())
				.withFilmOId(e.getFilmOId())
				.withFilmUEId(e.getFilmUEId())
				.withFilmUId(e.getFilmUId())
				.withKommentFilm(e.getKommentFilm())
				.withFilmMimeType(e.getFilmMimeType())
				.withNodeId(e.getNodeId())
				.withOptions(e.getOptions())
				.withDeletedDate(e.getDeletedDate()))
			.orElse(null);
	}

	public static List<Film> toFilmList(final List<FilmEntity> entities) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(FilmMapper::toFilm)
				.toList())
			.orElse(emptyList());
	}
}
