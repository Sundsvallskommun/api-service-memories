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
	 * Map a single FilmEntity to a Film API model, with the resolved place name {@code location}.
	 *
	 * @param  entity   the source entity
	 * @param  location the topography-resolved place name (nullable)
	 * @return          the mapped {@link Film}, or {@code null} if {@code entity} is null
	 */
	public static Film toFilm(final FilmEntity entity, final String location) {
		return ofNullable(entity)
			.map(e -> Film.create()
				.withFilmId(e.getFilmId())
				.withFilename(e.getFilename())
				.withObjectFilePath(e.getObjectFilePath())
				.withObjectType(e.getObjectType())
				.withDate(e.getDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withTopographyId(e.getTopographyId())
				.withLocationText(e.getLocationText())
				.withLocation(location)
				.withOrganizationId(e.getOrganizationId())
				.withSubEntityId(e.getSubEntityId())
				.withUnitId(e.getUnitId())
				.withComment(e.getComment())
				.withFilmMimeType(e.getFilmMimeType())
				.withNodeId(e.getNodeId())
				.withOptions(e.getOptions())
				.withDeletedDate(e.getDeletedDate()))
			.orElse(null);
	}

	/**
	 * Map a list of FilmEntities, resolving each entity's location via the provided lookup.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup function from topographyId → resolved location string (nullable)
	 * @return                list of mapped {@link Film} objects (empty if entities is null)
	 */
	@SuppressWarnings("java:S4276") // IntFunction<String> would require unboxing topographyId; the field is a nullable Integer.
	public static List<Film> toFilmList(final List<FilmEntity> entities, final Function<Integer, String> locationLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toFilm(e, locationLookup.apply(e.getTopographyId())))
				.toList())
			.orElse(emptyList());
	}
}
