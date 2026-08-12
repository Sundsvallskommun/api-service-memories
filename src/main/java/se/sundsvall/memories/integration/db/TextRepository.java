package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.integration.db.model.TextEntity;

/**
 * Repository for the {@code TEXT} table.
 *
 * <p>
 * Searching is done with {@link JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification,
 * Pageable) findAll(Specification, Pageable)} — see
 * {@link se.sundsvall.memories.integration.db.specification.TextSpecifications TextSpecifications} for the available
 * filters.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) and
 * {@code subject} (from OCM) are not backed by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "textRepository")
public interface TextRepository extends JpaRepository<TextEntity, Integer>, JpaSpecificationExecutor<TextEntity> {
}
