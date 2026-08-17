package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.SeamanEntity;

import static se.sundsvall.memories.integration.db.model.SeamanEntity_.BIRTH_DATE;
import static se.sundsvall.memories.integration.db.model.SeamanEntity_.BIRTH_PARISH;
import static se.sundsvall.memories.integration.db.model.SeamanEntity_.FIRST_NAME;
import static se.sundsvall.memories.integration.db.model.SeamanEntity_.LAST_NAME1;
import static se.sundsvall.memories.integration.db.model.SeamanEntity_.LAST_NAME2;

public interface SeamanSpecification {

	SpecificationBuilder<SeamanEntity> BUILDER = new SpecificationBuilder<>();

	// SJOMAN carries two surnames per seaman, and the register is inconsistent about which one is used, so a search
	// has to match either.
	List<String> LAST_NAME_ATTRIBUTES = List.of(LAST_NAME1, LAST_NAME2);

	// A seaman is dated by the birth date alone, so the period it covers starts and ends on the same attribute.
	List<String> BIRTH_DATE_ATTRIBUTES = List.of(BIRTH_DATE);

	static Specification<SeamanEntity> hasLastName(final String lastName) {
		return BUILDER.buildLikeAnyFilter(LAST_NAME_ATTRIBUTES, lastName);
	}

	static Specification<SeamanEntity> hasFirstName(final String firstName) {
		return BUILDER.buildLikeAnyFilter(List.of(FIRST_NAME), firstName);
	}

	static Specification<SeamanEntity> hasBirthParish(final String birthParish) {
		return BUILDER.buildLikeAnyFilter(List.of(BIRTH_PARISH), birthParish);
	}

	static Specification<SeamanEntity> bornFrom(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(BIRTH_DATE_ATTRIBUTES, yearFrom);
	}

	static Specification<SeamanEntity> bornUntil(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(BIRTH_DATE_ATTRIBUTES, yearTo);
	}
}
