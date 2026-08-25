package se.sundsvall.memories.integration.db;

import java.util.List;
import se.sundsvall.memories.api.model.CombinedObjectParameters;

/**
 * The chip counters. A repository fragment rather than a query method because the free-text filter is one predicate
 * per word, so the query cannot be static, and because building it from the search's own specification keeps the
 * counts and the list on the same rows. The type selection is not applied — see
 * {@link se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification#filtersExcludingObjectType}.
 */
public interface CombinedObjectRepositoryCustom {

	List<TypeCount> countByType(CombinedObjectParameters parameters);

	/** One chip: an object type, and how many rows of it the search matches across every page. */
	record TypeCount(String objectType, long total) {
	}
}
