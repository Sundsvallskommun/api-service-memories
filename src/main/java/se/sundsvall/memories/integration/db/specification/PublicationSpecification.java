package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;
import se.sundsvall.memories.integration.db.model.PersonEntity_;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.PublicationEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DATE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.ID;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.TOPOGRAPHY;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.XMLTEXT;

public interface PublicationSpecification {

	SpecificationBuilder<PublicationEntity> BUILDER = new SpecificationBuilder<>();

	// XMLTEXT is searched here but not on TEXT, where the column is empty. PUBL holds roughly 68 MB of digitised text
	// across 20 326 rows, and a LIKE over a longtext column cannot use an index — worth measuring once this is live.
	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT, XMLTEXT);

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	// A publication is dated by a single DATUM, so the period it covers starts and ends on the same attribute.
	List<String> PERIOD_ATTRIBUTES = List.of(DATE);

	static Specification<PublicationEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<PublicationEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<PublicationEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<PublicationEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	static Specification<PublicationEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	static Specification<PublicationEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(PERIOD_ATTRIBUTES, yearFrom);
	}

	static Specification<PublicationEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(PERIOD_ATTRIBUTES, yearTo);
	}

	// Only P_T_ID is modelled — FORLAG_T_ID is mapped but read nowhere.
	/**
	 * The attributes an originator can be found by: a person's two name columns, a legal entity's name and its
	 * alternative names. Each association also names its sentinel row, which never counts as a match.
	 */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		// a person's name spans two columns and is matched as one string; a legal entity's two names are alternatives
		new SpecificationBuilder.AssociationAttributes(CREATOR_PERSON, List.of(List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME)), PersonEntity_.PERSON_ID,
			PersonSpecification.PLACEHOLDER_ID, PersonEntity_.DELETED_DATE),
		new SpecificationBuilder.AssociationAttributes(CREATOR_LEGAL_ENTITY, List.of(List.of(LegalEntityEntity_.NAME), List.of(LegalEntityEntity_.ALTERNATIVE_NAMES)),
			LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntitySpecification.PLACEHOLDER_ID, LegalEntityEntity_.DELETED_DATE));

	static Specification<PublicationEntity> matchesCreator(final String creator) {
		return BUILDER.buildAssociationLikeAnyFilter(CREATOR_ATTRIBUTES, creator);
	}

	static Specification<PublicationEntity> hasCreatorPerson(final Integer creatorPersonId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_PERSON, PersonEntity_.PERSON_ID, PersonEntity_.DELETED_DATE, creatorPersonId);
	}

	static Specification<PublicationEntity> hasCreatorLegalEntity(final Integer creatorLegalEntityId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_LEGAL_ENTITY, LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntityEntity_.DELETED_DATE, creatorLegalEntityId);
	}

	/**
	 * Fetches both originator associations, which the mapper reads a name from on every row. Without this each row
	 * costs two more queries.
	 */
	static Specification<PublicationEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<PublicationEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
