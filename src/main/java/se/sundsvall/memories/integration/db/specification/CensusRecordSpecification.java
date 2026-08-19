package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static se.sundsvall.memories.integration.db.model.CensusRecordEntity_.BIRTH_YEAR;
import static se.sundsvall.memories.integration.db.model.CensusRecordEntity_.FIRST_NAME;
import static se.sundsvall.memories.integration.db.model.CensusRecordEntity_.GENDER;
import static se.sundsvall.memories.integration.db.model.CensusRecordEntity_.LAST_NAME;

public interface CensusRecordSpecification {

	SpecificationBuilder<CensusRecordEntity> BUILDER = new SpecificationBuilder<>();

	// A census record is dated by the birth year alone, so the period it covers starts and ends on the same attribute.
	List<String> BIRTH_YEAR_ATTRIBUTES = List.of(BIRTH_YEAR);

	static Specification<CensusRecordEntity> hasLastName(final String lastName) {
		return BUILDER.buildLikeAnyFilter(List.of(LAST_NAME), lastName);
	}

	static Specification<CensusRecordEntity> hasFirstName(final String firstName) {
		return BUILDER.buildLikeAnyFilter(List.of(FIRST_NAME), firstName);
	}

	static Specification<CensusRecordEntity> hasGender(final String gender) {
		return BUILDER.buildEqualIgnoreCaseFilter(GENDER, gender);
	}

	static Specification<CensusRecordEntity> bornFrom(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(BIRTH_YEAR_ATTRIBUTES, yearFrom);
	}

	static Specification<CensusRecordEntity> bornUntil(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(BIRTH_YEAR_ATTRIBUTES, yearTo);
	}
}
