package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static se.sundsvall.memories.integration.db.specification.TopographySpecification.hasDisplayName;
import static se.sundsvall.memories.integration.db.specification.TopographySpecification.orderedByDisplayName;

/**
 * Repository for the {@code TOPOGRAFI} place lookup table.
 */
@CircuitBreaker(name = "topographyRepository")
public interface TopographyRepository extends JpaRepository<TopographyEntity, Integer>, JpaSpecificationExecutor<TopographyEntity> {

	/** The places a search form can offer, by display name: every row that has one. */
	default List<TopographyEntity> findAllSelectable() {
		return findAll(hasDisplayName().and(orderedByDisplayName()));
	}
}
