package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.model.TextEntity;

import static se.sundsvall.memories.integration.db.specification.TextSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.fetchSubject;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.hasCreatorLegalEntity;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.hasCreatorPerson;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.matchesCreator;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.published;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.yearAtLeast;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.yearAtMost;

@CircuitBreaker(name = "textRepository")
public interface TextRepository extends JpaRepository<TextEntity, Integer>, JpaSpecificationExecutor<TextEntity> {

	default Page<TextEntity> findAllByParameters(final TextParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCreators())
			.and(fetchSubject())
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery()))
			.and(matchesLocation(parameters.getLocation()))
			.and(yearAtLeast(parameters.getYearFrom()))
			.and(yearAtMost(parameters.getYearTo()))
			.and(matchesCreator(parameters.getCreator()))
			.and(hasCreatorPerson(parameters.getCreatorPersonId()))
			.and(hasCreatorLegalEntity(parameters.getCreatorLegalEntityId())),
			pageable);
	}

	// Unpublished documents stay reachable by id — a planned administrative interface needs them.
	default Optional<TextEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(fetchCreators())
			.and(fetchSubject())
			.and(hasId(id))
			.and(notDeleted()));
	}
}
