package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.OcmEntity;

@CircuitBreaker(name = "ocmRepository")
public interface OcmRepository extends JpaRepository<OcmEntity, Integer> {
}
