package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.yearAtLeast;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.yearAtMost;

@CircuitBreaker(name = "combinedObjectRepository")
public interface CombinedObjectRepository extends JpaRepository<CombinedObjectEntity, String>, JpaSpecificationExecutor<CombinedObjectEntity> {

	String FILTER = """
		(:query IS NULL OR SEARCH_TEXT LIKE CONCAT('%', :query, '%'))
		AND (:location IS NULL
		     OR LOCATION_TEXT LIKE CONCAT('%', :location, '%')
		     OR TOPOGRAPHY_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%')))
		AND (:yearFrom IS NULL OR SORT_YEAR >= :yearFrom)
		AND (:yearTo IS NULL OR SORT_YEAR <= :yearTo)
		""";

	default Page<CombinedObjectEntity> findAllByParameters(final CombinedObjectParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(matches(parameters.getQuery()))
			.and(matchesLocation(parameters.getLocation()))
			.and(yearAtLeast(parameters.getYearFrom()))
			.and(yearAtMost(parameters.getYearTo())),
			pageable);
	}

	@Query(value = "SELECT OBJECT_TYPE AS objectType, COUNT(*) AS total FROM VW_MEMORY_OBJECTS WHERE " + FILTER
		+ " GROUP BY OBJECT_TYPE ORDER BY OBJECT_TYPE",
		nativeQuery = true)
	List<TypeCount> countByType(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location);

	interface TypeCount {
		String getObjectType();

		long getTotal();
	}
}
