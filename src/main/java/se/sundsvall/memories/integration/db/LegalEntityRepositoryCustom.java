package se.sundsvall.memories.integration.db;

import java.util.List;

/**
 * The category sizes. A repository fragment rather than a query method because it is built from the legal entity
 * search's own specification, which keeps the sizes on the same rows as {@code /legal-entities?categoryId=...} —
 * published, undeleted and not the sentinel.
 */
public interface LegalEntityRepositoryCustom {

	List<CategoryTotal> countByCategory();

	/** One category, and how many legal entities the search would return for it. */
	record CategoryTotal(Integer categoryId, long total) {
	}
}
