package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.CategoryCount;
import se.sundsvall.memories.api.model.CombinedObject;
import se.sundsvall.memories.api.model.GenderCount;
import se.sundsvall.memories.api.model.ObjectTypeCount;
import se.sundsvall.memories.api.model.TopographyCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.service.util.Names.swedishOrder;

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
				.withCreator(CreatorMapper.toCreator(e.getCreatorPerson(), e.getCreatorLegalEntity()))
				.withNodeId(e.getNodeId()))
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
	 * Map the chip counters, in the order the chips are shown. The search groups them in the database's collation,
	 * which is not the one a Swedish list wants — see {@link se.sundsvall.memories.service.util.Names}.
	 *
	 * @param  typeCounts the counters the search grouped
	 * @return            list of mapped {@link ObjectTypeCount} objects (empty if {@code typeCounts} is null)
	 */
	public static List<ObjectTypeCount> toObjectTypeCountList(final List<TypeCount> typeCounts) {
		return ofNullable(typeCounts).orElse(emptyList()).stream()
			.map(CombinedObjectMapper::toObjectTypeCount)
			.sorted(comparing(ObjectTypeCount::getObjectType, swedishOrder()))
			.toList();
	}

	/**
	 * Map one gender chip counter.
	 *
	 * @param  genderCount the counter the search grouped
	 * @return             the mapped {@link GenderCount}, or {@code null} if {@code genderCount} is null
	 */
	public static GenderCount toGenderCount(final CombinedObjectRepositoryCustom.GenderCount genderCount) {
		return ofNullable(genderCount)
			.map(count -> GenderCount.create()
				.withGender(count.gender())
				.withCount(count.total()))
			.orElse(null);
	}

	/**
	 * Map the gender chip counters, in the order the chips are shown.
	 *
	 * @param  genderCounts the counters the search grouped
	 * @return              list of mapped {@link GenderCount} objects (empty if {@code genderCounts} is null)
	 */
	public static List<GenderCount> toGenderCountList(final List<CombinedObjectRepositoryCustom.GenderCount> genderCounts) {
		return ofNullable(genderCounts).orElse(emptyList()).stream()
			.map(CombinedObjectMapper::toGenderCount)
			.sorted(comparing(GenderCount::getGender, swedishOrder()))
			.toList();
	}

	/**
	 * Map one category chip counter.
	 *
	 * @param  categoryCount the counter the search grouped
	 * @return               the mapped {@link CategoryCount}, or {@code null} if {@code categoryCount} is null
	 */
	public static CategoryCount toCategoryCount(final CombinedObjectRepositoryCustom.CategoryCount categoryCount) {
		return ofNullable(categoryCount)
			.map(count -> CategoryCount.create()
				.withCategoryId(count.categoryId())
				.withName(count.name())
				.withCount(count.total()))
			.orElse(null);
	}

	/**
	 * Map the category chip counters, in the order the chips are shown — the same order {@code /categories} lists the
	 * very same names in, so the chips and the dropdown cannot disagree.
	 *
	 * @param  categoryCounts the counters the search grouped
	 * @return                list of mapped {@link CategoryCount} objects (empty if {@code categoryCounts} is null)
	 */
	public static List<CategoryCount> toCategoryCountList(final List<CombinedObjectRepositoryCustom.CategoryCount> categoryCounts) {
		return ofNullable(categoryCounts).orElse(emptyList()).stream()
			.map(CombinedObjectMapper::toCategoryCount)
			.sorted(comparing(CategoryCount::getName, swedishOrder())
				.thenComparing(CategoryCount::getCategoryId))
			.toList();
	}

	/**
	 * Map one place chip counter. The label is built by the rule behind {@link TopographyEntity#getDisplayName()}, so a
	 * chip reads exactly as the place does in {@code /topographies} and on an object's {@code location}.
	 *
	 * @param  topographyCount the counter the search grouped
	 * @return                 the mapped {@link TopographyCount}, or {@code null} if {@code topographyCount} is null
	 */
	public static TopographyCount toTopographyCount(final CombinedObjectRepositoryCustom.TopographyCount topographyCount) {
		return ofNullable(topographyCount)
			.map(count -> TopographyCount.create()
				.withTopographyId(count.topographyId())
				.withName(TopographyEntity.displayName(count.place(), count.name()))
				.withCount(count.total()))
			.orElse(null);
	}

	/**
	 * Map the place chip counters, in the order the chips are shown — the same order {@code /topographies} lists the
	 * very same names in.
	 *
	 * @param  topographyCounts the counters the search grouped
	 * @return                  list of mapped {@link TopographyCount} objects (empty if {@code topographyCounts} is null)
	 */
	public static List<TopographyCount> toTopographyCountList(final List<CombinedObjectRepositoryCustom.TopographyCount> topographyCounts) {
		return ofNullable(topographyCounts).orElse(emptyList()).stream()
			.map(CombinedObjectMapper::toTopographyCount)
			.sorted(comparing(TopographyCount::getName, swedishOrder())
				.thenComparing(TopographyCount::getTopographyId))
			.toList();
	}

	/** Resolves the place name through the topography association, which is {@code null} when there is no place. */
	private static String location(final CombinedObjectEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	/** The raw topography id, read through the association so it cannot disagree with the resolved location. */
	private static Integer topographyId(final CombinedObjectEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getId)
			.orElse(null);
	}
}
