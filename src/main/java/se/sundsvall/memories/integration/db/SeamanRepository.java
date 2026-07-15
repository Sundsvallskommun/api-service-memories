package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.SeamanEntity;

/**
 * Repository for the {@code SJOMAN} seamen's-register table.
 *
 * <p>
 * <strong>Sorting:</strong> {@link #search} is a native query, so a sort property supplied via {@link Pageable} must be
 * a physical DB column name (e.g. {@code EFTERNAMN1}, {@code FORNAMN}, {@code FODDAT}, {@code FODFORS}). The
 * {@code SJOMAN} table has no publish ({@code OPTIONS}) or gender column, so neither filter is applied.
 */
@CircuitBreaker(name = "seamanRepository")
public interface SeamanRepository extends JpaRepository<SeamanEntity, Integer> {

	/**
	 * Searches seamen records with all filter parameters optional (a {@code null} parameter is ignored). The last-name
	 * filter matches either surname column ({@code EFTERNAMN1} / {@code EFTERNAMN2}); name and parish filters are
	 * case-insensitive substring matches; the year filter compares against the four leading characters of the
	 * {@code FODDAT} varchar column.
	 *
	 * @param  lastName    substring to match against {@code EFTERNAMN1} or {@code EFTERNAMN2} (nullable)
	 * @param  firstName   substring to match against {@code FORNAMN} (nullable)
	 * @param  birthParish substring to match against {@code FODFORS} (nullable)
	 * @param  yearFrom    inclusive lower bound for the birth year (nullable)
	 * @param  yearTo      inclusive upper bound for the birth year (nullable)
	 * @param  pageable    pagination and sorting criteria
	 * @return             a page of matching {@link SeamanEntity} records
	 */
	@Query(value = """
		SELECT * FROM SJOMAN
		WHERE (:lastName IS NULL OR EFTERNAMN1 LIKE CONCAT('%', :lastName, '%') OR EFTERNAMN2 LIKE CONCAT('%', :lastName, '%'))
		  AND (:firstName IS NULL OR FORNAMN LIKE CONCAT('%', :firstName, '%'))
		  AND (:birthParish IS NULL OR FODFORS LIKE CONCAT('%', :birthParish, '%'))
		  AND (:yearFrom IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) >= :yearFrom)
		  AND (:yearTo IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) <= :yearTo)
		""",
		countQuery = """
			SELECT COUNT(*) FROM SJOMAN
			WHERE (:lastName IS NULL OR EFTERNAMN1 LIKE CONCAT('%', :lastName, '%') OR EFTERNAMN2 LIKE CONCAT('%', :lastName, '%'))
			  AND (:firstName IS NULL OR FORNAMN LIKE CONCAT('%', :firstName, '%'))
			  AND (:birthParish IS NULL OR FODFORS LIKE CONCAT('%', :birthParish, '%'))
			  AND (:yearFrom IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) >= :yearFrom)
			  AND (:yearTo IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) <= :yearTo)
			""",
		nativeQuery = true)
	Page<SeamanEntity> search(
		@Param("lastName") String lastName,
		@Param("firstName") String firstName,
		@Param("birthParish") String birthParish,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		Pageable pageable);
}
