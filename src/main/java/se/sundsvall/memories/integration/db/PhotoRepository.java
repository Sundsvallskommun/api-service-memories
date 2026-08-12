package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

/**
 * Repository for the {@code FOTO} table.
 *
 * <p>
 * Searching is done with {@link JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification,
 * Pageable) findAll(Specification, Pageable)} — see
 * {@link se.sundsvall.memories.integration.db.specification.PhotoSpecifications PhotoSpecifications} for the available
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
