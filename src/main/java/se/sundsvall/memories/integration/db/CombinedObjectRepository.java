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

import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchCreators;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.hasCreatorLegalEntity;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.hasCreatorPerson;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.matches;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.matchesCreator;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.yearAtLeast;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.yearAtMost;

@CircuitBreaker(name = "combinedObjectRepository")
public interface CombinedObjectRepository extends JpaRepository<CombinedObjectEntity, String>, JpaSpecificationExecutor<CombinedObjectEntity> {

	/**
	 * The counters are a handwritten query, so this repeats what the specifications express for the search itself. The
	 * originator predicates exclude the sentinel rows (person 0, legal entity 1) the same way the specifications do —
	 * a sentinel is named "Ingen", so a search for that word would otherwise count every object.
	 */
	String FILTER = """
		(:query IS NULL OR SEARCH_TEXT LIKE CONCAT('%', :query, '%'))
		AND (:location IS NULL
		     OR LOCATION_TEXT LIKE CONCAT('%', :location, '%')
		     OR TOPOGRAPHY_ID IN (SELECT T_ID FROM TOPOGRAFI WHERE TOPNAMN LIKE CONCAT('%', :location, '%') OR PLATS LIKE CONCAT('%', :location, '%')))
		AND (:yearFrom IS NULL OR SORT_YEAR >= :yearFrom)
		AND (:yearTo IS NULL OR SORT_YEAR <= :yearTo)
		AND (:creator IS NULL
		     OR CREATOR_PERSON_ID IN (SELECT P_ID FROM PERSON WHERE P_ID <> 0 AND CONCAT_WS(' ', FNAMN, ENAMN) LIKE CONCAT('%', :creator, '%'))
		     OR CREATOR_LEGAL_ENTITY_ID IN (SELECT J_ID FROM JURPERS WHERE J_ID <> 1 AND (JURPERS LIKE CONCAT('%', :creator, '%') OR ALTNAMN LIKE CONCAT('%', :creator, '%'))))
		AND (:creatorPersonId IS NULL OR CREATOR_PERSON_ID = :creatorPersonId)
		AND (:creatorLegalEntityId IS NULL OR CREATOR_LEGAL_ENTITY_ID = :creatorLegalEntityId)
		""";

	default Page<CombinedObjectEntity> findAllByParameters(final CombinedObjectParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCreators())
			.and(matches(parameters.getQuery()))
			.and(matchesLocation(parameters.getLocation()))
			.and(yearAtLeast(parameters.getYearFrom()))
			.and(yearAtMost(parameters.getYearTo()))
			.and(matchesCreator(parameters.getCreator()))
			.and(hasCreatorPerson(parameters.getCreatorPersonId()))
			.and(hasCreatorLegalEntity(parameters.getCreatorLegalEntityId())),
			pageable);
	}

	@Query(value = "SELECT OBJECT_TYPE AS objectType, COUNT(*) AS total FROM VW_MEMORY_OBJECTS WHERE " + FILTER
		+ " GROUP BY OBJECT_TYPE ORDER BY OBJECT_TYPE",
		nativeQuery = true)
	List<TypeCount> countByType(
		@Param("query") String query,
		@Param("yearFrom") Integer yearFrom,
		@Param("yearTo") Integer yearTo,
		@Param("location") String location,
		@Param("creator") String creator,
		@Param("creatorPersonId") Integer creatorPersonId,
		@Param("creatorLegalEntityId") Integer creatorLegalEntityId);

	interface TypeCount {
		String getObjectType();

		long getTotal();
	}
}
