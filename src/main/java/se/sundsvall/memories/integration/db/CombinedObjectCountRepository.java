package se.sundsvall.memories.integration.db;

import java.util.Map;
import se.sundsvall.memories.api.model.CombinedObjectParameters;

/**
 * The per-type counters behind the combined search. They are a {@code GROUP BY} with an aggregate, which
 * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor} cannot express, so they get a repository
 * fragment of their own rather than a handwritten query.
 */
public interface CombinedObjectCountRepository {

	/**
	 * Counts matching objects per object type, using the same filter as the search.
	 *
	 * @param  parameters the search filters
	 * @return            counts keyed by object type, ordered by type so the response is stable
	 */
	Map<String, Long> countByType(CombinedObjectParameters parameters);
}
