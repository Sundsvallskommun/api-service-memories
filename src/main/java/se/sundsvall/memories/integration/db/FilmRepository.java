package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static se.sundsvall.memories.integration.db.specification.FilmSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.published;

/**
 * Repository for the {@code FILM} table. See
 * {@link se.sundsvall.memories.integration.db.specification.FilmSpecification FilmSpecification} for the filters the
 * methods below compose.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) is not backed
 * by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "filmRepository")
public interface FilmRepository extends JpaRepository<FilmEntity, Integer>, JpaSpecificationExecutor<FilmEntity> {

	/**
	 * Searches films matching the given request parameters.
	 *
	 * @param  parameters the search parameters
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching films
	 */
	default Page<FilmEntity> findAllByParameters(final FilmParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery())),
			pageable);
	}

	/**
	 * Loads a single film by id under the same visibility rules a search applies, so that a soft-deleted film cannot be
	 * reached by guessing its id.
	 *
	 * <p>
	 * Unpublished films are deliberately still reachable — an administrative interface is planned that needs them.
	 *
	 * @param  id the film id
	 * @return    the film, or empty if it does not exist or is soft-deleted
	 */
	default Optional<FilmEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(hasId(id))
			.and(notDeleted()));
	}
}
