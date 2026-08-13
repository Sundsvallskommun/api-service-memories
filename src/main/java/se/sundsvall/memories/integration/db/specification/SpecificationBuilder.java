package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

	private static final char LIKE_ESCAPE = '!';

	private static final int PUBLISHED_BIT = 4;

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
