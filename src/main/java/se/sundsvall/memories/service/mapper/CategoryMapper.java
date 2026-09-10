package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.Map;
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.integration.db.LegalEntityRepositoryCustom.CategoryTotal;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

public final class CategoryMapper {

	private CategoryMapper() {}

	/**
	 * Map a single {@link CategoryEntity} to a {@link Category}.
	 *
	 * @param  entity           the source entity
	 * @param  legalEntityCount how many legal entities the category holds
	 * @return                  the mapped {@link Category}, or {@code null} if {@code entity} is null
	 */
	public static Category toCategory(final CategoryEntity entity, final long legalEntityCount) {
		return ofNullable(entity)
			.map(e -> Category.create()
				.withCategoryId(e.getCategoryId())
				.withCode(e.getCode())
				.withName(e.getName())
				.withLegalEntityCount(legalEntityCount))
			.orElse(null);
	}

	/**
	 * Map the categories in the given order, each with its size. A category the sizes do not mention holds no legal
	 * entity and gets zero rather than nothing, so a client can rely on the field.
	 *
	 * @param  entities the source entities
	 * @param  totals   the sizes the repository grouped, by category id
	 * @return          list of mapped {@link Category} objects (empty if {@code entities} is null)
	 */
	public static List<Category> toCategoryList(final List<CategoryEntity> entities, final List<CategoryTotal> totals) {
		final var totalByCategoryId = totalsByCategoryId(totals);

		return ofNullable(entities).orElse(emptyList()).stream()
			.map(entity -> toCategory(entity, totalByCategoryId.getOrDefault(entity.getCategoryId(), 0L)))
			.toList();
	}

	private static Map<Integer, Long> totalsByCategoryId(final List<CategoryTotal> totals) {
		return ofNullable(totals).orElse(emptyList()).stream()
			.filter(total -> total.categoryId() != null)
			.collect(toMap(CategoryTotal::categoryId, CategoryTotal::total, (first, _) -> first));
	}
}
