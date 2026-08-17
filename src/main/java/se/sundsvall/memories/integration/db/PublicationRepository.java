package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

/**
 * Repository for the {@code PUBL} table.
 *
 * <p>
 * <strong>Sorting:</strong> the queries below are native, so a sort property supplied via {@link Pageable} must be a
 * physical DB column name (e.g. {@code DOKTITEL}), not the camelCase API/entity field. The resolved {@code location}
 * (from TOPOGRAFI) is not backed by a column and cannot be sorted on.
 */
@CircuitBreaker(name = "publicationRepository")
public interface PublicationRepository extends JpaRepository<PublicationEntity, Integer> {

	/**
	 * Row projection for the paged queries below. Paired with {@link #SELECT_COUNT} so that the fetch query and its count
	 * query always share the exact same {@code WHERE} clause constant.
	 */
	String SELECT_ROWS = "SELECT * FROM PUBL ";

	/**
	 * Count projection matching {@link #SELECT_ROWS}.
	 */
	String SELECT_COUNT = "SELECT COUNT(*) FROM PUBL ";

	/**
	 * Restricts the result to published publications, i.e. bit {@code 4} of the {@code OPTIONS} bitmask is set.
	 */
	String WHERE_PUBLISHED = "WHERE (`OPTIONS` & 4) = 4";

	/**
	 * Published publications matching a mandatory fulltext expression.
	 */
	String WHERE_PUBLISHED_AND_FULLTEXT = "WHERE MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4";

	/**
	 * Published publications matching the optional {@code query}, {@code location}, {@code yearFrom} and {@code yearTo}
	 * filters. A {@code null} parameter disables its filter. The year guards wrap the derived year in
	 * {@code NULLIF(CAST(...), 0)} so that unparsable free-text dates (e.g. {@code 'okänt'}), which cast to {@code 0}, are
	 * excluded instead of wrongly satisfying an upper bound.
	 */
	String WHERE_FILTERED = """
		WHERE (`OPTIONS` & 4) = 4
		  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT) AGAINST (:query IN BOOLEAN MODE))
		  AND (:location IS NULL
		       OR P_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
		       OR P_OPLATS LIKE CONCAT('%', :location, '%'))
		  AND (:yearFrom IS NULL OR NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0) >= :yearFrom)
		  AND (:yearTo IS NULL OR NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0) <= :yearTo)
		""";

	/**
	 * Retrieves all published records from the {@code PUBL} table. A record is considered published when bit {@code 4} of
	 * the {@code OPTIONS} bitmask is set, i.e. {@code (OPTIONS & 4) = 4}. Other status bits may be set simultaneously.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a list of PublicationEntity objects representing published records
	 */
	@Query(value = SELECT_ROWS + WHERE_PUBLISHED,
		countQuery = SELECT_COUNT + WHERE_PUBLISHED,
		nativeQuery = true)
	Page<PublicationEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches the published records in the database using a full-text search query. The search is conducted against the
	 * specified fields: {@code DOKTITEL}, {@code KOMMENT_PUBL}, and {@code XMLTEXT}. Only records where bit {@code 4} of
	 * the {@code OPTIONS} bitmask is set ({@code (OPTIONS & 4) = 4}) are returned; other status bits may be set
	 * simultaneously.
	 *
	 * @param  query    the full-text search query to match against the specified fields
	 * @param  pageable the pagination and sorting criteria
	 * @return          a list of PublicationEntity objects that match the search criteria
	 */
	@Query(value = SELECT_ROWS + WHERE_PUBLISHED_AND_FULLTEXT,
		countQuery = SELECT_COUNT + WHERE_PUBLISHED_AND_FULLTEXT,
		nativeQuery = true)
	Page<PublicationEntity> searchPublished(@Param("query") String query, Pageable pageable);

	/**
	 * Searches published publications with optional free-text {@code query}, year range (on {@code DATUM}) and location
	 * (resolved TOPOGRAFI name for {@code P_T_ID}, or free-text {@code P_OPLATS}). All filters are optional. Used only
	 * when a year/location filter is present.
	 */
	@Query(value = SELECT_ROWS + WHERE_FILTERED,
		countQuery = SELECT_COUNT + WHERE_FILTERED,
		nativeQuery = true)
	Page<PublicationEntity> searchFiltered(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		Pageable pageable);
}
