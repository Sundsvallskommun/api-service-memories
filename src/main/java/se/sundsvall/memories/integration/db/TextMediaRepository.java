package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;

@CircuitBreaker(name = "textMediaRepository")
public interface TextMediaRepository extends JpaRepository<TextMediaEntity, Integer> {

	List<TextMediaEntity> findByTextIdOrderById(Integer textId);
}
