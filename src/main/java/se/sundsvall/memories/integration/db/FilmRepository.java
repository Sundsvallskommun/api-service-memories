package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static se.sundsvall.memories.integration.db.specification.FilmSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.hasCreatorLegalEntity;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.hasCreatorPerson;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.matchesCreator;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.published;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.yearAtLeast;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.yearAtMost;

@CircuitBreaker(name = "filmRepository")
public interface FilmRepository extends JpaRepository<FilmEntity, Integer>, JpaSpecificationExecutor<FilmEntity> {

	default Page<FilmEntity> findAllByParameters(final FilmParameters parameters, final Pageable pageable) {
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

	// Unpublished films stay reachable by id — a planned administrative interface needs them.
	default Optional<FilmEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(fetchCreators())
			.and(hasId(id))
			.and(notDeleted()));
	}
}
