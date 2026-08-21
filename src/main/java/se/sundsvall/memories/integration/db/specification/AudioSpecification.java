package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;
import se.sundsvall.memories.integration.db.model.PersonEntity_;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.AudioEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DATE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.ID;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.SUBJECT;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.TOPOGRAPHY;

public interface AudioSpecification {

	SpecificationBuilder<AudioEntity> BUILDER = new SpecificationBuilder<>();

	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	// A recording is dated by a single DATUM, so the period it covers starts and ends on the same attribute.
	List<String> PERIOD_ATTRIBUTES = List.of(DATE);

	static Specification<AudioEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<AudioEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<AudioEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<AudioEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	static Specification<AudioEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	static Specification<AudioEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(PERIOD_ATTRIBUTES, yearFrom);
	}

	static Specification<AudioEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(PERIOD_ATTRIBUTES, yearTo);
	}

	/**
	 * The attributes an originator can be found by: a person's two name columns, a legal entity's name and its
	 * alternative names. Each association also names its sentinel row, which never counts as a match.
	 */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		// a person's name spans two columns and is matched as one string; a legal entity's two names are alternatives
		new SpecificationBuilder.AssociationAttributes(CREATOR_PERSON, List.of(List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME)), PersonEntity_.PERSON_ID,
			PersonSpecification.PLACEHOLDER_ID),
		new SpecificationBuilder.AssociationAttributes(CREATOR_LEGAL_ENTITY, List.of(List.of(LegalEntityEntity_.NAME), List.of(LegalEntityEntity_.ALTERNATIVE_NAMES)),
			LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntitySpecification.PLACEHOLDER_ID));

	static Specification<AudioEntity> matchesCreator(final String creator) {
		return BUILDER.buildAssociationLikeAnyFilter(CREATOR_ATTRIBUTES, creator);
	}

	static Specification<AudioEntity> hasCreatorPerson(final Integer creatorPersonId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_PERSON, PersonEntity_.PERSON_ID, creatorPersonId);
	}

	static Specification<AudioEntity> hasCreatorLegalEntity(final Integer creatorLegalEntityId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_LEGAL_ENTITY, LegalEntityEntity_.LEGAL_ENTITY_ID, creatorLegalEntityId);
	}

	/**
	 * Fetches both originator associations, which the mapper reads a name from on every row. Without this each row
	 * costs two more queries.
	 */
	static Specification<AudioEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<AudioEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	static Specification<AudioEntity> fetchSubject() {
		return BUILDER.buildFetchJoin(SUBJECT);
	}
}
