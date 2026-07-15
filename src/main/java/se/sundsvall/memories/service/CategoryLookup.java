package se.sundsvall.memories.service;

import jakarta.annotation.PostConstruct;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.integration.db.CategoryRepository;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * Loads the small {@code KATEGORI} table into memory at startup so that legal entities (and archive nodes) can resolve
 * their category FK to a name without joining at query time, and so the {@code /categories} dropdown endpoint can be
 * served from memory. The table is tiny (~19 rows) and effectively static, so a single eager load is fine.
 */
@Component
public class CategoryLookup {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryLookup.class);

	private final CategoryRepository categoryRepository;
	private Map<Integer, String> nameByCategoryId = Map.of();
	private List<Category> categories = List.of();

	public CategoryLookup(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@PostConstruct
	void loadCache() {
		final var entities = categoryRepository.findAll();
		nameByCategoryId = entities.stream()
			.map(entity -> new SimpleImmutableEntry<>(entity.getCategoryId(), entity.getName()))
			.filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()))
			.collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
		categories = entities.stream()
			.map(CategoryLookup::toCategory)
			.sorted(Comparator.comparing(Category::getName, nullsLast(String::compareToIgnoreCase)))
			.toList();
		LOGGER.info("Loaded {} category entries into cache", categories.size());
	}

	/**
	 * Resolves a category FK to its name.
	 *
	 * @param  categoryId the category id (may be null or the placeholder 1)
	 * @return            the resolved category name, or {@code null} if the id is missing or unknown
	 */
	public String resolve(final Integer categoryId) {
		if (categoryId == null) {
			return null;
		}
		if (nameByCategoryId.isEmpty()) {
			loadCache();
		}
		return nameByCategoryId.get(categoryId);
	}

	/**
	 * Returns all categories (sorted by name) for the dropdown endpoint.
	 *
	 * @return an immutable list of categories
	 */
	public List<Category> getAllCategories() {
		if (categories.isEmpty()) {
			loadCache();
		}
		return categories;
	}

	private static Category toCategory(final CategoryEntity entity) {
		return Category.create()
			.withCategoryId(entity.getCategoryId())
			.withCode(entity.getCode())
			.withName(entity.getName());
	}
}
