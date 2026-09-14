package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;

import static se.sundsvall.memories.integration.db.specification.InstitutionSpecification.hasName;

/**
 * Repository for the {@code INSTITUTION} lookup table.
 */
@CircuitBreaker(name = "institutionRepository")
public interface InstitutionRepository extends JpaRepository<InstitutionEntity, Integer>, JpaSpecificationExecutor<InstitutionEntity> {

	/** The institutions a search form can offer: every named one. Ordered by the service. */
	default List<InstitutionEntity> findAllSelectable() {
		return findAll(hasName());
	}
}
