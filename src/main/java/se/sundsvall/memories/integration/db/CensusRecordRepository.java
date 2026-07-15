package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

/**
 * Repository for the {@code MANTAL} census-record table.
 *
 * <p>
 * <strong>Sorting:</strong> {@link #search} is a native query, so a sort property supplied via {@link Pageable} must be
 * a physical DB column name (e.g. {@code MNMNE}, {@code MNMNF}, {@code FODAR}). The {@code MANTAL} table has no publish
 * ({@code OPTIONS}) column, so no publish filter is applied.
 */
@CircuitBreaker(name = "censusRecordRepository")
public interface CensusRecordRepository extends JpaRepository<CensusRecordEntity, Integer> {

	/**
	 * Searches census records with all filter parameters optional (a {@code null} parameter is ignored). Name filters
	 * are case-insensitive substring matches; the year filter compares against the four leading characters of the
	 * {@code FODAR} varchar column (birth year).
	 *
	 * @param  lastName  substring to match against {@code MNMNE} (nullable)
	 * @param  firstName substring to match against {@code MNMNF} (nullable)
	 * @param  gender    case-insensitive exact match against {@code KON} (nullable)
	 * @param  yearFrom  inclusive lower bound for the birth year (nullable)
	 * @param  yearTo    inclusive upper bound for the birth year (nullable)
	 * @param  pageable  pagination and sorting criteria
	 * @return           a page of matching {@link CensusRecordEntity} records
	 */
	@Query(value = """
		SELECT * FROM MANTAL
		WHERE (:lastName IS NULL OR MNMNE LIKE CONCAT('%', :lastName, '%'))
		  AND (:firstName IS NULL OR MNMNF LIKE CONCAT('%', :firstName, '%'))
		  AND (:gender IS NULL OR LOWER(KON) = LOWER(:gender))
		  AND (:yearFrom IS NULL OR CAST(LEFT(FODAR, 4) AS UNSIGNED) >= :yearFrom)
		  AND (:yearTo IS NULL OR CAST(LEFT(FODAR, 4) AS UNSIGNED) <= :yearTo)
		""",
		countQuery = """
			SELECT COUNT(*) FROM MANTAL
			WHERE (:lastName IS NULL OR MNMNE LIKE CONCAT('%', :lastName, '%'))
			  AND (:firstName IS NULL OR MNMNF LIKE CONCAT('%', :firstName, '%'))
			  AND (:gender IS NULL OR LOWER(KON) = LOWER(:gender))
			  AND (:yearFrom IS NULL OR CAST(LEFT(FODAR, 4) AS UNSIGNED) >= :yearFrom)
			  AND (:yearTo IS NULL OR CAST(LEFT(FODAR, 4) AS UNSIGNED) <= :yearTo)
			""",
		nativeQuery = true)
	Page<CensusRecordEntity> search(
		@Param("lastName") String lastName,
		@Param("firstName") String firstName,
		@Param("gender") String gender,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		Pageable pageable);
}
