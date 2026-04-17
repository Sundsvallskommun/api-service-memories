package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.FotoEntity;

@CircuitBreaker(name = "fotoRepository")
public interface FotoRepository extends JpaRepository<FotoEntity, Integer> {

	/**
	 * Retrieves a paginated list of all published photos. A photo is considered published if its `OPTIONS` column equals 4.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE `OPTIONS` = 4",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE `OPTIONS` = 4",
		nativeQuery = true)
	Page<FotoEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches published photos using fulltext search on DOKTITEL and KOMMENT_FF (Boolean mode).
	 *
	 * @param  query    the fulltext query (sanitized for boolean mode)
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of matching published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND `OPTIONS` = 4",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND `OPTIONS` = 4",
		nativeQuery = true)
	Page<FotoEntity> searchPublished(@Param("query") String query, Pageable pageable);

	/**
	 * Retrieves a paginated list of all published photos matching the given OBJTYP (e.g. 'Foto' or 'Föremål').
	 *
	 * @param  objtyp   the OBJTYP value to filter by
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of matching published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE `OPTIONS` = 4 AND OBJTYP = :objtyp",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE `OPTIONS` = 4 AND OBJTYP = :objtyp",
		nativeQuery = true)
	Page<FotoEntity> findAllPublishedByObjtyp(@Param("objtyp") String objtyp, Pageable pageable);

	/**
	 * Searches published photos with a fulltext query, filtered by OBJTYP.
	 *
	 * @param  query    the fulltext query (sanitized for boolean mode)
	 * @param  objtyp   the OBJTYP value to filter by
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page of matching published photo entities
	 */
	@Query(value = "SELECT * FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND `OPTIONS` = 4 AND OBJTYP = :objtyp",
		countQuery = "SELECT COUNT(*) FROM FOTO WHERE MATCH (DOKTITEL, KOMMENT_FF) AGAINST (:query IN BOOLEAN MODE) AND `OPTIONS` = 4 AND OBJTYP = :objtyp",
		nativeQuery = true)
	Page<FotoEntity> searchPublishedByObjtyp(@Param("query") String query, @Param("objtyp") String objtyp, Pageable pageable);
}
