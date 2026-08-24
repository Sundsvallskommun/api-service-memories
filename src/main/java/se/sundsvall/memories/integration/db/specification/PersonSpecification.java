package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.PersonEntity;

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

	static Specification<PersonEntity> hasGender(final String gender) {
		return BUILDER.buildEqualIgnoreCaseFilter(GENDER, gender);
	}

	static Specification<PersonEntity> bornFrom(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(BIRTH_DATE_ATTRIBUTES, yearFrom);
	}

	static Specification<PersonEntity> bornUntil(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(BIRTH_DATE_ATTRIBUTES, yearTo);
	}
}
