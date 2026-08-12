package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static se.sundsvall.memories.integration.db.model.FilmEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.FILM_ID;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.TOPOGRAPHY;

/**
 * Criteria specifications for searching the {@code FILM} table.
 *
 * <p>
 * Each factory method returns {@link Specification#unrestricted()} when its filter is not requested, so callers can
 * combine them unconditionally with {@link Specification#allOf}, which does not accept {@code null} elements.
 *
 * <p>
 * <strong>Sorting:</strong> unlike the native queries these replace, a sort property supplied via {@code Pageable} is
 * an entity property (e.g. {@code documentTitle}), not a physical column name.
 */
public final class FilmSpecifications {

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

	/** The columns free-text search covers, matching the {@code MATCH (DOKTITEL, KOMMENT_FILM)} index it replaces. */
	private static final List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	private FilmSpecifications() {}

	/**
	 * Restricts the result to published rows.
	 *
	 * @return a specification matching rows where bit {@code 4} of {@code OPTIONS} is set
	 */
	public static Specification<FilmEntity> published() {
		return (root, _, cb) -> cb.equal(
			cb.function("bitand", Integer.class, root.get(OPTIONS), cb.literal(PUBLISHED_BIT)), PUBLISHED_BIT);
	}

	/**
	 * Excludes soft-deleted rows.
	 *
	 * <p>
	 * Deleting a row sets {@code DELETEDDATE} but leaves the published bit in {@code OPTIONS} set, so this is the only
	 * marker of a deletion and has to be applied everywhere a row is read.
	 *
	 * @return a specification matching rows with no {@code DELETEDDATE}
	 */
	public static Specification<FilmEntity> notDeleted() {
		return (root, _, cb) -> cb.isNull(root.get(DELETED_DATE));
	}

	/**
	 * Matches a single row by primary key, so that reads by id can be composed from the same filters as a search.
	 *
	 * @param  id the film id
	 * @return    a specification matching the given id
	 */
	public static Specification<FilmEntity> hasId(final Integer id) {
		return (root, _, cb) -> cb.equal(root.get(FILM_ID), id);
	}

	/**
	 * Fetches the topography association in the same query, so that mapping a page of results does not fire one
	 * additional select per row.
	 *
	 * <p>
	 * A fetch join is invalid in a count projection, and Spring Data reuses the same specification for both the content
	 * query and the count query, so the fetch is skipped when the result type is {@link Long}. The {@code CriteriaQuery}
	 * is nullable in Spring Data JPA 4 and is treated as "not a count query" when absent.
	 *
	 * @return a specification that adds a left fetch join and no restriction of its own
	 */
	public static Specification<FilmEntity> fetchTopography() {
		return (root, query, cb) -> {
			if (query == null || !Long.class.equals(query.getResultType())) {
				root.fetch(TOPOGRAPHY, JoinType.LEFT);
			}
			return cb.conjunction();
		};
	}

	/**
	 * Free-text search across {@code DOKTITEL} and {@code KOMMENT_FILM}.
	 *
	 * <p>
	 * The query is split on whitespace and <em>every</em> word must occur in at least one of the columns. The words need
	 * not be adjacent, nor occur in the same column — a single raw {@code LIKE} over the whole query would require the
	 * exact word order.
	 *
	 * <p>
	 * Matching is case-insensitive because the columns use a {@code _ci} collation.
	 *
	 * @param  query the free-text query, or {@code null}/blank to not filter
	 * @return       a specification matching every word, unrestricted when the query yields no words
	 */
	public static Specification<FilmEntity> matches(final String query) {
		final var words = split(query);
		if (words.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.and(words.stream()
			.map(word -> matchesAnyAttribute(root, cb, word))
			.toArray(Predicate[]::new));
	}

	private static Predicate matchesAnyAttribute(final Root<FilmEntity> root, final CriteriaBuilder cb, final String word) {
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
