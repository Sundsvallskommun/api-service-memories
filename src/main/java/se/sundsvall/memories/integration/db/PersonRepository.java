package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PersonEntity;

/**
 * Repository for the {@code PERSON} table.
 *
 * <p>
 * <strong>Sorting:</strong> {@link #search} is a native query, so a sort property supplied via {@link Pageable} must be
 * a physical DB column name (e.g. {@code ENAMN}, {@code FNAMN}, {@code FODDAT}, {@code FODFRS}), not a camelCase
 * API/entity field.
 */
@CircuitBreaker(name = "personRepository")
public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {

	/**
	 * Searches published person records with all filter parameters optional (a {@code null} parameter is ignored). A
	 * record is considered published when bit {@code 4} of the {@code OPTIONS} bitmask is set, i.e.
	 * {@code (OPTIONS & 4) = 4}; other status bits may be set simultaneously. The placeholder row {@code P_ID = 0}
	 * ("ingen person") is always excluded.
	 *
	 * <p>
	 * Name and parish filters are case-insensitive substring matches. The year filter compares against the four leading
	 * characters of the {@code FODDAT} varchar column (birth year); rows whose {@code FODDAT} has no numeric year yield
	 * {@code 0} and are therefore excluded once a {@code yearFrom}/{@code yearTo} bound is supplied.
	 *
	 * @param  lastName    substring to match against {@code ENAMN} (nullable)
	 * @param  firstName   substring to match against {@code FNAMN} (nullable)
	 * @param  birthParish substring to match against {@code FODFRS} (nullable)
	 * @param  gender      case-insensitive exact match against {@code KON} (nullable)
	 * @param  yearFrom    inclusive lower bound for the birth year (nullable)
	 * @param  yearTo      inclusive upper bound for the birth year (nullable)
	 * @param  pageable    pagination and sorting criteria
	 * @return             a page of matching {@link PersonEntity} records
	 */
	@Query(value = """
		SELECT * FROM PERSON
		WHERE (`OPTIONS` & 4) = 4 AND P_ID <> 0
		  AND (:lastName IS NULL OR ENAMN LIKE CONCAT('%', :lastName, '%'))
		  AND (:firstName IS NULL OR FNAMN LIKE CONCAT('%', :firstName, '%'))
		  AND (:birthParish IS NULL OR FODFRS LIKE CONCAT('%', :birthParish, '%'))
		  AND (:gender IS NULL OR LOWER(KON) = LOWER(:gender))
		  AND (:yearFrom IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) >= :yearFrom)
		  AND (:yearTo IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) <= :yearTo)
		""",
		countQuery = """
			SELECT COUNT(*) FROM PERSON
			WHERE (`OPTIONS` & 4) = 4 AND P_ID <> 0
			  AND (:lastName IS NULL OR ENAMN LIKE CONCAT('%', :lastName, '%'))
			  AND (:firstName IS NULL OR FNAMN LIKE CONCAT('%', :firstName, '%'))
			  AND (:birthParish IS NULL OR FODFRS LIKE CONCAT('%', :birthParish, '%'))
			  AND (:gender IS NULL OR LOWER(KON) = LOWER(:gender))
			  AND (:yearFrom IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) >= :yearFrom)
			  AND (:yearTo IS NULL OR CAST(LEFT(FODDAT, 4) AS UNSIGNED) <= :yearTo)
			""",
		nativeQuery = true)
	Page<PersonEntity> search(
		@Param("lastName") String lastName,
		@Param("firstName") String firstName,
		@Param("birthParish") String birthParish,
		@Param("gender") String gender,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		Pageable pageable);
}
