package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.PublicationSpecification.published;

/**
 * Repository for the {@code PUBL} table. See
 * {@link se.sundsvall.memories.integration.db.specification.PublicationSpecification PublicationSpecification} for the
 * filters the methods below compose.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) is not backed
 * by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "publicationRepository")
public interface PublicationRepository extends JpaRepository<PublicationEntity, Integer>, JpaSpecificationExecutor<PublicationEntity> {

	/**
	 * Searches publications matching the given request parameters.
	 *
	 * @param  parameters the search parameters
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching publications
	 */
	default Page<PublicationEntity> findAllByParameters(final PublicationParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery())),
			pageable);
	}

	/**
	 * Loads a single publication by id under the same visibility rules a search applies, so that a soft-deleted
	 * publication cannot be reached by guessing its id.
	 *
	 * <p>
	 * Unpublished publications are deliberately still reachable — an administrative interface is planned that needs
	 * them.
	 *
	 * @param  id the publication id
	 * @return    the publication, or empty if it does not exist or is soft-deleted
	 */
	default Optional<PublicationEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(hasId(id))
			.and(notDeleted()));
	}
}
