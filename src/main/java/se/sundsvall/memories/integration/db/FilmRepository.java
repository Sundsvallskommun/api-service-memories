package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.FilmEntity;

@CircuitBreaker(name = "filmRepository")
public interface FilmRepository extends JpaRepository<FilmEntity, Integer> {

	@Query(value = """
		SELECT * FROM FILM
		WHERE `OPTIONS` = 4
		""", nativeQuery = true)
	List<FilmEntity> findAllPublished();

	/**
	 * Searches for published film entries by matching the specified query against the `DOKTITEL` and `KOMMENT_FILM` fields
	 * in the database using full-text search with Boolean mode. Only entries with `OPTIONS` set to 4 are considered. Option
	 * 4 means the
	 * record is published.
	 *
	 * @param  query the search query to be matched against the `DOKTITEL` and `KOMMENT_FILM` fields.
	 * @return       a list of {@code FilmEntity} objects that match the search query and meet the filtering criteria.
	 */
	@Query(value = """
		SELECT * FROM FILM
		WHERE MATCH (DOKTITEL, KOMMENT_FILM) AGAINST (:query IN BOOLEAN MODE)
		  AND `OPTIONS` = 4
		""", nativeQuery = true)
	List<FilmEntity> searchPublished(@Param("query") String query);
}
