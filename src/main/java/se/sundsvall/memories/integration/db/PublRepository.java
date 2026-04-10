package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PublEntity;

/**
 * Repository interface for managing PublEntity objects interacting with the database. It includes methods for
 * retrieving and searching published records.
 */
@CircuitBreaker(name = "publRepository")
public interface PublRepository extends JpaRepository<PublEntity, Integer> {

	/**
	 * Retrieves all published records from the PUBL table where the `OPTIONS` field equals 4.Option 4 means the record is
	 * published.
	 *
	 * @return a list of PublEntity objects representing published records
	 */
	@Query(value = """
		SELECT * FROM PUBL
		WHERE `OPTIONS` = 4
		""", nativeQuery = true)
	List<PublEntity> findAllPublished();

	/**
	 * Searches the published records in the database using a full-text search query. The search is conducted against the
	 * specified fields: DOKTITEL, KOMMENT_PUBL, and XMLTEXT. Only records with the field `OPTIONS` equal to 4 are returned.
	 * Option 4 means
	 * the record is published.
	 *
	 * @param  query the full-text search query to match against the specified fields
	 * @return       a list of PublEntity objects that match the search criteria
	 */
	@Query(value = """
		SELECT * FROM PUBL
		WHERE MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT) AGAINST (:query IN BOOLEAN MODE)
		  AND `OPTIONS` = 4
		""", nativeQuery = true)
	List<PublEntity> searchPublished(@Param("query") String query);
}
