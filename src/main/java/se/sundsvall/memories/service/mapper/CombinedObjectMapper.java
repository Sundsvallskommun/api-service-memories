package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.CombinedObject;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class CombinedObjectMapper {

	private CombinedObjectMapper() {}

	/**
	 * Map a single {@link CombinedObjectEntity} to a {@link CombinedObject} with a resolved {@code location}.
	 *
	 * @param  entity   the source entity
	 * @param  location the topography-resolved place name (nullable)
	 * @return          the mapped {@link CombinedObject}, or {@code null} if {@code entity} is null
	 */
	public static CombinedObject toCombinedObject(final CombinedObjectEntity entity, final String location) {
		return ofNullable(entity)
			.map(e -> CombinedObject.create()
				.withObjectKey(e.getObjectKey())
				.withSourceId(e.getSourceId())
				.withObjectType(e.getObjectType())
				.withTitle(e.getTitle())
				.withYear(e.getYear())
				.withTopographyId(e.getTopographyId())
				.withLocationText(e.getLocationText())
				.withLocation(location))
			.orElse(null);
	}

	/**
	 * Map a list of {@link CombinedObjectEntity} objects, resolving each entity's location via the provided lookup.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup resolver from topographyId → location string (nullable)
	 * @return                list of mapped {@link CombinedObject} objects (empty if {@code entities} is null)
	 */
	public static List<CombinedObject> toCombinedObjectList(final List<CombinedObjectEntity> entities, final ReferenceResolver locationLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toCombinedObject(e, locationLookup.resolve(e.getTopographyId())))
			.toList();
	}
}
