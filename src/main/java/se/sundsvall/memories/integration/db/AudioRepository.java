package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.integration.db.model.AudioEntity;

/**
 * Repository for the {@code LJUD} table.
 *
 * <p>
 * Searching is done with {@link JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification,
 * Pageable) findAll(Specification, Pageable)} — see
 * {@link se.sundsvall.memories.integration.db.specification.AudioSpecification AudioSpecification} for the available
 * filters.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) and
 * {@code subject} (from OCM) are not backed by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "audioRepository")
public interface AudioRepository extends JpaRepository<AudioEntity, Integer>, JpaSpecificationExecutor<AudioEntity> {
}
