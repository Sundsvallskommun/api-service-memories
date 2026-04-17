package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.TopografiEntity;

@CircuitBreaker(name = "topografiRepository")
public interface TopografiRepository extends JpaRepository<TopografiEntity, Integer> {
}
