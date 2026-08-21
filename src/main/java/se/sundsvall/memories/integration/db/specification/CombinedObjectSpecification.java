package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;
import se.sundsvall.memories.integration.db.model.PersonEntity_;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.SEARCH_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.TOPOGRAPHY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.YEAR;

public interface CombinedObjectSpecification {

	SpecificationBuilder<CombinedObjectEntity> BUILDER = new SpecificationBuilder<>();

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	/**
	 * The combined endpoint trades the per-type relevance ranking for a plain substring match against the title and
	 * comment the view concatenates, which is what lets a single query sort and paginate across all five object types.
	 */
	static Specification<CombinedObjectEntity> matches(final String query) {
		return BUILDER.buildLikeAnyFilter(List.of(SEARCH_TEXT), query);
	}

	static Specification<CombinedObjectEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	/**
	 * The view has already derived the year and normalised an unreadable one to {@code NULL}, so these compare a real
	 * number and a row without a year falls outside every range.
	 */
	static Specification<CombinedObjectEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildAtLeastFilter(YEAR, yearFrom);
	}

	static Specification<CombinedObjectEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildAtMostFilter(YEAR, yearTo);
	}

	/**
	 * The attributes an originator can be found by, and the sentinel row each association may point at, which never
	 * counts as a match.
	 */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		// a person's name spans two columns and is matched as one string; a legal entity's two names are alternatives
		new SpecificationBuilder.AssociationAttributes(CREATOR_PERSON, List.of(List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME)), PersonEntity_.PERSON_ID,
			PersonSpecification.PLACEHOLDER_ID),
		new SpecificationBuilder.AssociationAttributes(CREATOR_LEGAL_ENTITY, List.of(List.of(LegalEntityEntity_.NAME), List.of(LegalEntityEntity_.ALTERNATIVE_NAMES)),
			LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntitySpecification.PLACEHOLDER_ID));

	/**
	 * The register branches of the view carry no originator, so filtering on one leaves only object types — which is
	 * the point: a person is not created by anyone.
	 */
	static Specification<CombinedObjectEntity> matchesCreator(final String creator) {
		return BUILDER.buildAssociationLikeAnyFilter(CREATOR_ATTRIBUTES, creator);
	}

	static Specification<CombinedObjectEntity> hasCreatorPerson(final Integer creatorPersonId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_PERSON, PersonEntity_.PERSON_ID, creatorPersonId);
	}

	static Specification<CombinedObjectEntity> hasCreatorLegalEntity(final Integer creatorLegalEntityId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_LEGAL_ENTITY, LegalEntityEntity_.LEGAL_ENTITY_ID, creatorLegalEntityId);
	}

	static Specification<CombinedObjectEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<CombinedObjectEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
