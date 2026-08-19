package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.hasCreatorLegalEntity;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.hasCreatorPerson;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.matchesCreator;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.published;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.yearAtLeast;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.yearAtMost;

@CircuitBreaker(name = "publicationRepository")
public interface PublicationRepository extends JpaRepository<PublicationEntity, Integer>, JpaSpecificationExecutor<PublicationEntity> {

	default Page<PublicationEntity> findAllByParameters(final PublicationParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCreators())
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

	// Unpublished publications stay reachable by id — a planned administrative interface needs them.
	default Optional<PublicationEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(fetchCreators())
			.and(hasId(id))
			.and(notDeleted()));
	}
}
