package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.SEARCH_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.TOPOGRAPHY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.YEAR;

public interface CombinedObjectSpecification {

	SpecificationBuilder<CombinedObjectEntity> BUILDER = new SpecificationBuilder<>();

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	/**
	 * The combined endpoint trades the per-type relevance ranking for a plain substring match against the title and
	 * comment the view concatenates, which is what lets a single query sort and paginate across all five object types.
	 */
	static Specification<CombinedObjectEntity> matches(final String query) {
		return BUILDER.buildLikeAnyFilter(List.of(SEARCH_TEXT), query);
	}

	static Specification<CombinedObjectEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	/**
	 * The view has already derived the year and normalised an unreadable one to {@code NULL}, so these compare a real
	 * number and a row without a year falls outside every range.
	 */
	static Specification<CombinedObjectEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildAtLeastFilter(YEAR, yearFrom);
	}

	static Specification<CombinedObjectEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildAtMostFilter(YEAR, yearTo);
	}

	static Specification<CombinedObjectEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
