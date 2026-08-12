package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.model.TextEntity;

import static se.sundsvall.memories.integration.db.specification.TextSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.TextSpecification.published;

/**
 * Repository for the {@code TEXT} table. See
 * {@link se.sundsvall.memories.integration.db.specification.TextSpecification TextSpecification} for the filters the
 * methods below compose.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) and
 * {@code subject} (from OCM) are not backed by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "textRepository")
public interface TextRepository extends JpaRepository<TextEntity, Integer>, JpaSpecificationExecutor<TextEntity> {

	/**
	 * Searches text documents matching the given request parameters.
	 *
	 * @param  parameters the search parameters
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching documents
	 */
	default Page<TextEntity> findAllByParameters(final TextParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery())),
			pageable);
	}

	/**
	 * Loads a single document by id under the same visibility rules a search applies, so that a soft-deleted document
	 * cannot be reached by guessing its id.
	 *
	 * <p>
	 * Unpublished documents are deliberately still reachable — an administrative interface is planned that needs them.
	 *
	 * @param  id the text id
	 * @return    the document, or empty if it does not exist or is soft-deleted
	 */
	default Optional<TextEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(hasId(id))
			.and(notDeleted()));
	}
}
