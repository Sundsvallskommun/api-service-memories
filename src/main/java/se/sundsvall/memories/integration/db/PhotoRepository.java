package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

/**
 * Repository for the {@code FOTO} table.
 *
 * <p>
 * <strong>Sorting:</strong> the queries below are native, so a sort property supplied via {@link Pageable} must be a
 * physical DB column name (e.g. {@code DOKTITEL}), not the camelCase API/entity field. The resolved {@code location}
 * (from TOPOGRAFI) is not backed by a column and cannot be sorted on.
 */
@CircuitBreaker(name = "photoRepository")
public interface PhotoRepository extends JpaRepository<PhotoEntity, Integer> {

	/**
	 * Retrieves a paginated list of all published photos. A photo is considered published when bit {@code 4} of the
	 * {@code OPTIONS} bitmask is set, i.e. {@code (OPTIONS & 4) = 4}. Other status bits may be set simultaneously.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE (`OPTIONS` & 4) = 4",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE (`OPTIONS` & 4) = 4",
		nativeQuery = true)
	Page<PhotoEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches published photos using fulltext search on DOKTITEL and KOMMENT_FF (Boolean mode).
	 *
	 * @param  query    the fulltext query (sanitized for boolean mode)
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of matching published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4",
		nativeQuery = true)
	Page<PhotoEntity> searchPublished(@Param("query") String query, Pageable pageable);

	/**
	 * Retrieves a paginated list of all published photos matching the given OBJTYP (e.g. 'Foto' or 'Föremål').
	 *
	 * @param  objectType the OBJTYP value to filter by
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE (`OPTIONS` & 4) = 4 AND OBJTYP = :objectType",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE (`OPTIONS` & 4) = 4 AND OBJTYP = :objectType",
		nativeQuery = true)
	Page<PhotoEntity> findAllPublishedByObjectType(@Param("objectType") String objectType, Pageable pageable);

	/**
	 * Searches published photos with a fulltext query, filtered by OBJTYP.
	 *
	 * @param  query      the fulltext query (sanitized for boolean mode)
	 * @param  objectType the OBJTYP value to filter by
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4 AND OBJTYP = :objectType",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4 AND OBJTYP = :objectType",
		nativeQuery = true)
	Page<PhotoEntity> searchPublishedByObjectType(@Param("query") String query, @Param("objectType") String objectType, Pageable pageable);

	/**
	 * Searches published photos with optional free-text {@code query}, object type, year range and location. All filters
	 * are optional (a {@code null} parameter is ignored). The year range is matched against the photo's time period: the
	 * period start is {@code TIDIG} and the period end is {@code SENAST} (falling back to {@code TIDIG}); a photo matches
	 * when its period overlaps the requested range. Location matches the resolved TOPOGRAFI name for {@code F_T_ID} or
	 * the free-text {@code F_OPLATS}. Used only when a year/location filter is present.
	 *
	 * @param  query      sanitized fulltext expression, or {@code null} to skip fulltext matching
	 * @param  objectType the OBJTYP value to filter by, or {@code null} for both
	 * @param  yearFrom   inclusive lower bound of the time period (nullable)
	 * @param  yearTo     inclusive upper bound of the time period (nullable)
	 * @param  location   substring to match against the resolved place name or {@code F_OPLATS} (nullable)
	 * @param  pageable   pagination and sorting criteria
	 * @return            a page of matching photo entities
	 */
	@Query(value = """
		SELECT * FROM FOTO
		WHERE (`OPTIONS` & 4) = 4
		  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE))
		  AND (:objectType IS NULL OR OBJTYP = :objectType)
		  AND (:location IS NULL
		       OR F_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
		       OR F_OPLATS LIKE CONCAT('%', :location, '%'))
		  AND (:yearFrom IS NULL OR CAST(LEFT(COALESCE(NULLIF(SENAST, ''), TIDIG), 4) AS UNSIGNED) >= :yearFrom)
		  AND (:yearTo IS NULL OR CAST(LEFT(NULLIF(TIDIG, ''), 4) AS UNSIGNED) <= :yearTo)
		""",
		countQuery = """
			SELECT COUNT(*) FROM FOTO
			WHERE (`OPTIONS` & 4) = 4
			  AND (:query IS NULL OR MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE))
			  AND (:objectType IS NULL OR OBJTYP = :objectType)
			  AND (:location IS NULL
			       OR F_T_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%'))
			       OR F_OPLATS LIKE CONCAT('%', :location, '%'))
			  AND (:yearFrom IS NULL OR CAST(LEFT(COALESCE(NULLIF(SENAST, ''), TIDIG), 4) AS UNSIGNED) >= :yearFrom)
			  AND (:yearTo IS NULL OR CAST(LEFT(NULLIF(TIDIG, ''), 4) AS UNSIGNED) <= :yearTo)
			""",
		nativeQuery = true)
	Page<PhotoEntity> searchFiltered(
		@Param("query") String query,
		@Param("objectType") String objectType,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		Pageable pageable);

	/**
	 * Returns the IDs of all photos connected to the given photo via the {@code FOTO_FOTO} junction table. The relation
	 * is bidirectional — a row with {@code F_ID1 = id} returns {@code F_ID2}, a row with {@code F_ID2 = id} returns
	 * {@code F_ID1}.
	 *
	 * @param  id the photo id to find relations for
	 * @return    the related photo ids (empty list if no relations exist)
	 */
	@Query(value = "SELECT CASE WHEN F_ID1 = :id THEN F_ID2 ELSE F_ID1 END AS related_id FROM FOTO_FOTO WHERE F_ID1 = :id OR F_ID2 = :id ORDER BY related_id",
		nativeQuery = true)
	List<Integer> findRelatedPhotoIds(@Param("id") Integer id);
}
