package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static se.sundsvall.memories.integration.db.model.CategoryEntity_.CATEGORY_ID;
import static se.sundsvall.memories.integration.db.model.CategoryEntity_.NAME;

public interface CategorySpecification {

	SpecificationBuilder<CategoryEntity> BUILDER = new SpecificationBuilder<>();

	/**
	 * {@code KAT_ID = 1} is the sentinel {@code JURPERS.KAT_ID} defaults to, meaning "no category" — the counterpart of
	 * {@code J_ID = 1} and {@code P_ID = 0}. It is a row in {@code KATEGORI} with a blank code and name, so it resolves as
	 * a foreign key, but it is not a category: it must not be offered in a dropdown, and a filter naming it must not
	 * select every uncategorised legal entity.
	 */
	Integer PLACEHOLDER_ID = 1;

	List<String> NAME_ATTRIBUTES = List.of(NAME);

	static Specification<CategoryEntity> notPlaceholder() {
		return BUILDER.buildNotEqualFilter(CATEGORY_ID, PLACEHOLDER_ID);
	}

	/** A category without a name cannot be shown, so it is not offered either. */
	static Specification<CategoryEntity> hasName() {
		return BUILDER.buildAnyNonBlankFilter(NAME_ATTRIBUTES);
	}
}
