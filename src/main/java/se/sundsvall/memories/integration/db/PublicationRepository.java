package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

@CircuitBreaker(name = "publicationRepository")
public interface PublicationRepository extends JpaRepository<PublicationEntity, Integer> {

	/**
	 * Retrieves all published records from the PUBL table where the `OPTIONS` field equals 4. Option 4 means the record is
	 * published.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a list of PublicationEntity objects representing published records
	 */
	@Query(value = "SELECT * FROM PUBL WHERE `OPTIONS` = 4",
		countQuery = "SELECT COUNT(*) FROM PUBL WHERE `OPTIONS` = 4",
		nativeQuery = true)
	Page<PublicationEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches the published records in the database using a full-text search query. The search is conducted against the
	 * specified fields: DOKTITEL, KOMMENT_PUBL, and XMLTEXT. Only records with the field `OPTIONS` equal to 4 are returned.
	 * Option 4 means the record is published.
	 *
	 * @param  query    the full-text search query to match against the specified fields
	 * @param  pageable the pagination and sorting criteria
	 * @return          a list of PublicationEntity objects that match the search criteria
	 */
	@Query(value = "SELECT * FROM PUBL WHERE MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT) AGAINST (:query IN BOOLEAN MODE) AND `OPTIONS` = 4",
		countQuery = "SELECT COUNT(*) FROM PUBL WHERE MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT) AGAINST (:query IN BOOLEAN MODE) AND `OPTIONS` = 4",
		nativeQuery = true)
	Page<PublicationEntity> searchPublished(@Param("query") String query, Pageable pageable);
}
