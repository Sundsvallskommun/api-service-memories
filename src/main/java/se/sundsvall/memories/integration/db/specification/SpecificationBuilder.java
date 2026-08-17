package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

	private static final char LIKE_ESCAPE = '!';

	private static final int PUBLISHED_BIT = 4;

	private static final int YEAR_LENGTH = 4;

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

	private static String asYearString(final Integer year) {
		return "%04d".formatted(year);
	}

	private Predicate matchesAnyAttribute(final Root<T> root, final CriteriaBuilder cb, final List<String> attributes, final String word) {
		final var pattern = "%" + escapeWildcards(word) + "%";
		return cb.or(attributes.stream()
			.map(attribute -> cb.like(root.<String>get(attribute), pattern, LIKE_ESCAPE))
			.toArray(Predicate[]::new));
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
