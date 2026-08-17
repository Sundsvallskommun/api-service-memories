package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class LegalEntityMapper {

	private LegalEntityMapper() {}

	/**
	 * Map a single {@link LegalEntityEntity} to a {@link LegalEntity} API model with resolved {@code location} and
	 * {@code category}.
	 *
	 * @param  entity   the source entity
	 * @param  location the topography-resolved place name (nullable)
	 * @param  category the category-resolved name (nullable)
	 * @return          the mapped {@link LegalEntity}, or {@code null} if {@code entity} is null
	 */
	public static LegalEntity toLegalEntity(final LegalEntityEntity entity, final String location, final String category) {
		return ofNullable(entity)
			.map(e -> LegalEntity.create()
				.withLegalEntityId(e.getLegalEntityId())
				.withName(e.getName())
				.withAlternativeNames(e.getAlternativeNames())
				.withTopographyId(e.getTopographyId())
				.withLocationText(e.getLocationText())
				.withLocation(location)
				.withStartDate(e.getStartDate())
				.withEndDate(e.getEndDate())
				.withPrincipal(e.getPrincipal())
				.withComment(e.getComment())
				.withHistoryFilename(e.getHistoryFilename())
				.withCategoryId(e.getCategoryId())
				.withCategory(category)
				.withOptions(e.getOptions())
				.withDeletedDate(e.getDeletedDate()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link LegalEntityEntity} objects, resolving each entity's location and category via the provided
	 * lookups.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup resolver from topographyId → location string (nullable)
	 * @param  categoryLookup resolver from categoryId → category name (nullable)
	 * @return                list of mapped {@link LegalEntity} objects (empty if {@code entities} is null)
	 */
	public static List<LegalEntity> toLegalEntityList(final List<LegalEntityEntity> entities, final ReferenceResolver locationLookup,
		final ReferenceResolver categoryLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toLegalEntity(e, locationLookup.resolve(e.getTopographyId()), categoryLookup.resolve(e.getCategoryId())))
			.toList();
	}
}
