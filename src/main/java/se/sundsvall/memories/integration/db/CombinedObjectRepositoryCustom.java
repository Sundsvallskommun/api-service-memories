package se.sundsvall.memories.integration.db;

import java.util.List;
import se.sundsvall.memories.api.model.CombinedObjectParameters;

/**
 * The chip counters. They are a repository fragment rather than a query method because the search filter cannot be
 * written as a static query: the free-text filter is one predicate per word, and the number of words is not known until
 * the request arrives. Building the counters from the same specification the search uses is also what keeps the two on
 * the same rows by construction, rather than by two tests policing two copies of the same SQL — which is what the
 * handwritten counter query this replaces needed, and still got wrong for a query containing a LIKE wildcard.
 *
 * <p>
 * The one filter the counters deliberately do not apply is the object type selection: a chip says how many rows its
 * own type would return, so it stays a count the user can act on rather than dropping to zero the moment another type
 * is picked. See
 * {@link se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification#filtersExcludingObjectType}.
 */
public interface CombinedObjectRepositoryCustom {

	List<TypeCount> countByType(CombinedObjectParameters parameters);

	/**
	 * One chip: an object type, and how many rows of it the search matches across every page.
	 */
	record TypeCount(String objectType, long total) {
	}
}
