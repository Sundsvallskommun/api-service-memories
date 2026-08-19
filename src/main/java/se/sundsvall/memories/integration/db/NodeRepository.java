package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.integration.db.model.NodeEntity;

import static se.sundsvall.memories.integration.db.specification.NodeSpecification.activeFrom;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.activeUntil;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.fetchNodeType;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.hasNodeType;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.published;

@CircuitBreaker(name = "nodeRepository")
public interface NodeRepository extends JpaRepository<NodeEntity, Integer>, JpaSpecificationExecutor<NodeEntity> {

	default Page<NodeEntity> findAllByParameters(final NodeParameters parameters, final Pageable pageable) {
		return findAll(fetchNodeType()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery()))
			.and(hasNodeType(parameters.getNodeTypeId()))
			.and(activeFrom(parameters.getYearFrom()))
			.and(activeUntil(parameters.getYearTo())),
			pageable);
	}
}
