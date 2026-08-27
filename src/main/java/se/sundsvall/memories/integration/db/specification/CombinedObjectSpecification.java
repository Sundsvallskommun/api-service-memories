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
		return filtersExcludingObjectType(parameters)
			.and(hasObjectType(parameters.getObjectType()));
	}

	/**
	 * What the type counters count over: every filter except the type selection, so a chip keeps reporting how many
	 * objects selecting that type would return.
	 */
	static Specification<CombinedObjectEntity> filtersExcludingObjectType(final CombinedObjectParameters parameters) {
		return filtersExcludingTypeAndGender(parameters)
			.and(hasGender(parameters.getGender()));
	}

	/**
	 * What the gender counters count over: every filter except the gender selection, mirroring how the type counters
	 * leave out theirs. Each dimension ignores only its own selection.
	 */
	static Specification<CombinedObjectEntity> filtersExcludingGender(final CombinedObjectParameters parameters) {
		return filtersExcludingTypeAndGender(parameters)
			.and(hasObjectType(parameters.getObjectType()));
	}

	private static Specification<CombinedObjectEntity> filtersExcludingTypeAndGender(final CombinedObjectParameters parameters) {
		return matches(parameters.getQuery())
			.and(matchesLocation(parameters.getLocation()))
			.and(yearAtLeast(parameters.getYearFrom()))
			.and(yearAtMost(parameters.getYearTo()))
			.and(matchesCreator(parameters.getCreator()))
			.and(hasCreatorPerson(parameters.getCreatorPersonId()))
			.and(hasCreatorLegalEntity(parameters.getCreatorLegalEntityId()));
	}

	/** Only the person registers record a gender, so this filter also excludes every other type. */
	static Specification<CombinedObjectEntity> hasGender(final String gender) {
		return BUILDER.buildEqualIgnoreCaseFilter(GENDER, gender);
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

	/** The view normalises an unreadable year to {@code NULL}, so a row without a year falls outside every range. */
	static Specification<CombinedObjectEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildAtLeastFilter(YEAR, yearFrom);
	}

	static Specification<CombinedObjectEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildAtMostFilter(YEAR, yearTo);
	}

	/** The attributes an originator is matched on, and the sentinel id that never counts as a match. */
	List<SpecificationBuilder.AssociationAttributes> CREATOR_ATTRIBUTES = List.of(
		// a person's name spans two columns and is matched as one string; a legal entity's two names are alternatives
		new SpecificationBuilder.AssociationAttributes(CREATOR_PERSON, List.of(List.of(PersonEntity_.FIRST_NAME, PersonEntity_.LAST_NAME)), PersonEntity_.PERSON_ID,
			PersonSpecification.PLACEHOLDER_ID, PersonEntity_.DELETED_DATE),
		new SpecificationBuilder.AssociationAttributes(CREATOR_LEGAL_ENTITY, List.of(List.of(LegalEntityEntity_.NAME), List.of(LegalEntityEntity_.ALTERNATIVE_NAMES)),
			LegalEntityEntity_.LEGAL_ENTITY_ID, LegalEntitySpecification.PLACEHOLDER_ID, LegalEntityEntity_.DELETED_DATE));

	/** Only the object branches carry an originator, so this filter also excludes the register types. */
	static Specification<CombinedObjectEntity> matchesCreator(final String creator) {
		return BUILDER.buildAssociationLikeAnyFilter(CREATOR_ATTRIBUTES, creator);
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
