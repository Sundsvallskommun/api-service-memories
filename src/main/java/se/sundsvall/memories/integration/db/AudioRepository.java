package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.integration.db.model.AudioEntity;

import static se.sundsvall.memories.integration.db.specification.AudioSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.fetchSubject;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.hasCreatorLegalEntity;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.hasCreatorPerson;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.matchesCreator;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.published;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.yearAtLeast;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.yearAtMost;

@CircuitBreaker(name = "audioRepository")
public interface AudioRepository extends JpaRepository<AudioEntity, Integer>, JpaSpecificationExecutor<AudioEntity> {

	default Page<AudioEntity> findAllByParameters(final AudioParameters parameters, final Pageable pageable) {
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

	// Unpublished recordings stay reachable by id — a planned administrative interface needs them.
	default Optional<AudioEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(fetchCreators())
			.and(fetchSubject())
			.and(hasId(id))
			.and(notDeleted()));
	}
}
