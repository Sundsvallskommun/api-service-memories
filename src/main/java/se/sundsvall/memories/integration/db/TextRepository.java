package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.TextEntity;

/**
 * Repository for the {@code TEXT} table.
 *
 * <p>
 * <strong>Sorting:</strong> the queries below are native, so a sort property supplied via {@link Pageable} must be a
 * physical DB <em>column</em> name (e.g. {@code DOKTITEL}, {@code DOKDATUM}) — not the camelCase API/entity field —
 * because Spring Data cannot translate property names for native queries. Fields resolved in application code, such as
 * {@code location} (from TOPOGRAFI) and {@code subject} (from OCM), are not backed by a column and therefore cannot be
 * sorted on.
 */
@CircuitBreaker(name = "textRepository")
public interface TextRepository extends JpaRepository<TextEntity, Integer> {

	/**
	 * Retrieves all published records from the {@code TEXT} table. A record is considered published when bit {@code 4} of
	 * the {@code OPTIONS} bitmask is set, i.e. {@code (OPTIONS & 4) = 4}. Other status bits may be set simultaneously.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of published text entities
	 */
	@Query(value = "SELECT * FROM TEXT WHERE (`OPTIONS` & 4) = 4",
		countQuery = "SELECT COUNT(*) FROM TEXT WHERE (`OPTIONS` & 4) = 4",
		nativeQuery = true)
	Page<TextEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches the published records using a fulltext query against {@code DOKTITEL}, {@code KOMMENT_DOC} and
	 * {@code XMLTEXT}. Only records where bit {@code 4} of {@code OPTIONS} is set are returned.
	 *
	 * @param  query    the sanitized fulltext query (boolean mode)
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of matching text entities
	 */
	@Query(value = "SELECT * FROM TEXT WHERE MATCH (DOKTITEL, KOMMENT_DOC, XMLTEXT) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4",
		countQuery = "SELECT COUNT(*) FROM TEXT WHERE MATCH (DOKTITEL, KOMMENT_DOC, XMLTEXT) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4",
		nativeQuery = true)
	Page<TextEntity> searchPublished(@Param("query") String query, Pageable pageable);

	/**
	 * Searches published texts with optional free-text {@code query}, year range and location. The year range is matched
	 * against the document's period: the period start is {@code DOKDATUM} and the period end is
	 * {@code DOKDATUM_SLUT} (falling back to {@code DOKDATUM}); a text matches when its period overlaps the requested
	 * range. Location matches the resolved TOPOGRAFI name for {@code D_T_ID} or the free-text {@code D_OPLATS}. Used only
	 * when a year/location filter is present.
	 */
	@Query(value = """
		SELECT * FROM TEXT
		WHERE (`OPTIONS` & 4) = 4
		  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_DOC, XMLTEXT) AGAINST (:query IN BOOLEAN MODE))
		  AND (:location IS NULL
		       OR D_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
		       OR D_OPLATS LIKE CONCAT('%', :location, '%'))
		  AND (:yearFrom IS NULL OR CAST(LEFT(COALESCE(NULLIF(DOKDATUM_SLUT, ''), DOKDATUM), 4) AS UNSIGNED) >= :yearFrom)
		  AND (:yearTo IS NULL OR CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED) <= :yearTo)
		""",
		countQuery = """
			SELECT COUNT(*) FROM TEXT
			WHERE (`OPTIONS` & 4) = 4
			  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_DOC, XMLTEXT) AGAINST (:query IN BOOLEAN MODE))
			  AND (:location IS NULL
			       OR D_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
			       OR D_OPLATS LIKE CONCAT('%', :location, '%'))
			  AND (:yearFrom IS NULL OR CAST(LEFT(COALESCE(NULLIF(DOKDATUM_SLUT, ''), DOKDATUM), 4) AS UNSIGNED) >= :yearFrom)
			  AND (:yearTo IS NULL OR CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED) <= :yearTo)
			""",
		nativeQuery = true)
	Page<TextEntity> searchFiltered(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		Pageable pageable);
}
