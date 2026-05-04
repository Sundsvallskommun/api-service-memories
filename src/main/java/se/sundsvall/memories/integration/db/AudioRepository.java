package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.AudioEntity;

@CircuitBreaker(name = "audioRepository")
public interface AudioRepository extends JpaRepository<AudioEntity, Integer> {

	/**
	 * Retrieves a paginated list of all published audio records from the database. A record is considered published when
	 * bit {@code 4} of the {@code OPTIONS} bitmask is set, i.e. {@code (OPTIONS & 4) = 4}. Other status bits may be set
	 * simultaneously.
	 *
	 * @param  pageable the pagination and sorting criteria
	 * @return          a page containing the list of published audio records, encapsulated as {@code AudioEntity} objects
	 */
	@Query(value = "SELECT * FROM LJUD WHERE (`OPTIONS` & 4) = 4",
		countQuery = "SELECT COUNT(*) FROM LJUD WHERE (`OPTIONS` & 4) = 4",
		nativeQuery = true)
	Page<AudioEntity> findAllPublished(Pageable pageable);

	/**
	 * Searches for published audio entries by matching the specified query against the {@code DOKTITEL} and
	 * {@code KOMMENT_LJUD} fields in the database using full-text search with Boolean mode. Only entries where bit
	 * {@code 4} of the {@code OPTIONS} bitmask is set ({@code (OPTIONS & 4) = 4}) are considered; other status bits may be
	 * set simultaneously.
	 *
	 * @param  query    the search query to be matched against the {@code DOKTITEL} and {@code KOMMENT_LJUD} fields.
	 * @param  pageable the pagination parameters for the query result.
	 * @return          a list of {@code AudioEntity} objects that match the search query and meet the filtering criteria.
	 */
	@Query(value = "SELECT * FROM LJUD WHERE MATCH (DOKTITEL, KOMMENT_LJUD) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4",
		countQuery = "SELECT COUNT(*) FROM LJUD WHERE MATCH (DOKTITEL, KOMMENT_LJUD) AGAINST (:query IN BOOLEAN MODE) AND (`OPTIONS` & 4) = 4",
		nativeQuery = true)
	Page<AudioEntity> searchPublished(@Param("query") String query, Pageable pageable);
}
