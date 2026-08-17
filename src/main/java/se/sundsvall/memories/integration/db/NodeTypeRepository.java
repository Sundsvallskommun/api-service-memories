package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;

/**
 * Repository for the {@code TBL_NODETYPES} lookup table.
 */
@CircuitBreaker(name = "nodeTypeRepository")
public interface NodeTypeRepository extends JpaRepository<NodeTypeEntity, Integer> {
}
