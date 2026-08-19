package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class LegalEntityMapper {

	private LegalEntityMapper() {}

	/**
	 * Map a single {@link LegalEntityEntity} to a {@link LegalEntity} API model. The place name and the category name
	 * both come from their respective associations.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link LegalEntity}, or {@code null} if {@code entity} is null
	 */
	public static LegalEntity toLegalEntity(final LegalEntityEntity entity) {
		return ofNullable(entity)
			.map(e -> LegalEntity.create()
				.withLegalEntityId(e.getLegalEntityId())
				.withName(e.getName())
				.withAlternativeNames(e.getAlternativeNames())
				.withTopographyId(topographyId(e))
				.withLocationText(e.getLocationText())
				.withLocation(location(e))
				.withStartDate(e.getStartDate())
				.withEndDate(e.getEndDate())
				.withPrincipal(e.getPrincipal())
				.withComment(e.getComment())
				.withHistoryFilename(e.getHistoryFilename())
				.withCategoryId(categoryId(e))
				.withCategory(category(e))
				.withOptions(e.getOptions())
				.withDeletedDate(e.getDeletedDate()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link LegalEntityEntity} objects.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link LegalEntity} objects (empty if {@code entities} is null)
	 */
	public static List<LegalEntity> toLegalEntityList(final List<LegalEntityEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(LegalEntityMapper::toLegalEntity)
			.toList();
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the legal
	 * entity has no place and when {@code T_ID} points at a row that does not exist.
	 */
	private static String location(final LegalEntityEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	/**
	 * Resolves the raw topography id, which the API exposes alongside the resolved {@code location}. Read through the
	 * association rather than from a second mapping of {@code T_ID}, so the two can never disagree.
	 */
	private static Integer topographyId(final LegalEntityEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getId)
			.orElse(null);
	}

	/**
	 * Resolves the category name through the category association. The association is {@code null} both when the legal
	 * entity has no category and when {@code KAT_ID} points at a row that does not exist.
	 */
	private static String category(final LegalEntityEntity entity) {
		return ofNullable(entity.getCategory())
			.map(CategoryEntity::getName)
			.orElse(null);
	}

	/**
	 * Resolves the raw category id, which the API exposes alongside the resolved {@code category}. Read through the
	 * association rather than from a second mapping of {@code KAT_ID}, so the two can never disagree.
	 */
	private static Integer categoryId(final LegalEntityEntity entity) {
		return ofNullable(entity.getCategory())
			.map(CategoryEntity::getCategoryId)
			.orElse(null);
	}
}
