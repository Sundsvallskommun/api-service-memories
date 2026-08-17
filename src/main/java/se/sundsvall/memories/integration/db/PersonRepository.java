package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
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
	 * The filter predicate shared by {@link #search}'s result query and its count query, kept in one place so the two
	 * can never drift apart.
	 */
	String SEARCH_WHERE_CLAUSE = """
		WHERE (`OPTIONS` & 4) = 4 AND P_ID <> 0
		  AND (:lastName IS NULL OR ENAMN LIKE CONCAT('%', :lastName, '%'))
		  AND (:firstName IS NULL OR FNAMN LIKE CONCAT('%', :firstName, '%'))
		  AND (:birthParish IS NULL OR FODFRS LIKE CONCAT('%', :birthParish, '%'))
		  AND (:gender IS NULL OR LOWER(KON) = LOWER(:gender))
		  AND (:yearFrom IS NULL OR NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0) >= :yearFrom)
		  AND (:yearTo IS NULL OR NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0) <= :yearTo)
		""";

	/**
	 * Searches published person records with all filter parameters optional (a {@code null} parameter is ignored). A
	 * record is considered published when bit {@code 4} of the {@code OPTIONS} bitmask is set, i.e.
	 * {@code (OPTIONS & 4) = 4}; other status bits may be set simultaneously. The placeholder row {@code P_ID = 0}
	 * ("ingen person") is always excluded.
	 *
	 * <p>
	 * Name and parish filters are case-insensitive substring matches. The year filter compares against the four leading
	 * characters of the {@code FODDAT} varchar column (birth year). {@code FODDAT} is dirty free text, so the derived
	 * year is guarded with {@code NULLIF(CAST(...), 0)}: blank and non-numeric values (e.g. "okänt") cast to {@code 0},
	 * which is normalised to {@code NULL} so they never satisfy a {@code yearFrom}/{@code yearTo} bound — without the
	 * guard a {@code yearTo}-only search would wrongly match every undated row.
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
	@Query(value = "SELECT * FROM PERSON " + SEARCH_WHERE_CLAUSE,
		countQuery = "SELECT COUNT(*) FROM PERSON " + SEARCH_WHERE_CLAUSE,
		nativeQuery = true)
	Page<PersonEntity> search(
		@Param("lastName") String lastName,
		@Param("firstName") String firstName,
		@Param("birthParish") String birthParish,
		@Param("gender") String gender,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		Pageable pageable);

	/**
	 * Looks up a single person by id, excluding only the placeholder row {@code P_ID = 0} ("ingen person"). That row is
	 * not a person at all — it is the sentinel other tables point at to mean "no person linked" — so it must never be
	 * retrievable as if it were an archive record.
	 *
	 * <p>
	 * <strong>The published bit is deliberately NOT applied here, unlike in {@link #search}.</strong> Unpublished
	 * persons (those without bit {@code 4} of {@code OPTIONS} set) must remain reachable by id: a planned administrative
	 * interface needs to fetch them directly. Hiding them from {@link #search} while keeping them addressable by id is
	 * the intended behaviour, not an oversight — do not add an {@code (OPTIONS & 4) = 4} filter to this query.
	 *
	 * <p>
	 * Note that the placeholder row cannot be filtered out by the published bit anyway: it carries
	 * {@code OPTIONS = 6}, i.e. it is itself flagged as published. Excluding it requires the explicit
	 * {@code P_ID <> 0} predicate.
	 *
	 * @param  id the person id to look up
	 * @return    the matching {@link PersonEntity}, or an empty {@link Optional} if no such person exists or the id
	 *            refers to the placeholder row
	 */
	@Query(value = "SELECT * FROM PERSON WHERE P_ID = :id AND P_ID <> 0", nativeQuery = true)
	Optional<PersonEntity> findVisibleById(@Param("id") Integer id);
}
