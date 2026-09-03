package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

	private static final char LIKE_ESCAPE = '!';

	private static final int PUBLISHED_BIT = 4;

	private static final int YEAR_LENGTH = 4;

	private static final int RELEVANCE_EXACT_NAME = 0;
	private static final int RELEVANCE_NAME_PREFIX = 1;
	private static final int RELEVANCE_ALL_WORDS_IN_NAME = 2;
	private static final int RELEVANCE_ANY_WORD_IN_NAME = 3;
	private static final int RELEVANCE_BODY_ONLY = 4;

	private static final Pattern LIKE_WILDCARDS = Pattern.compile("([!%_])");
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");

	/**
	 * Matches rows where the attribute equals the value, or every row when the value is {@code null}.
	 */
	public Specification<T> buildEqualFilter(final String attribute, final Object value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.equal(root.get(attribute), value);
	}

	/**
	 * Matches rows where the attribute differs from the value. Rows where the attribute is {@code NULL} are kept, which
	 * is what the legacy schema needs: it uses sentinel ids rather than {@code NULL}, and a row that has neither is
	 * still a real row.
	 */
	public Specification<T> buildNotEqualFilter(final String attribute, final Object value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.or(cb.isNull(root.get(attribute)), cb.notEqual(root.get(attribute), value));
	}

	/**
	 * Matches rows where the attribute equals the value regardless of case. Matches every row when the value is blank,
	 * so the request parameter can be passed through untrimmed.
	 */
	public Specification<T> buildEqualIgnoreCaseFilter(final String attribute, final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		final var lowerCased = value.trim().toLowerCase();
		return (root, _, cb) -> cb.equal(cb.lower(root.get(attribute)), lowerCased);
	}

	/**
	 * Matches rows whose attribute is one of the values, which are alternatives. Blank values are dropped, so an empty
	 * or blank selection matches every row.
	 */
	public Specification<T> buildInFilter(final String attribute, final List<String> values) {
		final var wanted = distinctNonBlank(values);
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, _) -> root.get(attribute).in(wanted);
	}

	/**
	 * Matches rows whose value, lower-cased, is one of the given alternatives, compared lower-cased. Matches every row
	 * when no alternative is given.
	 */
	public Specification<T> buildInIgnoreCaseFilter(final String attribute, final List<String> values) {
		final var wanted = distinctNonBlank(values).stream()
			.map(value -> value.toLowerCase(Locale.ROOT))
			.distinct()
			.toList();
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.lower(root.get(attribute)).in(wanted);
	}

	/**
	 * Matches no row at all — for a filter value that names nothing the data can hold, where matching every row would
	 * be the wrong reading of "not found".
	 */
	public Specification<T> buildNoneFilter() {
		return (_, _, cb) -> cb.disjunction();
	}

	/**
	 * Matches rows where the value occurs anywhere in at least one of the attributes. Wildcards in the value are
	 * escaped. Matches every row when the value is blank.
	 */
	public Specification<T> buildLikeAnyFilter(final List<String> attributes, final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> matchesAnyAttribute(root, cb, attributes, value.trim());
	}

	/**
	 * Matches rows where the value occurs in at least one attribute of at least one of the given associations, skipping
	 * the sentinel row each association may point at, and rows the association points at that are soft-deleted. Both
	 * foreign keys default to a placeholder called "Ingen" rather than to {@code NULL}, so without the sentinel guard a
	 * search for that word would return everything. Matches every row when the value is blank.
	 */
	public Specification<T> buildAssociationLikeAnyFilter(final List<AssociationAttributes> associations, final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		final var pattern = "%" + escapeWildcards(value.trim()) + "%";
		return (root, _, cb) -> cb.or(associations.stream()
			.map(association -> matchesAssociation(root, cb, association, pattern))
			.toArray(Predicate[]::new));
	}

	private Predicate matchesAssociation(final Root<T> root, final CriteriaBuilder cb, final AssociationAttributes association, final String pattern) {
		final var join = reuseFetchOrJoin(root, association.association());
		final var matches = association.attributeGroups().stream()
			.map(group -> cb.like(joined(cb, join, group), pattern, LIKE_ESCAPE));
		return cb.and(
			cb.notEqual(join.get(association.idAttribute()), association.placeholderId()),
			cb.isNull(join.get(association.deletedAttribute())),
			cb.or(matches.toArray(Predicate[]::new)));
	}

	/**
	 * The attributes of one group as a single space-separated string, so that a value spanning them still matches: a
	 * person's name lives in two columns, and a search for "Anton Nordin" is in neither of them on its own. A missing
	 * attribute reads as empty rather than turning the whole expression into {@code NULL}.
	 */
	private Expression<String> joined(final CriteriaBuilder cb, final Join<T, ?> join, final List<String> attributes) {
		return attributes.stream()
			.map(attribute -> cb.coalesce(join.<String>get(attribute), ""))
			.map(Expression.class::cast)
			.reduce((left, right) -> cb.concat(cb.concat((Expression<String>) left, " "), (Expression<String>) right))
			.map(expression -> (Expression<String>) expression)
			.orElseThrow(() -> new IllegalArgumentException("An association attribute group cannot be empty"));
	}

	/**
	 * What a value may match on one association, together with the sentinel row that never counts as a match. Each
	 * group is matched as one space-separated string, so attributes that together form a single name — a person's given
	 * and family name — belong in the same group, while attributes that are alternatives to each other get one group
	 * apiece.
	 *
	 * @param association      name of the association attribute
	 * @param attributeGroups  attributes on the associated entity to match against, grouped
	 * @param idAttribute      name of the associated entity's id attribute
	 * @param placeholderId    id of the sentinel row
	 * @param deletedAttribute name of the associated entity's soft-delete attribute, which must be null to match
	 */
	public record AssociationAttributes(String association, List<List<String>> attributeGroups, String idAttribute, Object placeholderId, String deletedAttribute) {}

	/**
	 * Matches rows where the attribute is {@code NULL}.
	 */
	public Specification<T> buildIsNullFilter(final String attribute) {
		return (root, _, cb) -> cb.isNull(root.get(attribute));
	}

	/**
	 * Matches rows where bit 4 of the given bitmask attribute is set, which is what marks a row as published.
	 */
	public Specification<T> buildPublishedFilter(final String attribute) {
		return (root, _, cb) -> cb.equal(cb.function("bitand", Integer.class, root.get(attribute), cb.literal(PUBLISHED_BIT)), PUBLISHED_BIT);
	}

	/**
	 * Matches rows where every word in the query occurs in at least one of the attributes, in any order and not
	 * necessarily the same one. Wildcards in the query are escaped. Matches every row when the query yields no words.
	 */
	public Specification<T> buildLikeAllWordsFilter(final List<String> attributes, final String query) {
		final var words = splitWords(query);
		if (words.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.and(words.stream()
			.map(word -> matchesAnyAttribute(root, cb, attributes, word))
			.toArray(Predicate[]::new));
	}

	/**
	 * Matches rows whose place matches the given text, either through the topography association ({@code TOPNAMN} or
	 * {@code PLATS}) or through the entity's own free-text place attribute. The association is joined with a left join,
	 * so a row without topography still matches on its free text. Matches every row when the location is blank.
	 */
	public Specification<T> buildLocationFilter(final String association, final List<String> associationAttributes, final String textAttribute,
		final String location) {
		if (location == null || location.isBlank()) {
			return Specification.unrestricted();
		}
		final var pattern = "%" + escapeWildcards(location.trim()) + "%";
		return (root, _, cb) -> {
			final var topography = reuseFetchOrJoin(root, association);
			final var matches = Stream.concat(
				associationAttributes.stream().map(attribute -> cb.like(topography.<String>get(attribute), pattern, LIKE_ESCAPE)),
				Stream.of(cb.like(root.<String>get(textAttribute), pattern, LIKE_ESCAPE)));
			return cb.or(matches.toArray(Predicate[]::new));
		};
	}

	/**
	 * Matches rows whose year is at least {@code yearFrom}. The year is read from the four leading characters of the
	 * first non-blank attribute in {@code attributes}, which lets an entity end its period on one column and fall back
	 * to another. Rows whose leading characters are not four digits are excluded rather than read as year zero, which
	 * would otherwise let free text such as {@code 'okänt'} satisfy every bound. Matches every row when
	 * {@code yearFrom} is {@code null}.
	 */
	public Specification<T> buildYearAtLeastFilter(final List<String> attributes, final Integer yearFrom) {
		if (yearFrom == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var year = leadingYear(root, cb, attributes);
			return cb.and(isFourDigits(cb, year), cb.greaterThanOrEqualTo(year, asYearString(yearFrom)));
		};
	}

	/**
	 * Like {@link #buildYearAtLeastFilter(List, Integer)}, except that a missing or unreadable year is treated as an
	 * open period rather than as no period. {@code JURPERS} needs this: a legal entity without an end date has not
	 * ended, so it is still active in every range that starts after it did.
	 */
	public Specification<T> buildYearAtLeastOrOpenFilter(final List<String> attributes, final Integer yearFrom) {
		if (yearFrom == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var year = leadingYear(root, cb, attributes);
			return cb.or(isOpen(cb, year), cb.greaterThanOrEqualTo(year, asYearString(yearFrom)));
		};
	}

	/**
	 * Like {@link #buildYearAtMostFilter(List, Integer)}, except that a missing or unreadable year is treated as an open
	 * period. See {@link #buildYearAtLeastOrOpenFilter(List, Integer)}.
	 */
	public Specification<T> buildYearAtMostOrOpenFilter(final List<String> attributes, final Integer yearTo) {
		if (yearTo == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var year = leadingYear(root, cb, attributes);
			return cb.or(isOpen(cb, year), cb.lessThanOrEqualTo(year, asYearString(yearTo)));
		};
	}

	/**
	 * Matches rows where the attribute is at least the value. Unlike the year filters this one compares a real number,
	 * so no digit guard is needed: a row whose value is {@code NULL} simply does not compare. Matches every row when the
	 * value is {@code null}.
	 */
	public <Y extends Comparable<? super Y>> Specification<T> buildAtLeastFilter(final String attribute, final Y value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value);
	}

	/**
	 * Matches rows where the attribute is at most the value. See {@link #buildAtLeastFilter(String, Comparable)}.
	 */
	public <Y extends Comparable<? super Y>> Specification<T> buildAtMostFilter(final String attribute, final Y value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value);
	}

	/**
	 * Matches rows whose association points at the given id. Reading the id through the association rather than through
	 * a second mapping of the foreign key keeps the two from disagreeing; Hibernate resolves it to the foreign key
	 * column, so this adds no join. Matches every row when the id is {@code null}.
	 */
	public Specification<T> buildAssociationEqualFilter(final String association, final String attribute, final Object value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.equal(root.get(association).get(attribute), value);
	}

	/**
	 * As {@link #buildAssociationEqualFilter(String, String, Object)}, and the associated row must not be soft-deleted.
	 * The originator filters need that: a deleted register record is not served by its own endpoint, so it must not
	 * select objects here either.
	 */
	public Specification<T> buildAssociationEqualFilter(final String association, final String attribute, final String deletedAttribute, final Object value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var join = reuseFetchOrJoin(root, association);
			return cb.and(cb.equal(join.get(attribute), value), cb.isNull(join.get(deletedAttribute)));
		};
	}

	/**
	 * As {@link #buildAssociationEqualFilter(String, String, String, Object)}, for several ids that are alternatives.
	 * Matches every row when the list yields no ids.
	 */
	public Specification<T> buildAssociationInFilter(final String association, final String attribute, final String deletedAttribute, final List<?> values) {
		final var wanted = distinctNonNull(values);
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var join = reuseFetchOrJoin(root, association);
			return cb.and(join.get(attribute).in(wanted), cb.isNull(join.get(deletedAttribute)));
		};
	}

	/**
	 * The string a row's place sorts on: the free-text attribute when present, otherwise the association's attributes
	 * in the given order. The registers fill only the free text and the objects often only the association, so without
	 * the fallback either kind would clump at one end of the order. Blank values count as absent, like in the display
	 * name they fall back through.
	 */
	public Expression<String> location(final Root<T> root, final CriteriaBuilder cb, final String textAttribute, final String association,
		final List<String> associationAttributes) {
		final var join = reuseFetchOrJoin(root, association);
		final var coalesce = cb.<String>coalesce();
		coalesce.value(cb.nullif(root.<String>get(textAttribute), ""));
		associationAttributes.forEach(attribute -> coalesce.value(cb.nullif(join.<String>get(attribute), "")));
		return coalesce;
	}

	/**
	 * Matches rows whose year is at most {@code yearTo}, read the same way as in
	 * {@link #buildYearAtLeastFilter(List, Integer)}.
	 */
	public Specification<T> buildYearAtMostFilter(final List<String> attributes, final Integer yearTo) {
		if (yearTo == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var year = leadingYear(root, cb, attributes);
			return cb.and(isFourDigits(cb, year), cb.lessThanOrEqualTo(year, asYearString(yearTo)));
		};
	}

	/**
	 * Matches rows whose number is at least the value, treating a row without one as an open period rather than as no
	 * period. The archive nodes need this: a series that has not ended carries no stop year, so it is still running in
	 * every range that starts after it did. The legacy schema expresses "unknown" as both {@code NULL} and {@code 0},
	 * so both count as open. Matches every row when the value is {@code null}.
	 */
	public Specification<T> buildNumberAtLeastOrOpenFilter(final String attribute, final Integer value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.or(isOpenNumber(cb, root.get(attribute)), cb.greaterThanOrEqualTo(root.get(attribute), value));
	}

	/**
	 * Matches rows whose number is at most the value. See {@link #buildNumberAtLeastOrOpenFilter(String, Integer)}.
	 */
	public Specification<T> buildNumberAtMostOrOpenFilter(final String attribute, final Integer value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.or(isOpenNumber(cb, root.get(attribute)), cb.lessThanOrEqualTo(root.get(attribute), value));
	}

	/**
	 * Left-fetches an association, adding no restriction of its own. The fetch is skipped for the count query Spring
	 * Data derives from the same specification, where a fetch join is invalid.
	 */
	public Specification<T> buildFetchJoin(final String association) {
		return (root, query, cb) -> {
			if (query == null || !Long.class.equals(query.getResultType())) {
				root.fetch(association, JoinType.LEFT);
			}
			return cb.conjunction();
		};
	}

	/**
	 * Orders the query without restricting it, so an order can be a computed expression rather than a column. Only
	 * applies while the {@code Pageable} carries no sort of its own, which Spring Data would otherwise use instead.
	 * Skipped for the derived count query.
	 */
	public Specification<T> buildOrderBy(final BiFunction<Root<T>, CriteriaBuilder, List<Order>> orders) {
		return (root, query, cb) -> {
			if (query != null && !Long.class.equals(query.getResultType())) {
				query.orderBy(orders.apply(root, cb));
			}
			return cb.conjunction();
		};
	}

	/**
	 * How well the attribute matches the query, lower being better: exact (0), prefix (1), all words (2), some word
	 * (3), none (4). Ordering ascending therefore puts a name or title hit above one that only matched a comment. Uses
	 * the same escaped {@code LIKE} as the filters, so ranking and matching cannot disagree.
	 */
	public Expression<Integer> relevance(final Root<T> root, final CriteriaBuilder cb, final String attribute, final String query) {
		final var words = splitWords(query);
		final var value = query.trim().toLowerCase();
		final var name = cb.lower(root.<String>get(attribute));

		return cb.<Integer>selectCase()
			.when(cb.equal(name, value), RELEVANCE_EXACT_NAME)
			.when(cb.like(name, escapeWildcards(value) + "%", LIKE_ESCAPE), RELEVANCE_NAME_PREFIX)
			.when(cb.and(matchesWords(name, cb, words)), RELEVANCE_ALL_WORDS_IN_NAME)
			.when(cb.or(matchesWords(name, cb, words)), RELEVANCE_ANY_WORD_IN_NAME)
			.otherwise(RELEVANCE_BODY_ONLY);
	}

	/** One {@code LIKE} per word, for the caller to combine with {@code and} or {@code or}. */
	private Predicate[] matchesWords(final Expression<String> name, final CriteriaBuilder cb, final List<String> words) {
		return words.stream()
			.map(word -> cb.like(name, "%" + escapeWildcards(word.toLowerCase()) + "%", LIKE_ESCAPE))
			.toArray(Predicate[]::new);
	}

	/**
	 * A number that carries no information: the legacy schema leaves an unknown year as {@code NULL} in some rows and as
	 * {@code 0} in others, and neither bounds a period.
	 */
	private Predicate isOpenNumber(final CriteriaBuilder cb, final Expression<Integer> number) {
		return cb.or(cb.isNull(number), cb.equal(number, 0));
	}

	/**
	 * Reuses the join a fetch of the same association already created, so that filtering on it does not add a second
	 * {@code LEFT JOIN}. A fetch and a join are separate nodes in the criteria tree, but the same Hibernate object
	 * implements both. There is no fetch in the count query, where the specification falls back to a plain join.
	 */
	@SuppressWarnings("unchecked")
	private Join<T, ?> reuseFetchOrJoin(final Root<T> root, final String association) {
		return root.getFetches().stream()
			.filter(fetch -> fetch.getAttribute().getName().equals(association))
			.filter(Join.class::isInstance)
			.map(fetch -> (Join<T, ?>) fetch)
			.findFirst()
			.orElseGet(() -> root.join(association, JoinType.LEFT));
	}

	/**
	 * The four leading characters of the first non-blank attribute, as a string. The comparison stays textual: the years
	 * live in free-text date columns, and a four-digit year sorts the same way as a number.
	 */
	private Expression<String> leadingYear(final Root<T> root, final CriteriaBuilder cb, final List<String> attributes) {
		final var coalesce = cb.<String>coalesce();
		attributes.forEach(attribute -> coalesce.value(cb.nullif(root.<String>get(attribute), "")));
		return cb.substring(coalesce, 1, YEAR_LENGTH);
	}

	/**
	 * Excludes anything that is not four digits. Digits sort before letters, so a real year falls inside the range while
	 * free text and blanks fall outside it.
	 */
	private static Predicate isFourDigits(final CriteriaBuilder cb, final Expression<String> year) {
		return cb.between(year, "0000", "9999");
	}

	/**
	 * The opposite of {@link #isFourDigits}, with the {@code NULL} case spelled out: a missing value is not a year
	 * either, and {@code NOT NULL-predicate} is {@code NULL} rather than true.
	 */
	private static Predicate isOpen(final CriteriaBuilder cb, final Expression<String> year) {
		return cb.or(cb.isNull(year), cb.not(isFourDigits(cb, year)));
	}

	private static String asYearString(final Integer year) {
		return "%04d".formatted(year);
	}

	private Predicate matchesAnyAttribute(final Root<T> root, final CriteriaBuilder cb, final List<String> attributes, final String word) {
		final var pattern = "%" + escapeWildcards(word) + "%";
		return cb.or(attributes.stream()
			.map(attribute -> cb.like(root.<String>get(attribute), pattern, LIKE_ESCAPE))
			.toArray(Predicate[]::new));
	}

	private static List<?> distinctNonNull(final List<?> values) {
		if (values == null) {
			return List.of();
		}
		return values.stream()
			.filter(Objects::nonNull)
			.distinct()
			.toList();
	}

	private static List<String> distinctNonBlank(final List<String> values) {
		if (values == null) {
			return List.of();
		}
		return values.stream()
			.filter(Objects::nonNull)
			.map(String::trim)
			.filter(value -> !value.isEmpty())
			.distinct()
			.toList();
	}

	private static List<String> splitWords(final String query) {
		if (query == null || query.isBlank()) {
			return List.of();
		}
		return Arrays.stream(WHITESPACE.split(query.trim()))
			.filter(word -> !word.isEmpty())
			.toList();
	}

	private static String escapeWildcards(final String word) {
		return LIKE_WILDCARDS.matcher(word).replaceAll(LIKE_ESCAPE + "$1");
	}
}
