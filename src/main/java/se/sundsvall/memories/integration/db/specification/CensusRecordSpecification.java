package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;
import se.sundsvall.memories.integration.db.model.Gender;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
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

	/**
	 * Accepts the canonical labels (Man, Kvinna, Okänt) case-insensitively and matches every stored spelling of that
	 * gender, words and codes alike. Okänt also matches the rows whose stored value names no gender — stray, blank or
	 * missing — since the register knows no more about those than about the ones spelling it out, and the API reports
	 * them all as unknown. A label naming no gender matches nothing rather than every row.
	 */
	static Specification<CensusRecordEntity> hasGender(final String gender) {
		return ofNullable(gender)
			.filter(not(String::isBlank))
			.map(label -> Gender.fromLabel(label)
				.map(CensusRecordSpecification::genderFilter)
				.orElseGet(BUILDER::buildNoneFilter))
			.orElseGet(Specification::unrestricted);
	}

	/** Every gender is matched by the spellings it is stored as, except the unknown one, which is what is left over. */
	private static Specification<CensusRecordEntity> genderFilter(final Gender gender) {
		return switch (gender) {
			case OKANT -> BUILDER.buildNotInIgnoreCaseFilter(GENDER, gender.getOtherSourceValues());
			case MAN, KVINNA -> BUILDER.buildInIgnoreCaseFilter(GENDER, gender.getSourceValues());
		};
	}

	static Specification<CensusRecordEntity> bornFrom(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(BIRTH_YEAR_ATTRIBUTES, yearFrom);
	}

	static Specification<CensusRecordEntity> bornUntil(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(BIRTH_YEAR_ATTRIBUTES, yearTo);
	}
}
