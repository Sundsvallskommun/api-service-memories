package se.sundsvall.memories.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.integration.db.CategoryRepository;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.service.mapper.CategoryMapper;

import static java.util.Comparator.comparing;
import static se.sundsvall.memories.service.util.Names.swedishOrder;

/**
 * The {@code /categories} dropdown. Read per request rather than cached: the table is tiny and the count behind the
 * sizes is one cheap GROUP BY, so a cache would buy nothing to set against sizes gone stale after an archive edit. The
 * {@code /objects} facet counts are cached despite that staleness because each of them is a pass over the combined
 * view.
 */
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final LegalEntityRepository legalEntityRepository;

	public CategoryService(final CategoryRepository categoryRepository, final LegalEntityRepository legalEntityRepository) {
		this.categoryRepository = categoryRepository;
		this.legalEntityRepository = legalEntityRepository;
	}

	/** Ordered here rather than in SQL, see {@link se.sundsvall.memories.service.util.Names}. */
	@Transactional(readOnly = true)
	public List<Category> getCategories() {
		final var categories = categoryRepository.findAllSelectable();

		final var totals = legalEntityRepository.countByCategory();

		return CategoryMapper.toCategoryList(categories, totals).stream()
			.sorted(comparing(Category::getName, swedishOrder())
				.thenComparing(Category::getCategoryId))
			.toList();
	}
}
