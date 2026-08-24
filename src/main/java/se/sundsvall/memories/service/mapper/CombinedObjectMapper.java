package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.CombinedObject;
import se.sundsvall.memories.api.model.ObjectTypeCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class CombinedObjectMapper {

	private CombinedObjectMapper() {}

	/**
	 * Map a single {@link CombinedObjectEntity} to a {@link CombinedObject}. The place name comes from the topography
	 * association.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link CombinedObject}, or {@code null} if {@code entity} is null
	 */
	public static CombinedObject toCombinedObject(final CombinedObjectEntity entity) {
		return ofNullable(entity)
			.map(e -> CombinedObject.create()
				.withObjectKey(e.getObjectKey())
				.withSourceId(e.getSourceId())
				.withObjectType(e.getObjectType())
				.withTitle(e.getTitle())
				.withYear(e.getYear())
				.withTopographyId(topographyId(e))
				.withLocationText(e.getLocationText())
				.withLocation(location(e))
				.withCreator(CreatorMapper.toCreator(e.getCreatorPerson(), e.getCreatorLegalEntity())))
			.orElse(null);
	}

	/**
	 * Map a list of {@link CombinedObjectEntity} objects.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link CombinedObject} objects (empty if {@code entities} is null)
	 */
	public static List<CombinedObject> toCombinedObjectList(final List<CombinedObjectEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(CombinedObjectMapper::toCombinedObject)
			.toList();
	}

	/**
	 * Map one chip counter.
	 *
	 * @param  typeCount the counter the search grouped
	 * @return           the mapped {@link ObjectTypeCount}, or {@code null} if {@code typeCount} is null
	 */
	public static ObjectTypeCount toObjectTypeCount(final TypeCount typeCount) {
		return ofNullable(typeCount)
			.map(count -> ObjectTypeCount.create()
				.withObjectType(count.objectType())
				.withCount(count.total()))
			.orElse(null);
	}

	/**
	 * Map the chip counters, keeping the order the search counted them in — which a JSON array preserves and an object
	 * keyed by the type name does not.
	 *
	 * @param  typeCounts the counters the search grouped
	 * @return            list of mapped {@link ObjectTypeCount} objects (empty if {@code typeCounts} is null)
	 */
	public static List<ObjectTypeCount> toObjectTypeCountList(final List<TypeCount> typeCounts) {
		return ofNullable(typeCounts).orElse(emptyList()).stream()
			.map(CombinedObjectMapper::toObjectTypeCount)
			.toList();
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the object
	 * has no place and when {@code TOPOGRAPHY_ID} points at a row that does not exist.
	 */
	private static String location(final CombinedObjectEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	/**
	 * Resolves the raw topography id, which the API exposes alongside the resolved {@code location}. Read through the
	 * association rather than from a second mapping of {@code TOPOGRAPHY_ID}, so the two can never disagree.
	 */
	private static Integer topographyId(final CombinedObjectEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getId)
			.orElse(null);
	}
}
