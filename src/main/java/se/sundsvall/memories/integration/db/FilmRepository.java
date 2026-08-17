package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.FilmEntity;

/**
 * Repository for the {@code FILM} table.
 *
 * <p>
 * <strong>Sorting:</strong> the queries below are native, so a sort property supplied via {@link Pageable} must be a
 * physical DB column name (e.g. {@code DOKTITEL}), not the camelCase API/entity field. The resolved {@code location}
 * (from TOPOGRAFI) is not backed by a column and cannot be sorted on.
 */
@CircuitBreaker(name = "filmRepository")
public interface FilmRepository extends JpaRepository<FilmEntity, Integer> {

	/**
	 * Row projection for the paged queries below. Paired with {@link #SELECT_COUNT} so that the fetch query and its count
	 * query always share the exact same {@code WHERE} clause constant.
	 */
	String SELECT_ROWS = "SELECT * FROM FILM ";

	/**
	 * Count projection matching {@link #SELECT_ROWS}.
	 */
	String SELECT_COUNT = "SELECT COUNT(*) FROM FILM ";

	/**
	 * Restricts the result to published films, i.e. bit {@code 4} of the {@code OPTIONS} bitmask is set.
	 */
	String WHERE_PUBLISHED = "WHERE (`OPTIONS` & 4) = 4";

	/**
	 * Published films matching a mandatory fulltext expression.
	 */
	String WHERE_PUBLISHED_AND_FULLTEXT = "WHERE MATCH (DOKTITEL, KOMMENT_FILM) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4";

	/**
	 * Published films matching the optional {@code query}, {@code location}, {@code yearFrom} and {@code yearTo} filters.
	 * A {@code null} parameter disables its filter. The year guards wrap the derived year in
	 * {@code NULLIF(CAST(...), 0)} so that unparsable free-text dates (e.g. {@code 'okänt'}), which cast to {@code 0}, are
	 * excluded instead of wrongly satisfying an upper bound.
	 */
	String WHERE_FILTERED = """
		WHERE (`OPTIONS` & 4) = 4
		  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_FILM) AGAINST (:query IN BOOLEAN MODE))
		  AND (:location IS NULL
		       OR FILM_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
		       OR FILM_OPLATS LIKE CONCAT('%', :location, '%'))
		  AND (:yearFrom IS NULL OR NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0) >= :yearFrom)
		  AND (:yearTo IS NULL OR NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0) <= :yearTo)
		""";

	/**
	 * Retrieves a paginated list of all published films from the database. A film is considered published when bit
	 * {@code 4} of the {@code OPTIONS} bitmask is set, i.e. {@code (OPTIONS & 4) = 4}. Other status bits may be set
	 * simultaneously.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page containing the list of published films, encapsulated as {@code FilmEntity} objects
	 */
	@Query(value = SELECT_ROWS + WHERE_PUBLISHED,
		countQuery = SELECT_COUNT + WHERE_PUBLISHED,
		nativeQuery = true)
	Page<FilmEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches for published film entries by matching the specified query against the {@code DOKTITEL} and
	 * {@code KOMMENT_FILM} fields in the database using full-text search with Boolean mode. Only entries where bit
	 * {@code 4} of the {@code OPTIONS} bitmask is set ({@code (OPTIONS & 4) = 4}) are considered; other status bits may be
	 * set simultaneously.
	 *
	 * @param  query    the search query to be matched against the {@code DOKTITEL} and {@code KOMMENT_FILM} fields.
	 * @param  pageable the pagination parameters for the query result.
	 * @return          a list of {@code FilmEntity} objects that match the search query and meet the filtering criteria.
	 */
	@Query(value = SELECT_ROWS + WHERE_PUBLISHED_AND_FULLTEXT,
		countQuery = SELECT_COUNT + WHERE_PUBLISHED_AND_FULLTEXT,
		nativeQuery = true)
	Page<FilmEntity> searchPublished(@Param("query") String query, Pageable pageable);

	/**
	 * Searches published film with optional free-text {@code query}, year range (on {@code DATUM}) and location (resolved
	 * TOPOGRAFI name for {@code FILM_T_ID}, or free-text {@code FILM_OPLATS}). All filters are optional. Used only when a
	 * year/location filter is present.
	 */
	@Query(value = SELECT_ROWS + WHERE_FILTERED,
		countQuery = SELECT_COUNT + WHERE_FILTERED,
		nativeQuery = true)
	Page<FilmEntity> searchFiltered(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		Pageable pageable);
}
