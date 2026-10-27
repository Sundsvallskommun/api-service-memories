package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filters;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.orderedBy;

@CircuitBreaker(name = "combinedObjectRepository")
public interface CombinedObjectRepository extends JpaRepository<CombinedObjectEntity, String>, JpaSpecificationExecutor<CombinedObjectEntity>, CombinedObjectRepositoryCustom {

	/**
	 * The pageable must carry no sort: this search orders itself from its specification, and Spring Data would replace
	 * that order with the pageable's. The caller's sort keys are translated in
	 * {@link se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification}.
	 */
	default Page<CombinedObjectEntity> findAllByParameters(final CombinedObjectParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCreators())
			.and(filters(parameters))
			.and(orderedBy(parameters.getQuery(), parameters.sort())),
			pageable);
	}

	/**
	 * The legal entities that are the arkivbildare of at least one row, each id once. A full pass over the combined view,
	 * so {@link se.sundsvall.memories.service.ArchiveCreatorService} caches it.
	 */
	@Query("SELECT DISTINCT o.archiveCreatorLegalEntity.legalEntityId FROM CombinedObjectEntity o WHERE o.archiveCreatorLegalEntity IS NOT NULL")
	List<Integer> findArchiveCreatorLegalEntityIds();
}
