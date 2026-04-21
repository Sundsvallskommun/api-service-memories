package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

@CircuitBreaker(name = "topographyRepository")
public interface TopographyRepository extends JpaRepository<TopographyEntity, Integer> {
}
