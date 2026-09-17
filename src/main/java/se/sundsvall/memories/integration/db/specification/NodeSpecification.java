package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.CategoryEntity_;
import se.sundsvall.memories.integration.db.model.InstitutionEntity_;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;
import se.sundsvall.memories.integration.db.model.NodeAttributesEntity_;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity_;
import se.sundsvall.memories.integration.db.model.PersonEntity_;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.ATTRIBUTES;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.DESCRIPTION;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.ID;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.NAME;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.NODE_TYPE;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.PARENT_ID;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.START_YEAR;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.STOP_YEAR;

public interface NodeSpecification {

	SpecificationBuilder<NodeEntity> BUILDER = new SpecificationBuilder<>();

	/** The topography columns a place is matched on, the same two the object searches read. */
	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	/** The lookups a node's attribute row names, fetched with it so a page of nodes is one query. */
	List<String> ATTRIBUTE_LOOKUPS = List.of(NodeAttributesEntity_.LEGAL_ENTITY, NodeAttributesEntity_.PERSON, NodeAttributesEntity_.INSTITUTION,
		NodeAttributesEntity_.CATEGORY, NodeAttributesEntity_.TOPOGRAPHY, NodeAttributesEntity_.SUBJECT);

	/**
	 * The arkivbildare associations, each with the sentinel id that never counts as a match. {@code FIELD1} and
	 * {@code FIELD2} carry no default, but the sentinels are rows like any other and could be pointed at by hand.
	 */
	SpecificationBuilder.GuardedAssociation LEGAL_ENTITY_GUARD = new SpecificationBuilder.GuardedAssociation(NodeAttributesEntity_.LEGAL_ENTITY,
		LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntitySpecification.PLACEHOLDER_ID, LegalEntityEntity_.DELETED_DATE);

	SpecificationBuilder.GuardedAssociation PERSON_GUARD = new SpecificationBuilder.GuardedAssociation(NodeAttributesEntity_.PERSON, PersonEntity_.PERSON_ID,
		PersonSpecification.PLACEHOLDER_ID, PersonEntity_.DELETED_DATE);

	/** The attributes an arkivbildare is matched on: the same ones the object searches match an originator on. */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		new SpecificationBuilder.AssociationAttributes(PERSON_GUARD, List.of(List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME))),
		new SpecificationBuilder.AssociationAttributes(LEGAL_ENTITY_GUARD, List.of(List.of(LegalEntityEntity_.NAME), List.of(LegalEntityEntity_.ALTERNATIVE_NAMES))));

	/**
	 * The free text has to reach the description as well as the name: a series is often findable only through what its
	 * description says it contains. It reaches the arkivbildare too, since an archive named after its arkivbildare has
	 * an empty name of its own — the archive's own view falls back to the legal entity or the person, and so does the
	 * name the API reports, so a search for that name has to find the node.
	 */
	/** The index {@code MATCH} needs for {@link #matches}; without it the search falls back to {@code LIKE}. */
	String FULLTEXT_TABLE = "TBL_NODES";

	List<String> FULLTEXT_COLUMNS = List.of("NAME", "DESCRIPTION");

	static Specification<NodeEntity> matches(final String query) {
		return BUILDER.buildFullTextOrAssociationFilter(List.of(NAME, DESCRIPTION), FULLTEXT_TABLE, FULLTEXT_COLUMNS, ATTRIBUTES, CREATOR_ATTRIBUTES, query);
	}

	static Specification<NodeEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<NodeEntity> hasParent(final Integer parentId) {
		return BUILDER.buildEqualFilter(PARENT_ID, parentId);
	}

	static Specification<NodeEntity> hasNodeType(final Integer nodeTypeId) {
		return BUILDER.buildAssociationEqualFilter(NODE_TYPE, NodeTypeEntity_.ID, nodeTypeId);
	}

	/**
	 * The node type by its name, so a client can ask for the archives without knowing the id the archive gave that
	 * type. The archives are not the tree's roots: the real tree hangs every archive under a synthetic root node, so the
	 * level a list of arkiv och samlingar shows is the type, not the depth.
	 */
	static Specification<NodeEntity> hasNodeTypeName(final String nodeType) {
		return BUILDER.buildAssociationEqualIgnoreCaseFilter(NODE_TYPE, NodeTypeEntity_.NAME, nodeType);
	}

	/** Restricts to the nodes held by one of the given institutions, which are alternatives. */
	static Specification<NodeEntity> hasInstitution(final List<Integer> institutionIds) {
		return BUILDER.buildNestedLookupInFilter(ATTRIBUTES, NodeAttributesEntity_.INSTITUTION, InstitutionEntity_.ID, institutionIds);
	}

	/**
	 * Restricts to the nodes whose arkivbildare is in one of the given categories, which are alternatives. The sentinel
	 * category is never a match — naming it alone matches nothing rather than every node whose category was never set,
	 * the same reading the object search gives it.
	 */
	static Specification<NodeEntity> hasCategory(final List<Integer> categoryIds) {
		final var named = ofNullable(categoryIds).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.toList();
		final var real = named.stream()
			.filter(id -> !CategorySpecification.PLACEHOLDER_ID.equals(id))
			.toList();
		if (!named.isEmpty() && real.isEmpty()) {
			return BUILDER.buildNoneFilter();
		}
		return BUILDER.buildNestedLookupInFilter(ATTRIBUTES, NodeAttributesEntity_.CATEGORY, CategoryEntity_.CATEGORY_ID, real);
	}

	/**
	 * Restricts to the nodes placed in one of the given topographies, which are alternatives — the exact counterpart of
	 * the substring {@link #matchesLocation} filter, for a place picked from the {@code /topographies} list.
	 */
	static Specification<NodeEntity> hasTopography(final List<Integer> topographyIds) {
		return BUILDER.buildNestedLookupInFilter(ATTRIBUTES, NodeAttributesEntity_.TOPOGRAPHY, TopographyEntity_.ID, topographyIds);
	}

	/** Matches the place through the topography a node is placed in, or through its free-text place. */
	static Specification<NodeEntity> matchesLocation(final String location) {
		return BUILDER.buildNestedLocationFilter(ATTRIBUTES, NodeAttributesEntity_.TOPOGRAPHY, LOCATION_ATTRIBUTES, NodeAttributesEntity_.LOCATION_TEXT, location);
	}

	/**
	 * Keeps nodes whose period overlaps the requested range. A node without a stop year has not ended, so it runs into
	 * every range that starts later.
	 */
	static Specification<NodeEntity> activeFrom(final Integer yearFrom) {
		return BUILDER.buildNumberAtLeastOrOpenFilter(STOP_YEAR, yearFrom);
	}

	static Specification<NodeEntity> activeUntil(final Integer yearTo) {
		return BUILDER.buildNumberAtMostOrOpenFilter(START_YEAR, yearTo);
	}

	/**
	 * Deletion sets {@code DELETEDDATE} but leaves the published bit set, so {@link #published()} alone does not hide
	 * the row.
	 */
	static Specification<NodeEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<NodeEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	static Specification<NodeEntity> fetchNodeType() {
		return BUILDER.buildFetchJoin(NODE_TYPE);
	}

	/**
	 * Fetches the node's attribute row and the lookups it names in the same query. A shared-key one-to-one cannot be
	 * left unloaded — Hibernate has to know whether the row exists — so without this every node on a page would cost a
	 * query of its own, and each lookup another.
	 */
	static Specification<NodeEntity> fetchAttributes() {
		return BUILDER.buildNestedFetchJoin(ATTRIBUTES, ATTRIBUTE_LOOKUPS);
	}
}
