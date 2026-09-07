package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.Gender;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.BIRTH_DATE;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.BIRTH_PARISH;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.FIRST_NAME;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.GENDER;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.LAST_NAME;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.PersonEntity_.PERSON_ID;

public interface PersonSpecification {

	SpecificationBuilder<PersonEntity> BUILDER = new SpecificationBuilder<>();

	/**
	 * {@code P_ID = 0} is the sentinel other tables point at to mean "no person linked". It is not a person, and it
	 * carries {@code OPTIONS = 6}, so it is flagged as published and cannot be excluded by {@link #published()}.
	 */
	Integer PLACEHOLDER_ID = 0;

	// A person is dated by the birth date alone, so the period it covers starts and ends on the same attribute.
	List<String> BIRTH_DATE_ATTRIBUTES = List.of(BIRTH_DATE);

	static Specification<PersonEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	/**
	 * Deletion sets {@code DELETEDDATE} but leaves the published bit set, so {@link #published()} alone does not hide
	 * the row — the same reason the object searches filter on it.
	 */
	static Specification<PersonEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<PersonEntity> notPlaceholder() {
		return BUILDER.buildNotEqualFilter(PERSON_ID, PLACEHOLDER_ID);
	}

	static Specification<PersonEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(PERSON_ID, id);
	}

	static Specification<PersonEntity> hasLastName(final String lastName) {
		return BUILDER.buildLikeAnyFilter(List.of(LAST_NAME), lastName);
	}

	static Specification<PersonEntity> hasFirstName(final String firstName) {
		return BUILDER.buildLikeAnyFilter(List.of(FIRST_NAME), firstName);
	}

	static Specification<PersonEntity> hasBirthParish(final String birthParish) {
		return BUILDER.buildLikeAnyFilter(List.of(BIRTH_PARISH), birthParish);
	}

	/**
	 * Accepts the canonical labels (Man, Kvinna, Okänt) case-insensitively and matches every stored spelling of that
	 * gender, as the census records do — the register writes the words, but the labels are what the API emits and what
	 * the combined search filters on, so one spelling works everywhere. Okänt also matches the rows whose stored value
	 * names no gender, stray, blank or missing alike. A label naming no gender matches nothing rather than every row.
	 */
	static Specification<PersonEntity> hasGender(final String gender) {
		return ofNullable(gender)
			.filter(not(String::isBlank))
			.map(label -> Gender.fromLabel(label)
				.map(PersonSpecification::genderFilter)
				.orElseGet(BUILDER::buildNoneFilter))
			.orElseGet(Specification::unrestricted);
	}

	/** Every gender is matched by the spellings it is stored as, except the unknown one, which is what is left over. */
	private static Specification<PersonEntity> genderFilter(final Gender gender) {
		return switch (gender) {
			case OKANT -> BUILDER.buildNotInIgnoreCaseFilter(GENDER, gender.getOtherSourceValues());
			case MAN, KVINNA -> BUILDER.buildInIgnoreCaseFilter(GENDER, gender.getSourceValues());
		};
	}

	static Specification<PersonEntity> bornFrom(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(BIRTH_DATE_ATTRIBUTES, yearFrom);
	}

	static Specification<PersonEntity> bornUntil(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(BIRTH_DATE_ATTRIBUTES, yearTo);
	}
}
