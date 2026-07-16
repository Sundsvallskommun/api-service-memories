package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

/**
 * Repository for the {@code JURPERS} legal-entity table.
 *
 * <p>
 * <strong>Sorting:</strong> {@link #search} is a native query, so a sort property supplied via {@link Pageable} must be
 * a physical DB column name on {@code JURPERS} (e.g. {@code JURPERS}, {@code KAT_ID}, {@code STARTDATUM}).
 */
@CircuitBreaker(name = "legalEntityRepository")
public interface LegalEntityRepository extends JpaRepository<LegalEntityEntity, Integer> {

	/**
	 * Searches published legal entities with all filter parameters optional (a {@code null} parameter is ignored). A
	 * record is considered published when bit {@code 4} of the {@code OPTIONS} bitmask is set, i.e.
	 * {@code (OPTIONS & 4) = 4}; the placeholder row {@code J_ID = 1} ("ingen") is always excluded.
	 *
	 * <p>
	 * The name filter matches {@code JURPERS} or {@code ALTNAMN}. The location filter matches the resolved place name
	 * (TOPOGRAFI {@code TOPNAMN}/{@code PLATS}, joined via {@code T_ID}) or the free-text {@code OPLATS}. The year filter
	 * treats {@code STARTDATUM}/{@code SLUTDATUM} (varchar) as an activity period and keeps entities whose period
	 * overlaps the requested range, comparing on the four leading year characters and treating missing/non-numeric
	 * bounds as open.
	 *
	 * @param  name       substring to match against {@code JURPERS} or {@code ALTNAMN} (nullable)
	 * @param  location   substring to match against the resolved place name or {@code OPLATS} (nullable)
	 * @param  categoryId exact match against {@code KAT_ID} (nullable)
	 * @param  yearFrom   inclusive lower bound of the activity period (nullable)
	 * @param  yearTo     inclusive upper bound of the activity period (nullable)
	 * @param  pageable   pagination and sorting criteria
	 * @return            a page of matching {@link LegalEntityEntity} records
	 */
	@Query(value = """
		SELECT j.* FROM JURPERS j
		LEFT JOIN TOPOGRAFI t ON t.T_ID = j.T_ID
		WHERE (j.`OPTIONS` & 4) = 4 AND j.J_ID <> 1
		  AND (:name IS NULL OR j.JURPERS LIKE CONCAT('%', :name, '%') OR j.ALTNAMN LIKE CONCAT('%', :name, '%'))
		  AND (:location IS NULL OR t.TOPNAMN LIKE CONCAT('%', :location, '%') OR t.PLATS LIKE CONCAT('%', :location, '%') OR j.OPLATS LIKE CONCAT('%', :location, '%'))
		  AND (:categoryId IS NULL OR j.KAT_ID = :categoryId)
		  AND (:yearFrom IS NULL OR j.SLUTDATUM IS NULL OR j.SLUTDATUM = '' OR CAST(LEFT(j.SLUTDATUM, 4) AS UNSIGNED) = 0 OR CAST(LEFT(j.SLUTDATUM, 4) AS UNSIGNED) >= :yearFrom)
		  AND (:yearTo IS NULL OR j.STARTDATUM IS NULL OR j.STARTDATUM = '' OR CAST(LEFT(j.STARTDATUM, 4) AS UNSIGNED) = 0 OR CAST(LEFT(j.STARTDATUM, 4) AS UNSIGNED) <= :yearTo)
		""",
		countQuery = """
			SELECT COUNT(*) FROM JURPERS j
			LEFT JOIN TOPOGRAFI t ON t.T_ID = j.T_ID
			WHERE (j.`OPTIONS` & 4) = 4 AND j.J_ID <> 1
			  AND (:name IS NULL OR j.JURPERS LIKE CONCAT('%', :name, '%') OR j.ALTNAMN LIKE CONCAT('%', :name, '%'))
			  AND (:location IS NULL OR t.TOPNAMN LIKE CONCAT('%', :location, '%') OR t.PLATS LIKE CONCAT('%', :location, '%') OR j.OPLATS LIKE CONCAT('%', :location, '%'))
			  AND (:categoryId IS NULL OR j.KAT_ID = :categoryId)
			  AND (:yearFrom IS NULL OR j.SLUTDATUM IS NULL OR j.SLUTDATUM = '' OR CAST(LEFT(j.SLUTDATUM, 4) AS UNSIGNED) = 0 OR CAST(LEFT(j.SLUTDATUM, 4) AS UNSIGNED) >= :yearFrom)
			  AND (:yearTo IS NULL OR j.STARTDATUM IS NULL OR j.STARTDATUM = '' OR CAST(LEFT(j.STARTDATUM, 4) AS UNSIGNED) = 0 OR CAST(LEFT(j.STARTDATUM, 4) AS UNSIGNED) <= :yearTo)
			""",
		nativeQuery = true)
	Page<LegalEntityEntity> search(
		@Param("name") String name,
		@Param("location") String location,
		@Param("categoryId") Integer categoryId,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		Pageable pageable);
}
