package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Topography;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class TopographyMapper {

	private TopographyMapper() {}

	/**
	 * Map a single {@link TopographyEntity} to a {@link Topography}. The display name is the same one an object's
	 * location is resolved to, so a client can match the two.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Topography}, or {@code null} if {@code entity} is null
	 */
	public static Topography toTopography(final TopographyEntity entity) {
		return ofNullable(entity)
			.map(e -> Topography.create()
				.withTopographyId(e.getId())
				.withDisplayName(e.getDisplayName())
				.withName(e.getName())
				.withCode(e.getCode())
				.withPlace(e.getPlace())
				.withMunicipality(e.getMunicipality()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link TopographyEntity} objects, keeping their order.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Topography} objects (empty if {@code entities} is null)
	 */
	public static List<Topography> toTopographyList(final List<TopographyEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(TopographyMapper::toTopography)
			.toList();
	}
}
