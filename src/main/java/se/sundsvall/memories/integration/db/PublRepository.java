package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PublEntity;

@CircuitBreaker(name = "publRepository")
public interface PublRepository extends JpaRepository<PublEntity, Integer> {

	@Query(value = """
		SELECT * FROM PUBL
		WHERE `OPTIONS` = 4
		""", nativeQuery = true)
	List<PublEntity> findAllPublished();

	@Query(value = """
		SELECT * FROM PUBL
		WHERE MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT) AGAINST (:query IN BOOLEAN MODE)
		  AND `OPTIONS` = 4
		""", nativeQuery = true)
	List<PublEntity> searchPublished(@Param("query") String query);
}
