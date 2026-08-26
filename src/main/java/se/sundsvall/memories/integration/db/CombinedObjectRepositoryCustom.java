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

	/**
	 * The gender counters, over the rows that record one. Gender is its own dimension — a row is both Person and man —
	 * so these overlap with the type counts rather than summing with them, and the gender selection is not applied —
	 * see {@link se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification#filtersExcludingGender}.
	 */
	List<GenderCount> countByGender(CombinedObjectParameters parameters);

	/** One chip: an object type, and how many rows of it the search matches across every page. */
	record TypeCount(String objectType, long total) {
	}

	/** One chip: a gender, and how many rows recording it the search matches across every page. */
	record GenderCount(String gender, long total) {
	}
}
