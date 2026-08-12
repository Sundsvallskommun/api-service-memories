package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class FilmMapper {

	private FilmMapper() {}

	/**
	 * Map a single FilmEntity to a Film API model.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Film}, or {@code null} if {@code entity} is null
	 */
	public static Film toFilm(final FilmEntity entity) {
		return ofNullable(entity)
			.map(e -> Film.create()
				.withFilmId(e.getFilmId())
				.withFilename(e.getFilename())
				.withObjectFilePath(e.getObjectFilePath())
				.withObjectType(e.getObjectType())
				.withDate(e.getDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withTopographyId(topographyId(e))
				.withLocationText(e.getLocationText())
				.withLocation(location(e))
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
	 * Map a list of FilmEntities.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Film} objects (empty if entities is null)
	 */
	public static List<Film> toFilmList(final List<FilmEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(FilmMapper::toFilm)
			.toList();
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the film has
	 * no place and when {@code FILM_T_ID} points at a row that does not exist.
	 */
	private static String location(final FilmEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	/**
	 * Resolves the raw topography id, which the API exposes alongside the resolved {@code location}. Read through the
	 * association rather than from a second mapping of {@code FILM_T_ID}, so the two can never disagree.
	 */
	private static Integer topographyId(final FilmEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getTId)
			.orElse(null);
	}
}
