package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static se.sundsvall.memories.integration.db.model.TopographyEntity_.NAME;
import static se.sundsvall.memories.integration.db.model.TopographyEntity_.PLACE;

public interface TopographySpecification {

	SpecificationBuilder<TopographyEntity> BUILDER = new SpecificationBuilder<>();

	/** The columns {@link TopographyEntity#getDisplayName()} builds a label out of. The code is not one of them. */
	List<String> DISPLAY_ATTRIBUTES = List.of(NAME, PLACE);

	/**
	 * A place with nothing to show it by is not offered — neither column holds a name, so there is no label to build.
	 * This is also what keeps the sentinel the object tables' {@code *_T_ID} default to out of the list, since it is
	 * blank in every column, without the list having to know its id.
	 */
	static Specification<TopographyEntity> hasDisplayName() {
		return BUILDER.buildAnyNonBlankFilter(DISPLAY_ATTRIBUTES);
	}
}
