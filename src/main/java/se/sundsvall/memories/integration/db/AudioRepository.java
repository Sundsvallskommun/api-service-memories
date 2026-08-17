package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.AudioEntity;

/**
 * Repository for the {@code LJUD} table.
 *
 * <p>
 * <strong>Sorting:</strong> the queries below are native, so a sort property supplied via {@link Pageable} must be a
 * physical DB column name (e.g. {@code DOKTITEL}), not the camelCase API/entity field. The resolved {@code location}
 * (from TOPOGRAFI) and {@code subject} (from OCM) are not backed by columns and cannot be sorted on.
 */
@CircuitBreaker(name = "audioRepository")
public interface AudioRepository extends JpaRepository<AudioEntity, Integer> {

	/**
	 * Row projection for the paged queries below. Paired with {@link #SELECT_COUNT} so that the fetch query and its count
	 * query always share the exact same {@code WHERE} clause constant.
	 */
	String SELECT_ROWS = "SELECT * FROM LJUD ";

	/**
	 * Count projection matching {@link #SELECT_ROWS}.
	 */
	String SELECT_COUNT = "SELECT COUNT(*) FROM LJUD ";

	/**
	 * Restricts the result to published audio records, i.e. bit {@code 4} of the {@code OPTIONS} bitmask is set.
	 */
	String WHERE_PUBLISHED = "WHERE (`OPTIONS` & 4) = 4";

	/**
	 * Published audio records matching a mandatory fulltext expression.
	 */
	String WHERE_PUBLISHED_AND_FULLTEXT = "WHERE MATCH (DOKTITEL, KOMMENT_LJUD) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4";

	/**
	 * Published audio records matching the optional {@code query}, {@code location}, {@code yearFrom} and {@code yearTo}
	 * filters. A {@code null} parameter disables its filter. The year guards wrap the derived year in
	 * {@code NULLIF(CAST(...), 0)} so that unparsable free-text dates (e.g. {@code 'okänt'}), which cast to {@code 0}, are
	 * excluded instead of wrongly satisfying an upper bound.
	 */
	String WHERE_FILTERED = """
		WHERE (`OPTIONS` & 4) = 4
		  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_LJUD) AGAINST (:query IN BOOLEAN MODE))
		  AND (:location IS NULL
		       OR LJUD_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
		       OR LJUD_OPLATS LIKE CONCAT('%', :location, '%'))
		  AND (:yearFrom IS NULL OR NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0) >= :yearFrom)
		  AND (:yearTo IS NULL OR NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0) <= :yearTo)
		""";

	/**
	 * Retrieves a paginated list of all published audio records from the database. A record is considered published when
	 * bit {@code 4} of the {@code OPTIONS} bitmask is set, i.e. {@code (OPTIONS & 4) = 4}. Other status bits may be set
	 * simultaneously.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page containing the list of published audio records, encapsulated as {@code AudioEntity} objects
	 */
	@Query(value = SELECT_ROWS + WHERE_PUBLISHED,
		countQuery = SELECT_COUNT + WHERE_PUBLISHED,
		nativeQuery = true)
	Page<AudioEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches for published audio entries by matching the specified query against the {@code DOKTITEL} and
	 * {@code KOMMENT_LJUD} fields in the database using full-text search with Boolean mode. Only entries where bit
	 * {@code 4} of the {@code OPTIONS} bitmask is set ({@code (OPTIONS & 4) = 4}) are considered; other status bits may be
	 * set simultaneously.
	 *
	 * @param  query    the search query to be matched against the {@code DOKTITEL} and {@code KOMMENT_LJUD} fields.
	 * @param  pageable the pagination parameters for the query result.
	 * @return          a list of {@code AudioEntity} objects that match the search query and meet the filtering criteria.
	 */
	@Query(value = SELECT_ROWS + WHERE_PUBLISHED_AND_FULLTEXT,
		countQuery = SELECT_COUNT + WHERE_PUBLISHED_AND_FULLTEXT,
		nativeQuery = true)
	Page<AudioEntity> searchPublished(@Param("query") String query, Pageable pageable);

	/**
	 * Searches published audio with optional free-text {@code query}, year range and location. All filters are optional
	 * (a {@code null} parameter is ignored). The year filter compares the four leading characters of {@code DATUM}; the
	 * location filter matches the resolved place name (TOPOGRAFI {@code TOPNAMN}/{@code PLATS} for {@code LJUD_T_ID}) or
	 * the free-text {@code LJUD_OPLATS}. Used only when a year/location filter is present; the plain search paths above
	 * remain the fast path for the common case.
	 *
	 * @param  query    sanitized fulltext expression, or {@code null} to skip fulltext matching
	 * @param  yearFrom inclusive lower bound for the year (nullable)
	 * @param  yearTo   inclusive upper bound for the year (nullable)
	 * @param  location substring to match against the resolved place name or {@code LJUD_OPLATS} (nullable)
	 * @param  pageable pagination and sorting criteria
	 * @return          a page of matching audio records
	 */
	@Query(value = SELECT_ROWS + WHERE_FILTERED,
		countQuery = SELECT_COUNT + WHERE_FILTERED,
		nativeQuery = true)
	Page<AudioEntity> searchFiltered(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		Pageable pageable);
}
