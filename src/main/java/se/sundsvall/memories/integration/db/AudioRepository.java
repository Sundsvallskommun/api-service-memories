package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.integration.db.model.AudioEntity;

import static se.sundsvall.memories.integration.db.specification.AudioSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.AudioSpecification.published;

/**
 * Repository for the {@code LJUD} table. See
 * {@link se.sundsvall.memories.integration.db.specification.AudioSpecification AudioSpecification} for the filters the
 * methods below compose.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) and
 * {@code subject} (from OCM) are not backed by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "audioRepository")
public interface AudioRepository extends JpaRepository<AudioEntity, Integer>, JpaSpecificationExecutor<AudioEntity> {

	/**
	 * Searches audio recordings matching the given request parameters.
	 *
	 * @param  parameters the search parameters
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching recordings
	 */
	default Page<AudioEntity> findAllByParameters(final AudioParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery())),
			pageable);
	}

	/**
	 * Loads a single recording by id under the same visibility rules a search applies, so that a soft-deleted recording
	 * cannot be reached by guessing its id.
	 *
	 * <p>
	 * Unpublished recordings are deliberately still reachable — an administrative interface is planned that needs them.
	 *
	 * @param  id the audio id
	 * @return    the recording, or empty if it does not exist or is soft-deleted
	 */
	default Optional<AudioEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(hasId(id))
			.and(notDeleted()));
	}
}
