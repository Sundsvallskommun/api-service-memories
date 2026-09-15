package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;

import static se.sundsvall.memories.integration.db.model.InstitutionEntity_.NAME;

public interface InstitutionSpecification {

	SpecificationBuilder<InstitutionEntity> BUILDER = new SpecificationBuilder<>();

	/**
	 * An institution with no name is not offered: there is nothing to show it by, and a blank row is what a sentinel
	 * the legacy foreign keys default to looks like, so this keeps one out without the list having to know its id.
	 */
	static Specification<InstitutionEntity> hasName() {
		return BUILDER.buildAnyNonBlankFilter(List.of(NAME));
	}
}
