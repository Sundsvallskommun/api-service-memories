package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CategoryEntity_;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.ALTERNATIVE_NAMES;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.CATEGORY;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.END_DATE;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.LEGAL_ENTITY_ID;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.NAME;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.START_DATE;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.TOPOGRAPHY;

public interface LegalEntitySpecification {

	SpecificationBuilder<LegalEntityEntity> BUILDER = new SpecificationBuilder<>();

	/**
	 * {@code J_ID = 1} is the sentinel other tables point at to mean "no legal entity". It is not a legal entity, and it
	 * is flagged as published, so {@link #published()} does not hide it.
	 */
	Integer PLACEHOLDER_ID = 1;

	// A legal entity is known under its registered name and under any number of alternative ones, and a search has to
	// match either.
	List<String> NAME_ATTRIBUTES = List.of(NAME, ALTERNATIVE_NAMES);

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	// STARTDATUM and SLUTDATUM bound the period the entity was active. A missing bound means the period is open in that
	// direction, which is why these use the open-ended year filters.
	List<String> PERIOD_END_ATTRIBUTES = List.of(END_DATE);

	List<String> PERIOD_START_ATTRIBUTES = List.of(START_DATE);

	static Specification<LegalEntityEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	/**
	 * Deletion sets {@code DELETEDDATE} but leaves the published bit set, so {@link #published()} alone does not hide
	 * the row — the same reason the object searches filter on it.
	 */
	static Specification<LegalEntityEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<LegalEntityEntity> notPlaceholder() {
		return BUILDER.buildNotEqualFilter(LEGAL_ENTITY_ID, PLACEHOLDER_ID);
	}

	static Specification<LegalEntityEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(LEGAL_ENTITY_ID, id);
	}

	static Specification<LegalEntityEntity> hasName(final String name) {
		return BUILDER.buildLikeAnyFilter(NAME_ATTRIBUTES, name);
	}

	static Specification<LegalEntityEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	static Specification<LegalEntityEntity> hasCategory(final Integer categoryId) {
		return BUILDER.buildAssociationEqualFilter(CATEGORY, CategoryEntity_.CATEGORY_ID, categoryId);
	}

	// An entity whose period ended before the requested range falls outside it. An entity that has not ended is still
	// active, so a missing end date matches.
	static Specification<LegalEntityEntity> activeFrom(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastOrOpenFilter(PERIOD_END_ATTRIBUTES, yearFrom);
	}

	// An entity whose period started after the requested range falls outside it. A missing start date says nothing
	// about when it began, so it matches.
	static Specification<LegalEntityEntity> activeUntil(final Integer yearTo) {
		return BUILDER.buildYearAtMostOrOpenFilter(PERIOD_START_ATTRIBUTES, yearTo);
	}

	static Specification<LegalEntityEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	static Specification<LegalEntityEntity> fetchCategory() {
		return BUILDER.buildFetchJoin(CATEGORY);
	}
}
