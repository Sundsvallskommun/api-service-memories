package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

import static se.sundsvall.memories.integration.db.specification.PhotoSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.PhotoSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.PhotoSpecification.hasObjectType;
import static se.sundsvall.memories.integration.db.specification.PhotoSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.PhotoSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.PhotoSpecification.published;

/**
 * Repository for the {@code FOTO} table.
 *
 * <p>
 * Searching is done with {@link JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification,
 * Pageable) findAll(Specification, Pageable)} — see
 * {@link se.sundsvall.memories.integration.db.specification.PhotoSpecification PhotoSpecification} for the available
 * filters.
 *
 * <p>
 * <strong>Sorting:</strong> a sort property supplied via {@link Pageable} is an entity property (e.g.
 * {@code documentTitle}), not a physical DB column name. The resolved {@code location} (from TOPOGRAFI) is not backed
 * by a column on this entity and cannot be sorted on.
 */
@CircuitBreaker(name = "photoRepository")
public interface PhotoRepository extends JpaRepository<PhotoEntity, Integer>, JpaSpecificationExecutor<PhotoEntity> {

	/**
	 * Searches photos matching the given request parameters.
	 *
	 * @param  parameters the search parameters
	 * @param  pageable   the pagination and sorting criteria
	 * @return            a page of matching photos
	 */
	default Page<PhotoEntity> findAllByParameters(final PhotoParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery()))
			.and(hasObjectType(parameters.getObjectType())),
			pageable);
	}

	/**
	 * Loads a single photo by id under the same visibility rules a search applies, so that a soft-deleted photo cannot
	 * be reached by guessing its id.
	 *
	 * <p>
	 * Unpublished photos are deliberately still reachable — an administrative interface is planned that needs them.
	 *
	 * @param  id the photo id
	 * @return    the photo, or empty if it does not exist or is soft-deleted
	 */
	default Optional<PhotoEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(hasId(id))
			.and(notDeleted()));
	}

	/**
	 * Returns the IDs of all photos connected to the given photo via the {@code FOTO_FOTO} junction table. The relation
	 * is bidirectional — a row with {@code F_ID1 = id} returns {@code F_ID2}, a row with {@code F_ID2 = id} returns
	 * {@code F_ID1}.
	 *
	 * @param  id the photo id to find relations for
	 * @return    the related photo ids (empty list if no relations exist)
	 */
	@Query(value = "SELECT CASE WHEN F_ID1 = :id THEN F_ID2 ELSE F_ID1 END AS related_id FROM FOTO_FOTO WHERE F_ID1 = :id OR F_ID2 = :id ORDER BY related_id",
		nativeQuery = true)
	List<Integer> findRelatedPhotoIds(@Param("id") Integer id);
}
