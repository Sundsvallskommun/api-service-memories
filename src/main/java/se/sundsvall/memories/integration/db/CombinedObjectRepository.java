package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

/**
 * Repository for the {@code VW_MEMORY_OBJECTS} view — the combined object search across all object types.
 *
 * <p>
 * <strong>Sorting:</strong> {@link #search} is a native query, so a sort property supplied via {@link Pageable} must be
 * a view column name: {@code TITLE}, {@code SORT_YEAR} or {@code OBJECT_TYPE}.
 *
 * <p>
 * The free-text {@code query} is a case-insensitive substring match against the pre-concatenated title+comment
 * ({@code SEARCH_TEXT}) — the combined endpoint trades the per-type fulltext relevance for a single, cleanly paginated
 * union. (A per-branch {@code MATCH … AGAINST} union is a possible future optimization.)
 */
@CircuitBreaker(name = "combinedObjectRepository")
public interface CombinedObjectRepository extends JpaRepository<CombinedObjectEntity, String> {

	String FILTER = """
		(:query IS NULL OR SEARCH_TEXT LIKE CONCAT('%', :query, '%'))
		AND (:location IS NULL
		     OR LOCATION_TEXT LIKE CONCAT('%', :location, '%')
		     OR TOPOGRAPHY_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%')))
		AND (:yearFrom IS NULL OR SORT_YEAR >= :yearFrom)
		AND (:yearTo IS NULL OR SORT_YEAR <= :yearTo)
		""";

	/**
	 * Searches the combined object view with all filters optional (a {@code null} parameter is ignored).
	 *
	 * @param  query    substring to match against the title+comment text (nullable)
	 * @param  yearFrom inclusive lower bound for the year (nullable)
	 * @param  yearTo   inclusive upper bound for the year (nullable)
	 * @param  location substring to match against the resolved place name or free-text location (nullable)
	 * @param  pageable pagination and sorting criteria
	 * @return          a page of matching combined objects
	 */
	@Query(value = "SELECT * FROM VW_MEMORY_OBJECTS WHERE " + FILTER,
		countQuery = "SELECT COUNT(*) FROM VW_MEMORY_OBJECTS WHERE " + FILTER,
		nativeQuery = true)
	Page<CombinedObjectEntity> search(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		Pageable pageable);

	/**
	 * Counts matching objects grouped by object type, for the per-type chip counters. Uses the same filter as
	 * {@link #search}.
	 *
	 * @return a list of {@code [objectType, count]} rows
	 */
	@Query(value = "SELECT OBJECT_TYPE AS objectType, COUNT(*) AS total FROM VW_MEMORY_OBJECTS WHERE " + FILTER + " GROUP BY OBJECT_TYPE",
		nativeQuery = true)
	List<TypeCount> countByType(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location);

	/**
	 * Projection for the type-count query.
	 */
	interface TypeCount {
		String getObjectType();

		long getTotal();
	}
}
