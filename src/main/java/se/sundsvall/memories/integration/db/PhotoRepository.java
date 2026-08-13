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

@CircuitBreaker(name = "photoRepository")
public interface PhotoRepository extends JpaRepository<PhotoEntity, Integer>, JpaSpecificationExecutor<PhotoEntity> {

	default Page<PhotoEntity> findAllByParameters(final PhotoParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(notDeleted())
			.and(published())
			.and(matches(parameters.getQuery()))
			.and(hasObjectType(parameters.getObjectType())),
			pageable);
	}

	// Unpublished photos stay reachable by id — a planned administrative interface needs them.
	default Optional<PhotoEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(hasId(id))
			.and(notDeleted()));
	}

	/**
	 * Returns the IDs of all photos connected to the given photo via the {@code FOTO_FOTO} junction table. The relation
	 * is bidirectional — a row with {@code F_ID1 = id} returns {@code F_ID2}, a row with {@code F_ID2 = id} returns
	 * {@code F_ID1}, which is what the CASE expression flattens.
	 */
	@Query(value = "SELECT CASE WHEN F_ID1 = :id THEN F_ID2 ELSE F_ID1 END AS related_id FROM FOTO_FOTO WHERE F_ID1 = :id OR F_ID2 = :id ORDER BY related_id",
		nativeQuery = true)
	List<Integer> findRelatedPhotoIds(@Param("id") Integer id);
}
