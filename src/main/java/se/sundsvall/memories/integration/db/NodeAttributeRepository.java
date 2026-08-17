package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.NodeAttributeEntity;

/**
 * Repository for the {@code TBL_NODEATTRIBUTES} table.
 */
@CircuitBreaker(name = "nodeAttributeRepository")
public interface NodeAttributeRepository extends JpaRepository<NodeAttributeEntity, Integer> {
}
