package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.activeFrom;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.activeUntil;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.fetchCategory;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.hasCategory;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.hasName;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.notPlaceholder;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.published;

/**
 * Repository for the {@code JURPERS} legal-entity table.
 */
@CircuitBreaker(name = "legalEntityRepository")
public interface LegalEntityRepository extends JpaRepository<LegalEntityEntity, Integer>, JpaSpecificationExecutor<LegalEntityEntity> {

	default Page<LegalEntityEntity> findAllByParameters(final LegalEntityParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCategory())
			.and(published())
			.and(notDeleted())
			.and(notPlaceholder())
			.and(hasName(parameters.getName()))
			.and(matchesLocation(parameters.getLocation()))
			.and(hasCategory(parameters.getCategoryId()))
			.and(activeFrom(parameters.getYearFrom()))
			.and(activeUntil(parameters.getYearTo())),
			pageable);
	}

	// Unpublished legal entity records stay reachable by id, the way the object searches keep unpublished objects;
	// a deleted one does not, and is not named as an originator either.
	default Optional<LegalEntityEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(fetchCategory())
			.and(hasId(id))
			.and(notDeleted())
			.and(notPlaceholder()));
	}
}
