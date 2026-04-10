package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.PublTypEntity;

@CircuitBreaker(name = "publTypRepository")
public interface PublTypRepository extends JpaRepository<PublTypEntity, Integer> {
}
