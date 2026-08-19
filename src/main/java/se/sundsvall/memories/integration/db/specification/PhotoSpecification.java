package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;
import se.sundsvall.memories.integration.db.model.PersonEntity_;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.EARLIEST;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.ID;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.LATEST;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.TOPOGRAPHY;

public interface PhotoSpecification {

	SpecificationBuilder<PhotoEntity> BUILDER = new SpecificationBuilder<>();

	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	// A photo covers a period: it starts at TIDIG and ends at SENAST, which falls back to TIDIG when the period is a
	// single point in time.
	List<String> PERIOD_END_ATTRIBUTES = List.of(LATEST, EARLIEST);

	List<String> PERIOD_START_ATTRIBUTES = List.of(EARLIEST);

	static Specification<PhotoEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<PhotoEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<PhotoEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	// A blank object type means "no filter", so the request parameter can be passed through untrimmed.
	static Specification<PhotoEntity> hasObjectType(final String objectType) {
		return BUILDER.buildEqualFilter(OBJECT_TYPE, trimToNull(objectType));
	}

	static Specification<PhotoEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	static Specification<PhotoEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	// A photo whose period ends before the requested range falls outside it.
	static Specification<PhotoEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(PERIOD_END_ATTRIBUTES, yearFrom);
	}

	// A photo whose period starts after the requested range falls outside it.
	static Specification<PhotoEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(PERIOD_START_ATTRIBUTES, yearTo);
	}

	/**
	 * Fetches both originator associations, which the mapper reads a name from on every row. Without this each row
	 * costs two more queries.
	 */
	/**
	 * The attributes an originator can be found by: a person's two name columns, a legal entity's name and its
	 * alternative names. Each association also names its sentinel row, which never counts as a match.
	 */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		new SpecificationBuilder.AssociationAttributes(CREATOR_PERSON, List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME), PersonEntity_.PERSON_ID, PersonSpecification.PLACEHOLDER_ID),
		new SpecificationBuilder.AssociationAttributes(CREATOR_LEGAL_ENTITY, List.of(LegalEntityEntity_.NAME, LegalEntityEntity_.ALTERNATIVE_NAMES), LegalEntityEntity_.LEGAL_ENTITY_ID,
			LegalEntitySpecification.PLACEHOLDER_ID));

	static Specification<PhotoEntity> matchesCreator(final String creator) {
		return BUILDER.buildAssociationLikeAnyFilter(CREATOR_ATTRIBUTES, creator);
	}

	static Specification<PhotoEntity> hasCreatorPerson(final Integer creatorPersonId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_PERSON, PersonEntity_.PERSON_ID, creatorPersonId);
	}

	static Specification<PhotoEntity> hasCreatorLegalEntity(final Integer creatorLegalEntityId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_LEGAL_ENTITY, LegalEntityEntity_.LEGAL_ENTITY_ID, creatorLegalEntityId);
	}

	static Specification<PhotoEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<PhotoEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	private static String trimToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(trimmed -> !trimmed.isEmpty())
			.orElse(null);
	}
}
