package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the Criteria predicates the archive tables have in common, so that each {@code XxxSpecifications} class only
 * has to state <em>which</em> attributes a filter applies to, not <em>how</em> the predicate is constructed.
 *
 * <p>
 * The tables this service reads are near-identical in shape: a bitmask status column, a soft-delete date, a numeric
 * primary key, a couple of free-text columns and a topography foreign key. Without this class every one of them
 * repeats the same ~120 lines, including two constructs that fail quietly when they are copied wrong — the escaping in
 * {@link #buildLikeAllWordsFilter} and the count-projection guard in {@link #buildFetchJoin}.
 *
 * <p>
 * Attributes are addressed by name. Callers pass constants from the generated JPA metamodel ({@code PhotoEntity_}
 * and friends) rather than string literals, which is what keeps this compile-checked.
 *
 * <p>
 * Instantiate one per entity type — the instance carries the type parameter so that call sites get
 * {@code Specification<PhotoEntity>} back without a type witness:
 *
 * <pre>{@code
 * private static final SpecificationBuilder<PhotoEntity> BUILDER = new SpecificationBuilder<>();
 * }</pre>
 *
 * @param <T> the entity type the produced specifications apply to
 */
public class SpecificationBuilder<T> {

	/**
	 * Escape character for {@code LIKE} patterns. {@code !} is used rather than a backslash because MariaDB also treats
	 * the backslash as a string-literal escape, which would make the pattern depend on the {@code NO_BACKSLASH_ESCAPES}
	 * sql_mode.
	 */
	private static final char LIKE_ESCAPE = '!';

	private static final Pattern LIKE_WILDCARDS = Pattern.compile("([!%_])");
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");

	/**
	 * Equality filter. Returns {@link Specification#unrestricted()} when the value is {@code null}, so callers can
	 * combine filters unconditionally with {@link Specification#allOf}, which does not accept {@code null} elements.
	 *
	 * @param  attribute the entity attribute to compare
	 * @param  value     the value to compare against, or {@code null} to not filter
	 * @return           a specification matching the value, unrestricted when the value is null
	 */
	public Specification<T> buildEqualFilter(final String attribute, final Object value) {
		if (value == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.equal(root.get(attribute), value);
	}

	/**
	 * Filter matching rows where the attribute is {@code NULL}. Used for the soft-delete columns, which are set on
	 * deletion and left {@code NULL} otherwise.
	 *
	 * @param  attribute the entity attribute that must be null
	 * @return           a specification matching rows with no value in the attribute
	 */
	public Specification<T> buildIsNullFilter(final String attribute) {
		return (root, _, cb) -> cb.isNull(root.get(attribute));
	}

	/**
	 * Filter matching rows where a given bit of a bitmask column is set.
	 *
	 * <p>
	 * The legacy schema stores status in an {@code OPTIONS} bitmask rather than a status value, and several bits can be
	 * set at once, so the test has to be {@code (OPTIONS & bit) = bit} and never {@code OPTIONS = bit}. A row with a
	 * {@code NULL} bitmask does not match, since {@code bitand(NULL, x)} is {@code NULL}.
	 *
	 * <p>
	 * {@code bitand} is already registered by the MariaDB dialect and needs no custom function contributor.
	 *
	 * @param  attribute the bitmask attribute
	 * @param  bit       the bit that must be set
	 * @return           a specification matching rows where the bit is set
	 */
	public Specification<T> buildBitmaskFilter(final String attribute, final int bit) {
		return (root, _, cb) -> cb.equal(cb.function("bitand", Integer.class, root.get(attribute), cb.literal(bit)), bit);
	}

	/**
	 * Free-text filter requiring that <em>every</em> word in the query occurs in at least one of the given attributes.
	 *
	 * <p>
	 * The words need not be adjacent, nor occur in the same attribute — a single raw {@code LIKE} over the whole query
	 * would require the exact word order, so "hamnen sundsvall" would miss a row titled "Hamnen i Sundsvall".
	 *
	 * <p>
	 * Wildcards in the query are escaped; without that, a user searching for {@code %} would match the entire archive.
	 * Matching is case-insensitive because the columns use a {@code _ci} collation.
	 *
	 * @param  attributes the attributes to search, at least one
	 * @param  query      the free-text query, or {@code null}/blank to not filter
	 * @return            a specification matching every word, unrestricted when the query yields no words
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
	 * Adds a left fetch join for an association, so that mapping a page of results does not fire one additional select
	 * per row. Adds no restriction of its own.
	 *
	 * <p>
	 * A fetch join is invalid in a count projection, and Spring Data reuses the same specification for both the content
	 * query and the count query, so the fetch is skipped when the result type is {@link Long}. The
	 * {@code CriteriaQuery} is nullable in Spring Data JPA 4 and is treated as "not a count query" when absent.
	 *
	 * <p>
	 * Fetching also decides what happens to a foreign key that points at a row which does not exist — and the legacy
	 * schema declares no foreign key constraints, so that is representable. The left join resolves such an association
	 * to {@code null}; a plain lazy load would hand back a proxy that throws {@code EntityNotFoundException} when read.
	 *
	 * @param  association the association attribute to fetch
	 * @return             a specification that adds the fetch join and no restriction
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
