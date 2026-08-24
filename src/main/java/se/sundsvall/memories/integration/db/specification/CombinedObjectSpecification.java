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

	/**
	 * The value {@code sortBy} takes to order by how well a row matches the query. It is not an attribute of
	 * {@link CombinedObjectEntity} — nothing stores it, it is computed per request — so it is translated here rather
	 * than handed to Spring Data, which is also why every other sort key has to be translated here.
	 */
	String RELEVANCE = "relevance";

	/**
	 * Matches rows where every word of the query occurs somewhere in the title and comment the view concatenates, in
	 * any order — the same rule the per-type searches apply. The single {@code LIKE} over the whole query string this
	 * replaces could only find the words as a verbatim phrase, so a query as ordinary as a first name followed by a
	 * surname found nothing at all, while a single word found everything whose comment merely mentioned it.
	 *
	 * <p>
	 * The match deliberately stays on {@code SEARCH_TEXT} alone: {@code NAME_TEXT} is a subset of it in every branch of
	 * the view, so adding it here would widen the {@code OR} without matching a single further row. It earns its keep
	 * in {@link #orderedBy(String, Sort)} instead, where it decides the order.
	 */
	static Specification<CombinedObjectEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(List.of(SEARCH_TEXT), query);
	}

	/**
	 * Every filter the search restricts on, without the fetch joins and without the ordering. The chip counters build
	 * their grouped query from the very same predicates rather than from a second copy of them written in SQL: the two
	 * disagreeing is the failure mode — a chip claiming more hits than the list can show. The one predicate they leave
	 * out is the type selection itself; see {@link #filtersExcludingObjectType(CombinedObjectParameters)}.
	 */
	static Specification<CombinedObjectEntity> filters(final CombinedObjectParameters parameters) {
		return filtersExcludingObjectType(parameters)
			.and(hasObjectType(parameters.getObjectType()));
	}

	/**
	 * The same filters with the type selection left out, which is what the counters count over. A chip has to keep
	 * saying how many rows the search would return <em>if</em> that type were selected — otherwise selecting Foto
	 * zeroes every other chip, and the only way back to Ljud is to clear the filter the user cannot see the effect of.
	 * Every other filter still applies, so the chips narrow with the query, the years, the location and the originator;
	 * only the selection they are themselves the control for leaves them alone.
	 */
	static Specification<CombinedObjectEntity> filtersExcludingObjectType(final CombinedObjectParameters parameters) {
		return matches(parameters.getQuery())
			.and(matchesLocation(parameters.getLocation()))
			.and(yearAtLeast(parameters.getYearFrom()))
			.and(yearAtMost(parameters.getYearTo()))
			.and(matchesCreator(parameters.getCreator()))
			.and(hasCreatorPerson(parameters.getCreatorPersonId()))
			.and(hasCreatorLegalEntity(parameters.getCreatorLegalEntityId()));
	}

	/**
	 * Restricts the search to the given object types, which are alternatives: Foto and Ljud selected together means
	 * either of them, not both at once. The accepted values are the ones the view emits, which are also the ones the
	 * response reports per row and counts under {@code typeCounts} — a client can filter on exactly what it counted.
	 * An empty selection is no filter at all, and returns every type.
	 */
	static Specification<CombinedObjectEntity> hasObjectType(final List<String> objectTypes) {
		return BUILDER.buildInFilter(OBJECT_TYPE, objectTypes);
	}

	/**
	 * Orders the result: relevance first when the caller asked for it, or asked for nothing at all but did pass a
	 * query, then whatever else the caller asked for, and last always {@code objectKey}. That final key is what makes
	 * paging stable — the view is a {@code UNION ALL}, and without a unique key the database is free to hand back the
	 * same row on two pages and never hand back another. It is the same tiebreak
	 * {@link se.sundsvall.memories.service.util.Pageables} appends for every other search; this search has to append it
	 * itself, because it owns its whole ordering.
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

	/**
	 * The keys to order by, in order. A caller who passed no sort gets relevance when there is a query to be relevant
	 * to, and nothing but the tiebreak when there is not — the closest thing to the arbitrary order the union used to
	 * hand back, only reproducible. A relevance key is dropped rather than ordered by when the query is blank: every
	 * row would score the same, and a constant in an {@code ORDER BY} is read as a column position.
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
	 * Translates one sort key into a criteria order. Every key but {@link #RELEVANCE} is an attribute of the entity;
	 * relevance is the computed expression, which is the whole reason the ordering is built here rather than left to
	 * Spring Data.
	 */
	private static Order toOrder(final Root<CombinedObjectEntity> root, final CriteriaBuilder cb, final Sort.Order order, final String query) {
		final Expression<?> expression = Optional.of(order.getProperty())
			.filter(RELEVANCE::equals)
			.<Expression<?>>map(_ -> BUILDER.relevance(root, cb, NAME_TEXT, query))
			.orElseGet(() -> root.get(order.getProperty()));

		return Optional.of(order)
			.filter(Sort.Order::isAscending)
			.map(_ -> cb.asc(expression))
			.orElseGet(() -> cb.desc(expression));
	}

	private static boolean hasQuery(final String query) {
		return Optional.ofNullable(query).filter(not(String::isBlank)).isPresent();
	}
}
