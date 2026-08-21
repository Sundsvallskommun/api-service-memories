package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.integration.db.model.NodeEntity;

import static se.sundsvall.memories.integration.db.specification.NodeSpecification.activeFrom;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.activeUntil;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.fetchNodeType;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.hasNodeType;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.hasParent;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.NodeSpecification.published;

@CircuitBreaker(name = "nodeRepository")
public interface NodeRepository extends JpaRepository<NodeEntity, Integer>, JpaSpecificationExecutor<NodeEntity> {

	default Page<NodeEntity> findAllByParameters(final NodeParameters parameters, final Pageable pageable) {
		return findAll(byParameters(parameters), pageable);
	}

	/**
	 * The children of one node, filtered and paged like any other search.
	 */
	default Page<NodeEntity> findChildrenByParameters(final Integer parentId, final NodeParameters parameters, final Pageable pageable) {
		return findAll(byParameters(parameters)
			.and(hasParent(parentId)), pageable);
	}

	/**
	 * A node by id, with its type fetched. Unlike the search this keeps unpublished nodes, the same decision the person
	 * and legal entity lookups document: an administrative interface has to be able to reach them by id. A deleted node
	 * is gone for good, though, so it is hidden here as well.
	 */
	default Optional<NodeEntity> findNodeById(final Integer id) {
		return findOne(fetchNodeType()
			.and(notDeleted())
			.and(hasId(id)));
	}

	/**
	 * Whether a node exists and is not deleted, for the callers that only need to tell a missing node from an empty
	 * result.
	 */
	default boolean existsNodeById(final Integer id) {
		return exists(hasId(id)
			.and(notDeleted()));
	}

	private static Specification<NodeEntity> byParameters(final NodeParameters parameters) {
		return fetchNodeType()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery()))
			.and(hasNodeType(parameters.getNodeTypeId()))
			.and(activeFrom(parameters.getYearFrom()))
			.and(activeUntil(parameters.getYearTo()));
	}
}
