package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filters;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.orderedBy;

@CircuitBreaker(name = "combinedObjectRepository")
public interface CombinedObjectRepository extends JpaRepository<CombinedObjectEntity, String>, JpaSpecificationExecutor<CombinedObjectEntity>, CombinedObjectRepositoryCustom {

	/**
	 * Unlike every other search here, the pageable must carry no sort: this one orders itself from the specification,
	 * because relevance is computed per request and is not a column Spring Data could sort by. Spring Data replaces a
	 * specification's ordering the moment the pageable has one of its own, so the caller's sort keys are read from the
	 * parameters and translated in {@link se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification}
	 * instead. See {@link se.sundsvall.memories.service.CombinedObjectService}.
	 */
	default Page<CombinedObjectEntity> findAllByParameters(final CombinedObjectParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCreators())
			.and(filters(parameters))
			.and(orderedBy(parameters.getQuery(), parameters.sort())),
			pageable);
	}
}
