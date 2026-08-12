package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

/**
 * Criteria specifications for searching the {@code FOTO} table.
 *
 * <p>
 * Each factory method returns {@link Specification#unrestricted()} when its filter is not requested, so callers can
 * combine them unconditionally with {@link Specification#allOf}, which does not accept {@code null} elements.
 *
 * <p>
 * <strong>Sorting:</strong> unlike the native queries these replace, a sort property supplied via
 * {@code Pageable} is an entity property (e.g. {@code documentTitle}), not a physical column name.
 */
public final class PhotoSpecifications {

	/**
	 * Bit {@code 4} of the {@code OPTIONS} bitmask marks a row as published. Other bits may be set at the same time, so
	 * the check must be {@code (OPTIONS & 4) = 4} and never {@code OPTIONS = 4}.
	 */
	private static final int PUBLISHED_BIT = 4;

	/**
	 * Escape character for {@code LIKE} patterns. {@code !} is used rather than a backslash because MariaDB also treats
	 * the backslash as a string-literal escape, which would make the pattern depend on the {@code NO_BACKSLASH_ESCAPES}
	 * sql_mode.
	 */
	private static final char LIKE_ESCAPE = '!';

	private static final Pattern LIKE_WILDCARDS = Pattern.compile("([!%_])");
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");

	/** The columns free-text search covers, matching the {@code MATCH (DOKTITEL, KOMMENT_FF)} index it replaces. */
	private static final List<String> SEARCHABLE_ATTRIBUTES = List.of("documentTitle", "comment");

	private PhotoSpecifications() {}

	/**
	 * Restricts the result to published rows.
	 *
	 * @return a specification matching rows where bit {@code 4} of {@code OPTIONS} is set
	 */
	public static Specification<PhotoEntity> published() {
		return (root, _, cb) -> cb.equal(
			cb.function("bitand", Integer.class, root.get("options"), cb.literal(PUBLISHED_BIT)), PUBLISHED_BIT);
	}

	/**
	 * Filters on the {@code OBJTYP} column (e.g. {@code Foto} or {@code Föremål}).
	 *
	 * @param  objectType the object type to filter by, or {@code null} to not filter
	 * @return            a specification matching the given object type, unrestricted when {@code objectType} is null
	 */
	public static Specification<PhotoEntity> hasObjectType(final String objectType) {
		if (objectType == null) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.equal(root.get("objectType"), objectType);
	}

	/**
	 * Free-text search across {@code DOKTITEL} and {@code KOMMENT_FF}.
	 *
	 * <p>
	 * The query is split on whitespace and <em>every</em> word must occur in at least one of the columns. The words need
	 * not be adjacent, nor occur in the same column — a single raw {@code LIKE} over the whole query would require the
	 * exact word order, so "hamnen sundsvall" would miss a photo titled "Hamnen i Sundsvall".
	 *
	 * <p>
	 * Matching is case-insensitive because the columns use a {@code _ci} collation.
	 *
	 * @param  query the free-text query, or {@code null}/blank to not filter
	 * @return       a specification matching every word, unrestricted when the query yields no words
	 */
	public static Specification<PhotoEntity> matches(final String query) {
		final var words = split(query);
		if (words.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.and(words.stream()
			.map(word -> matchesAnyAttribute(root, cb, word))
			.toArray(Predicate[]::new));
	}

	private static Predicate matchesAnyAttribute(final Root<PhotoEntity> root, final CriteriaBuilder cb, final String word) {
		final var pattern = "%" + escapeWildcards(word) + "%";
		return cb.or(SEARCHABLE_ATTRIBUTES.stream()
			.map(attribute -> cb.like(root.<String>get(attribute), pattern, LIKE_ESCAPE))
			.toArray(Predicate[]::new));
	}

	private static List<String> split(final String query) {
		if (query == null || query.isBlank()) {
			return List.of();
		}
		return Arrays.stream(WHITESPACE.split(query.trim()))
			.filter(word -> !word.isEmpty())
			.toList();
	}

	/**
	 * Escapes the {@code LIKE} wildcards {@code %} and {@code _}, plus the escape character itself. Without this, a user
	 * searching for {@code %} would match the entire archive.
	 */
	private static String escapeWildcards(final String word) {
		return LIKE_WILDCARDS.matcher(word).replaceAll(LIKE_ESCAPE + "$1");
	}
}
