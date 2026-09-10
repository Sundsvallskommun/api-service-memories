package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static se.sundsvall.memories.integration.db.model.CategoryEntity_.NAME;
import static se.sundsvall.memories.integration.db.specification.CategorySpecification.hasName;
import static se.sundsvall.memories.integration.db.specification.CategorySpecification.notPlaceholder;

@CircuitBreaker(name = "categoryRepository")
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer>, JpaSpecificationExecutor<CategoryEntity> {

	/** The categories a search form can offer, by name: every named one except the sentinel. */
	default List<CategoryEntity> findAllSelectable() {
		return findAll(notPlaceholder().and(hasName()), Sort.by(NAME));
	}
}
