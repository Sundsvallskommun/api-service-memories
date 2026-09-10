package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CategoryEntity_;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;
import se.sundsvall.memories.integration.db.model.PersonEntity_;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static java.util.function.Predicate.not;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.GENDER;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.NAME_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.OBJECT_KEY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.SEARCH_TEXT;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.TOPOGRAPHY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.YEAR;

public interface CombinedObjectSpecification {

	SpecificationBuilder<CombinedObjectEntity> BUILDER = new SpecificationBuilder<>();

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	/** The topography columns the location sort falls back through, in the display name's order. */
	List<String> LOCATION_DISPLAY_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE, TopographyEntity_.CODE);

	/** Sort value for ranking by how well a row matches the query. Computed per request, so not an entity attribute. */
	String RELEVANCE = "relevance";

	/**
	 * Sort value for the place, named after the filter parameter rather than an attribute: it reads the free-text
	 * location — the birth parish for persons and seamen — and falls back to the topography columns, since the
	 * registers fill only the former and the objects often only the latter. One key therefore sorts objects by place
	 * and the registers by parish, without either kind clumping at one end.
	 */
	String LOCATION = "location";

	/**
	 * Matches rows where every word of the query occurs in {@code SEARCH_TEXT} (title and comment), in any order.
	 * {@code NAME_TEXT} is a subset of it and would add no rows, so it decides the order instead.
	 */
	static Specification<CombinedObjectEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(List.of(SEARCH_TEXT), query);
	}

	/** Every filter the search applies, without fetch joins or ordering. The counters share the same predicates. */
	static Specification<CombinedObjectEntity> filters(final CombinedObjectParameters parameters) {
		return filtersExcludingFacets(parameters)
			.and(hasObjectType(parameters.getObjectType()))
			.and(hasGender(parameters.getGender()))
			.and(hasCategory(parameters.getCategoryId()));
	}

	/**
	 * What the type counters count over: every filter except the type selection, so a chip keeps reporting how many
	 * objects selecting that type would return.
	 */
	static Specification<CombinedObjectEntity> filtersExcludingObjectType(final CombinedObjectParameters parameters) {
		return filtersExcludingFacets(parameters)
			.and(hasGender(parameters.getGender()))
			.and(hasCategory(parameters.getCategoryId()));
	}

	/**
	 * What the gender counters count over: every filter except the gender selection, mirroring how the type counters
	 * leave out theirs. Each dimension ignores only its own selection. The counters sum to the number of matched rows
	 * that record a gender at all — the rest of a result carries none rather than an unknown one.
	 */
	static Specification<CombinedObjectEntity> filtersExcludingGender(final CombinedObjectParameters parameters) {
		return filtersExcludingFacets(parameters)
			.and(hasObjectType(parameters.getObjectType()))
			.and(hasCategory(parameters.getCategoryId()));
	}

	/**
	 * What the category counters count over: every filter except the category selection, the third dimension that
	 * ignores only its own. The counters sum to the number of matched rows whose originator is a categorised legal
	 * entity — the register rows and the objects without one carry no category rather than an unknown one.
	 */
	static Specification<CombinedObjectEntity> filtersExcludingCategory(final CombinedObjectParameters parameters) {
		return filtersExcludingFacets(parameters)
			.and(hasObjectType(parameters.getObjectType()))
			.and(hasGender(parameters.getGender()));
	}

	/** The filters no counter leaves out: everything that is not a faceted selection. */
	private static Specification<CombinedObjectEntity> filtersExcludingFacets(final CombinedObjectParameters parameters) {
		return matches(parameters.getQuery())
			.and(matchesLocation(parameters.getLocation()))
			.and(hasTopography(parameters.getTopographyId()))
			.and(yearAtLeast(parameters.getYearFrom()))
			.and(yearAtMost(parameters.getYearTo()))
			.and(matchesCreator(parameters.getCreator()))
			.and(hasCreatorPerson(parameters.getCreatorPersonId()))
			.and(hasCreatorLegalEntity(parameters.getCreatorLegalEntityId()));
	}

	/**
	 * Restricts the rows that record a gender to the given one and leaves every other row untouched. Only the person
	 * registers record a gender (V2_3 labels every one of their rows, unknown included), so a selection speaks for
	 * those rows alone rather than excluding the types that record none — a search for photos and men returns both,
	 * the way the filter reads. The values are the labels {@code genderCounts} counts by, matched case-insensitively;
	 * one naming no gender matches no register row while still keeping the rows that record none.
	 */
	static Specification<CombinedObjectEntity> hasGender(final String gender) {
		return BUILDER.buildEqualIgnoreCaseOrMissingFilter(GENDER, gender);
	}

	/**
	 * Restricts to the given object types, which are alternatives. The values are the ones the view emits and
	 * {@code typeCounts} counts by. An empty selection matches every type.
	 */
	static Specification<CombinedObjectEntity> hasObjectType(final List<String> objectTypes) {
		return BUILDER.buildInFilter(OBJECT_TYPE, objectTypes);
	}

	/**
	 * Orders by the caller's sort keys, always ending with {@code objectKey}. The view is a {@code UNION ALL}, so
	 * without that unique key consecutive pages can repeat one row and skip another.
	 */
	static Specification<CombinedObjectEntity> orderedBy(final String query, final Sort sort) {
		return BUILDER.buildOrderBy((root, cb) -> orderKeys(sort, query).stream()
			.map(order -> toOrder(root, cb, order, query))
			.toList());
	}

	static Specification<CombinedObjectEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	/**
	 * Restricts to the rows placed in one of the given topographies, which are alternatives — the exact counterpart of
	 * the substring {@code location} filter, for a client that picked a place from the {@code /topographies} list. Only
	 * the object types and the legal entities carry a topography; the person registers hold a parish as free text, so
	 * this filter excludes them the way {@code location} does for census records.
	 */
	static Specification<CombinedObjectEntity> hasTopography(final List<Integer> topographyIds) {
		return BUILDER.buildAssociationInFilter(TOPOGRAPHY, TopographyEntity_.ID, topographyIds);
	}

	/** The view normalises an unreadable year to {@code NULL}, so a row without a year falls outside every range. */
	static Specification<CombinedObjectEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildAtLeastFilter(YEAR, yearFrom);
	}

	static Specification<CombinedObjectEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildAtMostFilter(YEAR, yearTo);
	}

	/** The originator associations, each with the sentinel id that never counts as a match. */
	SpecificationBuilder.GuardedAssociation CREATOR_PERSON_GUARD = new SpecificationBuilder.GuardedAssociation(CREATOR_PERSON, PersonEntity_.PERSON_ID,
		PersonSpecification.PLACEHOLDER_ID, PersonEntity_.DELETED_DATE);

	SpecificationBuilder.GuardedAssociation CREATOR_LEGAL_ENTITY_GUARD = new SpecificationBuilder.GuardedAssociation(CREATOR_LEGAL_ENTITY,
		LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntitySpecification.PLACEHOLDER_ID, LegalEntityEntity_.DELETED_DATE);

	/** The attributes an originator is matched on. */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		// a person's name spans two columns and is matched as one string; a legal entity's two names are alternatives
		new SpecificationBuilder.AssociationAttributes(CREATOR_PERSON_GUARD, List.of(List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME))),
		new SpecificationBuilder.AssociationAttributes(CREATOR_LEGAL_ENTITY_GUARD, List.of(List.of(LegalEntityEntity_.NAME), List.of(LegalEntityEntity_.ALTERNATIVE_NAMES))));

	/** Only the object branches carry an originator, so this filter also excludes the register types. */
	static Specification<CombinedObjectEntity> matchesCreator(final String creator) {
		return BUILDER.buildAssociationLikeAnyFilter(CREATOR_ATTRIBUTES, creator);
	}

	/**
	 * Restricts to the objects whose originator is a legal entity in one of the given categories, which are
	 * alternatives — what a client used to spell out as every {@code creatorLegalEntityId} in the category. A category
	 * is a property of the originator, so this filter has the shape of {@code creator}: only the object branches carry
	 * one, and the register types are excluded, a legal entity's own row among them, since the view does not carry its
	 * category. The sentinel category every legal entity defaults to is never a match — naming it alone matches
	 * nothing, the way an unknown object type does, rather than every object whose originator is uncategorised.
	 */
	static Specification<CombinedObjectEntity> hasCategory(final List<Integer> categoryIds) {
		return BUILDER.buildNestedAssociationInFilter(CREATOR_LEGAL_ENTITY_GUARD, LegalEntityEntity_.CATEGORY, CategoryEntity_.CATEGORY_ID,
			CategorySpecification.PLACEHOLDER_ID, categoryIds);
	}

	/**
	 * The rows the category counters can group: an originator that is a real, undeleted legal entity with a category
	 * other than the sentinel. Built from the same guards as {@link #hasCategory}, so a chip never counts a row selecting
	 * it would not return.
	 */
	static Specification<CombinedObjectEntity> hasCategorisedCreator() {
		return BUILDER.buildNestedAssociationPresentFilter(CREATOR_LEGAL_ENTITY_GUARD, LegalEntityEntity_.CATEGORY, CategoryEntity_.CATEGORY_ID,
			CategorySpecification.PLACEHOLDER_ID);
	}

	static Specification<CombinedObjectEntity> hasCreatorPerson(final Integer creatorPersonId) {
		return BUILDER.buildAssociationEqualFilter(CREATOR_PERSON, PersonEntity_.PERSON_ID, PersonEntity_.DELETED_DATE, creatorPersonId);
	}

	/** The ids are alternatives, so a whole category of legal entities can be filtered in one call. */
	static Specification<CombinedObjectEntity> hasCreatorLegalEntity(final List<Integer> creatorLegalEntityIds) {
		return BUILDER.buildAssociationInFilter(CREATOR_LEGAL_ENTITY, LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntityEntity_.DELETED_DATE, creatorLegalEntityIds);
	}

	static Specification<CombinedObjectEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<CombinedObjectEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	/**
	 * The sort keys to apply, in order. With no explicit sortBy: relevance if there is a query, otherwise only the
	 * {@code objectKey} tiebreak. Relevance is dropped when the query is blank, since MariaDB reads a constant in
	 * {@code ORDER BY} as a column position.
	 */
	private static Sort orderKeys(final Sort sort, final String query) {
		final var requested = Optional.of(sort)
			.filter(Sort::isSorted)
			.orElseGet(() -> defaultSort(query));

		final var applicable = Sort.by(requested.stream()
			.filter(order -> !RELEVANCE.equals(order.getProperty()) || hasQuery(query))
			.toList());

		return Optional.ofNullable(applicable.getOrderFor(OBJECT_KEY))
			.map(_ -> applicable)
			.orElseGet(() -> applicable.and(Sort.by(ASC, OBJECT_KEY)));
	}

	private static Sort defaultSort(final String query) {
		return Optional.ofNullable(query)
			.filter(CombinedObjectSpecification::hasQuery)
			.map(_ -> Sort.by(ASC, RELEVANCE))
			.orElseGet(Sort::unsorted);
	}

	/**
	 * Translates one sort key into a criteria order. {@link #RELEVANCE} is computed and {@link #LOCATION} is renamed,
	 * every other key is an attribute.
	 */
	private static Order toOrder(final Root<CombinedObjectEntity> root, final CriteriaBuilder cb, final Sort.Order order, final String query) {
		final Expression<?> expression = switch (order.getProperty()) {
			case RELEVANCE -> BUILDER.relevance(root, cb, NAME_TEXT, query);
			case LOCATION -> BUILDER.location(root, cb, LOCATION_TEXT, TOPOGRAPHY, LOCATION_DISPLAY_ATTRIBUTES);
			default -> root.get(order.getProperty());
		};

		return Optional.of(order)
			.filter(Sort.Order::isAscending)
			.map(_ -> cb.asc(expression))
			.orElseGet(() -> cb.desc(expression));
	}

	private static boolean hasQuery(final String query) {
		return Optional.ofNullable(query).filter(not(String::isBlank)).isPresent();
	}
}
