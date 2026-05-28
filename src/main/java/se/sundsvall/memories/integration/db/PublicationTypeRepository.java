package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.PublicationTypeEntity;

@CircuitBreaker(name = "publicationTypeRepository")
public interface PublicationTypeRepository extends JpaRepository<PublicationTypeEntity, Integer> {
}
