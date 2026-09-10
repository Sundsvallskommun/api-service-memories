package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static se.sundsvall.memories.integration.db.model.TopographyEntity_.CODE;
import static se.sundsvall.memories.integration.db.model.TopographyEntity_.ID;
import static se.sundsvall.memories.integration.db.model.TopographyEntity_.NAME;
import static se.sundsvall.memories.integration.db.model.TopographyEntity_.PLACE;

public interface TopographySpecification {

	SpecificationBuilder<TopographyEntity> BUILDER = new SpecificationBuilder<>();

	/** The columns the display name falls back through, in {@link TopographyEntity#getDisplayName()}'s order. */
	List<String> DISPLAY_ATTRIBUTES = List.of(NAME, PLACE, CODE);

	/**
	 * A place with nothing to show it by is not offered. This is also what keeps the sentinel the object tables'
	 * {@code *_T_ID} default to out of the list, since it is blank in every column, without the list having to know
	 * its id.
	 */
	static Specification<TopographyEntity> hasDisplayName() {
		return BUILDER.buildAnyNonBlankFilter(DISPLAY_ATTRIBUTES);
	}

	/** Ordered by the name a place is shown under, with the id as tiebreak so equal names keep a stable order. */
	static Specification<TopographyEntity> orderedByDisplayName() {
		return BUILDER.buildOrderBy((root, cb) -> List.of(cb.asc(BUILDER.firstNonBlank(root, cb, DISPLAY_ATTRIBUTES)), cb.asc(root.get(ID))));
	}
}
