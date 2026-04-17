package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.function.Function;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class FilmMapper {

	private FilmMapper() {}

	/**
	 * Map a single FilmEntity to a Film API model, with the resolved place name {@code plats}.
	 *
	 * @param  entity the source entity
	 * @param  plats  the topografi-resolved place name (nullable)
	 * @return        the mapped {@link Film}, or {@code null} if {@code entity} is null
	 */
	public static Film toFilm(final FilmEntity entity, final String plats) {
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
				.withPlats(plats)
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

	/**
	 * Map a list of FilmEntities, resolving each entity's plats via the provided lookup.
	 *
	 * @param  entities    source entities
	 * @param  platsLookup function from filmTId → resolved plats string (nullable)
	 * @return             list of mapped {@link Film} objects (empty if entities is null)
	 */
	public static List<Film> toFilmList(final List<FilmEntity> entities, final Function<Integer, String> platsLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toFilm(e, platsLookup.apply(e.getFilmTId())))
				.toList())
			.orElse(emptyList());
	}
}
