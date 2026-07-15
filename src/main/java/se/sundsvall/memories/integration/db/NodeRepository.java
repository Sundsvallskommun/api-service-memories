package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.NodeEntity;

/**
 * Repository for the {@code TBL_NODES} archive-structure table. Search and hierarchy navigation queries are added by
 * the archive search/detail stories (HYDRAN-2439 / HYDRAN-2440).
 */
@CircuitBreaker(name = "nodeRepository")
public interface NodeRepository extends JpaRepository<NodeEntity, Integer> {
}
