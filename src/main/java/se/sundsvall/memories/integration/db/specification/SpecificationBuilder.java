package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.FullTextFunctionContributor;
import se.sundsvall.memories.integration.db.FullTextIndexes;

public class SpecificationBuilder<T> {

	/** What a composed place label puts between its parts, matching {@code TopographyEntity.getDisplayName()}. */
	private static final String LABEL_SEPARATOR = ", ";

	private static final char LIKE_ESCAPE = '!';

	private static final int PUBLISHED_BIT = 4;

	private static final int YEAR_LENGTH = 4;

	private static final int RELEVANCE_EXACT_NAME = 0;
	private static final int RELEVANCE_NAME_PREFIX = 1;
	private static final int RELEVANCE_ALL_WORDS_IN_NAME = 2;
	private static final int RELEVANCE_ANY_WORD_IN_NAME = 3;
	private static final int RELEVANCE_BODY_ONLY = 4;

	/**
	 * InnoDB refuses to index a token shorter than {@code innodb_ft_min_token_size} (three by default), so a shorter
	 * word can never match through the index however the query is written.
	 */
	private static final int MIN_TOKEN_LENGTH = 3;

	/**
	 * What InnoDB treats as a word boundary — everything that is not a letter or a digit. Splitting on this is how the
	 * query is broken into the same tokens the index actually holds, rather than into whitespace-separated words:
	 * {@code Anna-Lisa} is two tokens to the index, and {@code S:t} is one unusable one. The underscore is kept —
	 * InnoDB's default parser counts it as part of a word, so {@code fil_namn} is one token and does not match
	 * {@code filXnamn}.
	 */
	private static final Pattern TOKEN_BOUNDARY = Pattern.compile("[^\\p{L}\\p{N}_]+");

	/**
	 * InnoDB's default stopword list ({@code innodb_ft_default_stopword}). A stopword is not in the index, so requiring
	 * it with {@code +} makes the whole expression unsatisfiable however long the word is — {@code www.sundsvall.se}
	 * would otherwise return nothing at all.
	 */
	private static final Set<String> STOPWORDS = Set.of(
		"a", "about", "an", "are", "as", "at", "be", "by", "com", "de", "en", "for", "from", "how", "i", "in", "is", "it", "la",
		"of", "on", "or", "that", "the", "this", "to", "und", "was", "what", "when", "where", "who", "will", "with", "www");

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
		final var lowerCased = value.trim().toLowerCase(Locale.ROOT);
		return (root, _, cb) -> cb.equal(cb.lower(root.get(attribute)), lowerCased);
	}

	/**
	 * Matches rows where the attribute equals the value regardless of case, and every row where the attribute is
	 * {@code NULL} — a filter over a facet only some rows carry. The rows that carry none are left untouched rather
	 * than excluded, so selecting a value narrows the rows it can speak for and no others. Matches every row when the
	 * value is blank.
	 */
	public Specification<T> buildEqualIgnoreCaseOrMissingFilter(final String attribute, final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		final var lowerCased = value.trim().toLowerCase(Locale.ROOT);
		return (root, _, cb) -> cb.or(cb.isNull(root.get(attribute)), cb.equal(cb.lower(root.get(attribute)), lowerCased));
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
	 * Matches rows whose attribute is one of the ids, which are alternatives. Nulls are dropped, so an empty selection
	 * matches every row. The id counterpart of {@link #buildInFilter(String, List)}, which is for text.
	 */
	public Specification<T> buildIdInFilter(final String attribute, final List<?> values) {
		final var wanted = distinctNonNull(values);
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, _) -> root.get(attribute).in(wanted);
	}

	/**
	 * Matches rows whose value, lower-cased, is one of the given alternatives. The alternatives are trimmed, blank ones
	 * dropped and the rest lower-cased and deduplicated before the comparison, so {@code " Man "} matches a stored
	 * {@code man}. Matches every row when no alternative remains.
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
	 * Matches rows whose value, lower-cased, is none of the given alternatives — including the rows where it is
	 * {@code NULL}, which SQL's {@code NOT IN} would leave out on its own. The alternatives are normalised as in
	 * {@link #buildInIgnoreCaseFilter(String, List)}. Matches every row when no alternative remains.
	 */
	public Specification<T> buildNotInIgnoreCaseFilter(final String attribute, final List<String> values) {
		final var unwanted = distinctNonBlank(values).stream()
			.map(value -> value.toLowerCase(Locale.ROOT))
			.distinct()
			.toList();
		if (unwanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> cb.or(cb.isNull(root.get(attribute)), cb.not(cb.lower(root.get(attribute)).in(unwanted)));
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

	/**
	 * Matches rows where the value occurs in one of the entity's own attributes, or in an attribute of one of the
	 * guarded associations reached through {@code association} — a lookup row of the entity's that in turn names other
	 * rows. The archive nodes need this: a node named after its arkivbildare has an empty name of its own, and the
	 * name is found one row further in. The associations are guarded as in {@link #buildAssociationLikeAnyFilter}.
	 * Matches every row when the value is blank.
	 */
	public Specification<T> buildLikeAnyFilter(final List<String> attributes, final String association, final List<AssociationAttributes> nestedAssociations,
		final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		final var trimmed = value.trim();
		final var pattern = "%" + escapeWildcards(trimmed) + "%";
		return (root, _, cb) -> {
			final var holder = reuseFetchOrJoin(root, association);
			final var nested = nestedAssociations.stream()
				.map(nestedAssociation -> matchesAssociation(holder, cb, nestedAssociation, pattern));
			return cb.or(Stream.concat(Stream.of(matchesAnyAttribute(root, cb, attributes, trimmed)), nested)
				.toArray(Predicate[]::new));
		};
	}

	private Predicate matchesAssociation(final From<?, ?> from, final CriteriaBuilder cb, final AssociationAttributes association, final String pattern) {
		final var join = reuseFetchOrJoin(from, association.association());
		final var matches = association.attributeGroups().stream()
			.map(group -> cb.like(joined(cb, join, group), pattern, LIKE_ESCAPE));
		return cb.and(
			isRealRow(cb, join, association.guard()),
			cb.or(matches.toArray(Predicate[]::new)));
	}

	/**
	 * Matches rows whose guarded association points, through a second association, at one of the given ids, which are
	 * alternatives. The first association is guarded the way {@link #buildAssociationLikeAnyFilter} guards it — its
	 * sentinel row never matches and neither does a soft-deleted one — and the second has a sentinel of its own that is
	 * never a match either, so naming it alone matches nothing rather than every row defaulted to it. Only the foreign
	 * key of the second association is read, so the filter adds no second join. Matches every row when the list yields
	 * no ids.
	 */
	public Specification<T> buildNestedAssociationInFilter(final GuardedAssociation guard, final String nestedAssociation, final String nestedAttribute,
		final Object nestedPlaceholderId, final List<?> values) {
		final var wanted = distinctNonNull(values);
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, cb) -> {
			final var join = reuseFetchOrJoin(root, guard.association());
			final var nested = join.get(nestedAssociation).get(nestedAttribute);
			return cb.and(isRealRow(cb, join, guard), cb.notEqual(nested, nestedPlaceholderId), nested.in(wanted));
		};
	}

	/**
	 * Matches rows whose guarded association points at a real row that in turn points, through a second association, at
	 * something other than that association's sentinel — the rows a counter over the second association can group,
	 * matched by the same guards as {@link #buildNestedAssociationInFilter}, so the two cannot disagree.
	 */
	public Specification<T> buildNestedAssociationPresentFilter(final GuardedAssociation guard, final String nestedAssociation, final String nestedAttribute,
		final Object nestedPlaceholderId) {
		return (root, _, cb) -> {
			final var join = reuseFetchOrJoin(root, guard.association());
			final var nested = join.get(nestedAssociation).get(nestedAttribute);
			return cb.and(isRealRow(cb, join, guard), cb.isNotNull(nested), cb.notEqual(nested, nestedPlaceholderId));
		};
	}

	/** The associated row is a real one: not the sentinel the foreign key defaults to, and not soft-deleted. */
	private Predicate isRealRow(final CriteriaBuilder cb, final Join<?, ?> join, final GuardedAssociation guard) {
		return cb.and(
			cb.notEqual(join.get(guard.idAttribute()), guard.placeholderId()),
			cb.isNull(join.get(guard.deletedAttribute())));
	}

	/**
	 * An association together with the sentinel row that never counts as a match and the attribute that marks a row as
	 * soft-deleted. The legacy foreign keys default to a placeholder rather than to {@code NULL}, so every filter through
	 * such an association has to skip it.
	 *
	 * @param association      name of the association attribute
	 * @param idAttribute      name of the associated entity's id attribute
	 * @param placeholderId    id of the sentinel row
	 * @param deletedAttribute name of the associated entity's soft-delete attribute, which must be null to match
	 */
	public record GuardedAssociation(String association, String idAttribute, Object placeholderId, String deletedAttribute) {}

	/**
	 * The attributes of one group as a single space-separated string, so that a value spanning them still matches: a
	 * person's name lives in two columns, and a search for "Anton Nordin" is in neither of them on its own. A missing
	 * attribute reads as empty rather than turning the whole expression into {@code NULL}.
	 */
	private Expression<String> joined(final CriteriaBuilder cb, final Join<?, ?> join, final List<String> attributes) {
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
	public record AssociationAttributes(String association, List<List<String>> attributeGroups, String idAttribute, Object placeholderId, String deletedAttribute) {

		/** The same, over a {@link GuardedAssociation} a caller already holds for other filters through the association. */
		public AssociationAttributes(final GuardedAssociation guard, final List<List<String>> attributeGroups) {
			this(guard.association(), attributeGroups, guard.idAttribute(), guard.placeholderId(), guard.deletedAttribute());
		}

		/** The guards of this association, for the filters that match on its ids rather than its text. */
		public GuardedAssociation guard() {
			return new GuardedAssociation(association, idAttribute, placeholderId, deletedAttribute);
		}
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
	 * Matches rows of one kind whose body text lives on another entity rather than on this one, without selecting that
	 * body: the words are tested inside an {@code EXISTS} subquery, correlated on the id.
	 * <p>
	 * The combined search needs this for the digitised page of a publication. Concatenating it into the view's
	 * {@code SEARCH_TEXT} would have been simpler, but that column is mapped and selected with every row, so each page
	 * of results would drag tens of megabytes of scanned text onto the heap for a value no response field reads.
	 * <p>
	 * Matches nothing when the query yields no words — the caller combines this with {@code or}, where an
	 * unrestricted branch would match every row instead.
	 */
	public <E> Specification<T> buildRelatedTextFilter(final Class<E> relatedType, final String relatedIdAttribute,
		final String relatedTextAttribute, final String idAttribute, final String discriminatorAttribute,
		final String discriminatorValue, final String query) {
		final var words = splitWords(query);
		if (words.isEmpty()) {
			return (_, _, cb) -> cb.disjunction();
		}
		return (root, criteriaQuery, cb) -> {
			if (criteriaQuery == null) {
				return cb.disjunction();
			}
			final var subquery = criteriaQuery.subquery(Integer.class);
			final var related = subquery.from(relatedType);
			final var matchesEveryWord = words.stream()
				.map(word -> cb.like(related.<String>get(relatedTextAttribute), "%" + escapeWildcards(word) + "%", LIKE_ESCAPE))
				.toArray(Predicate[]::new);
			subquery.select(cb.literal(1))
				.where(cb.and(cb.equal(related.get(relatedIdAttribute), root.get(idAttribute)), cb.and(matchesEveryWord)));
			return cb.and(cb.equal(root.get(discriminatorAttribute), discriminatorValue), cb.exists(subquery));
		};
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
	 * Matches rows through the {@code FULLTEXT} index over the given attributes, requiring every word of the query and
	 * allowing each to match as a prefix — the semantics the legacy search had. The attributes must be exactly the
	 * column list of an existing index, in any order: MariaDB answers a partial list with error 1191 rather than
	 * falling back to a scan.
	 * <p>
	 * Unlike {@link #buildLikeAllWordsFilter} this matches whole words, so {@code olycka} no longer finds
	 * {@code drunkningsolycka}. That is the price of using the index, and it is what makes the search able to read the
	 * digitised document bodies at all.
	 * <p>
	 * Falls back to {@code LIKE} when any word is too short to have been indexed, when a word is a stopword, or when
	 * the database has no index over {@code indexColumns} of {@code indexTable} — the deployed legacy tables are not
	 * built by this repository's migrations, and {@code MATCH} answers a column list it cannot pair with an index
	 * with error 1191 rather than with a scan.
	 */
	public Specification<T> buildFullTextFilter(final List<String> attributes, final String indexTable, final List<String> indexColumns,
		final String query) {
		final var expression = booleanModeExpression(query);
		if (expression.isEmpty() || !FullTextIndexes.covers(indexTable, indexColumns)) {
			return buildLikeAllWordsFilter(attributes, query);
		}
		return (root, _, cb) -> fullTextMatches(root, cb, attributes, expression.get());
	}

	/**
	 * Matches rows through the {@code FULLTEXT} index over the entity's own attributes, or, as
	 * {@link #buildLikeAnyFilter(List, String, List, String)} does, in an attribute of one of the guarded associations
	 * reached through {@code association}. The associated names live in other tables and other indexes, so they cannot
	 * join the same {@code MATCH} and keep matching with {@code LIKE}.
	 * <p>
	 * Falls back wholesale to {@code LIKE} when the query is too short to have been indexed, so that both halves keep
	 * answering the same way.
	 */
	public Specification<T> buildFullTextOrAssociationFilter(final List<String> attributes, final String indexTable,
		final List<String> indexColumns, final String association, final List<AssociationAttributes> nestedAssociations, final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		final var expression = booleanModeExpression(value);
		if (expression.isEmpty() || !FullTextIndexes.covers(indexTable, indexColumns)) {
			return buildLikeAnyFilter(attributes, association, nestedAssociations, value);
		}
		final var pattern = "%" + escapeWildcards(value.trim()) + "%";
		return (root, _, cb) -> {
			final var holder = reuseFetchOrJoin(root, association);
			final var nested = nestedAssociations.stream()
				.map(nestedAssociation -> matchesAssociation(holder, cb, nestedAssociation, pattern));
			return cb.or(Stream.concat(Stream.of(fullTextMatches(root, cb, attributes, expression.get())), nested)
				.toArray(Predicate[]::new));
		};
	}

	/**
	 * The query as a boolean-mode expression requiring every word and allowing each to match as a prefix, or empty
	 * when the caller should use {@code LIKE} instead — either because there is nothing to search for, or because a
	 * word is shorter than the index stores and would therefore match nothing.
	 */
	private static Optional<String> booleanModeExpression(final String query) {
		if (query == null || query.isBlank()) {
			return Optional.empty();
		}
		final var tokens = Arrays.stream(TOKEN_BOUNDARY.split(query.trim()))
			.filter(token -> !token.isEmpty())
			.toList();
		if (tokens.isEmpty() || tokens.stream().anyMatch(SpecificationBuilder::unsearchable)) {
			return Optional.empty();
		}
		return Optional.of(String.join(" ", tokens.stream().map("+%s*"::formatted).toList()));
	}

	/**
	 * Whether the index can answer for this token at all. Two ways it cannot: the token is shorter than
	 * {@code innodb_ft_min_token_size} and was never stored, or it is a stopword and was deliberately dropped. Either
	 * way {@code +token*} matches nothing and takes the whole {@code AND} down with it, so the caller has to use
	 * {@code LIKE} for the entire query rather than return an empty page.
	 */
	private static boolean unsearchable(final String token) {
		return token.length() < MIN_TOKEN_LENGTH || STOPWORDS.contains(token.toLowerCase(Locale.ROOT));
	}

	private Predicate fullTextMatches(final Root<T> root, final CriteriaBuilder cb, final List<String> attributes, final String expression) {
		final var function = FullTextFunctionContributor.FUNCTION_PREFIX + attributes.size();
		return cb.gt(cb.function(function, Double.class, matchArguments(root, cb, attributes, expression)), 0d);
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
	 * As {@link #buildLocationFilter}, for an entity whose place lives one association further in: the topography and
	 * the free-text place are both attributes of the row {@code association} points at. Every join is a left join, so
	 * a row without that lookup row, or with one that names no place, simply does not match. Matches every row when the
	 * location is blank.
	 */
	public Specification<T> buildNestedLocationFilter(final String association, final String nestedAssociation, final List<String> nestedAttributes,
		final String textAttribute, final String location) {
		if (location == null || location.isBlank()) {
			return Specification.unrestricted();
		}
		final var pattern = "%" + escapeWildcards(location.trim()) + "%";
		return (root, _, cb) -> {
			final var holder = reuseFetchOrJoin(root, association);
			final var topography = reuseFetchOrJoin(holder, nestedAssociation);
			final var matches = Stream.concat(
				nestedAttributes.stream().map(attribute -> cb.like(topography.<String>get(attribute), pattern, LIKE_ESCAPE)),
				Stream.of(cb.like(holder.<String>get(textAttribute), pattern, LIKE_ESCAPE)));
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
	 * Matches rows whose association's attribute equals the value regardless of case — a lookup row matched by its
	 * name rather than its id. Reuses a fetch or join of the association already in the query. Matches every row when
	 * the value is blank.
	 */
	public Specification<T> buildAssociationEqualIgnoreCaseFilter(final String association, final String attribute, final String value) {
		if (value == null || value.isBlank()) {
			return Specification.unrestricted();
		}
		final var lowerCased = value.trim().toLowerCase(Locale.ROOT);
		return (root, _, cb) -> cb.equal(cb.lower(reuseFetchOrJoin(root, association).get(attribute)), lowerCased);
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
	 * As {@link #buildAssociationEqualFilter(String, String, Object)}, for several ids that are alternatives. Named apart
	 * from {@link #buildAssociationInFilter} because it applies <em>no</em> soft-delete guard: use it only for a lookup
	 * table whose rows are never soft-deleted, so that the choice is a decision rather than the shorter overload. Reads
	 * the foreign key only, so it adds no join. Matches every row when the list yields no ids.
	 */
	public Specification<T> buildLookupInFilter(final String association, final String attribute, final List<?> values) {
		final var wanted = distinctNonNull(values);
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, _) -> root.get(association).get(attribute).in(wanted);
	}

	/**
	 * As {@link #buildLookupInFilter}, one association further in: the rows whose lookup row, reached through
	 * {@code association}, points through a second association at one of the ids. The first association reuses a fetch
	 * or join already in the query, or is left-joined; only the foreign key of the second is read, so it adds no join of
	 * its own. Applies no soft-delete guard, like the filter it extends, so it is for lookup tables only. Matches every
	 * row when the list yields no ids.
	 */
	public Specification<T> buildNestedLookupInFilter(final String association, final String nestedAssociation, final String attribute, final List<?> values) {
		final var wanted = distinctNonNull(values);
		if (wanted.isEmpty()) {
			return Specification.unrestricted();
		}
		return (root, _, _) -> reuseFetchOrJoin(root, association).get(nestedAssociation).get(attribute).in(wanted);
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
	 * joined in the given order, the way the place is shown. The registers fill only the free text and the objects
	 * often only the association, so without the fallback either kind would clump at one end of the order. Blank values
	 * count as absent, so a row carrying only one of the attributes sorts on that one alone — the same string the
	 * response labels it with, which is the point: a list cannot sort on one name and show another.
	 */
	public Expression<String> location(final Root<T> root, final CriteriaBuilder cb, final String textAttribute, final String association,
		final List<String> associationAttributes) {
		final var join = reuseFetchOrJoin(root, association);
		final Stream<Expression<?>> parts = Stream.<Expression<?>>concat(
			Stream.of(cb.literal(LABEL_SEPARATOR)),
			associationAttributes.stream().map(attribute -> cb.nullif(cb.trim(join.<String>get(attribute)), "")));
		// CONCAT_WS drops the blank parts and returns an empty string when every one of them is blank, which the
		// coalesce then reads as absent, the same as a row with no association at all.
		final var label = cb.function("concat_ws", String.class, parts.toArray(Expression[]::new));
		final var coalesce = cb.<String>coalesce();
		coalesce.value(cb.nullif(cb.trim(root.<String>get(textAttribute)), ""));
		coalesce.value(cb.nullif(label, ""));
		return coalesce;
	}

	/**
	 * The first non-blank of the attributes, in the given order, or {@code NULL} when all are blank — the display name
	 * of a lookup row, computed in the database so it can be filtered on. Blank values count as absent, since the legacy
	 * data uses empty strings rather than {@code NULL}.
	 */
	private Expression<String> firstNonBlank(final Root<T> root, final CriteriaBuilder cb, final List<String> attributes) {
		return firstNonBlank(cb, attributes.stream().map(root::<String>get));
	}

	/**
	 * The shared shape of the two above: coalesce over the values with every blank one read as absent. Trimmed first, so
	 * that a value padded to a fixed width counts as blank whatever the column's collation does with trailing spaces —
	 * a {@code NO PAD} one does not treat {@code '  '} as equal to {@code ''}.
	 */
	private Expression<String> firstNonBlank(final CriteriaBuilder cb, final Stream<Expression<String>> values) {
		final var coalesce = cb.<String>coalesce();
		values.forEach(value -> coalesce.value(cb.nullif(cb.trim(value), "")));
		return coalesce;
	}

	/**
	 * Matches rows where at least one of the attributes is non-blank — the rows that have a name to show. A lookup
	 * table's sentinel row is usually blank in every column, so this is also what keeps it out of a dropdown.
	 */
	public Specification<T> buildAnyNonBlankFilter(final List<String> attributes) {
		return (root, _, cb) -> cb.isNotNull(firstNonBlank(root, cb, attributes));
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
	 * Left-fetches an association and, through it, the given nested ones, so a row's lookup row and the rows it names
	 * arrive in the one query. Reuses a fetch of the association already in the query rather than adding a second.
	 * Skipped for the derived count query, like {@link #buildFetchJoin}.
	 */
	public Specification<T> buildNestedFetchJoin(final String association, final List<String> nestedAssociations) {
		return (root, query, cb) -> {
			if (query == null || !Long.class.equals(query.getResultType())) {
				final var fetch = reuseFetch(root, association);
				nestedAssociations.forEach(nested -> fetch.fetch(nested, JoinType.LEFT));
			}
			return cb.conjunction();
		};
	}

	/** The fetch of the association already in the query, or a new left fetch of it. */
	private Fetch<?, ?> reuseFetch(final Root<T> root, final String association) {
		return root.getFetches().stream()
			.filter(fetch -> fetch.getAttribute().getName().equals(association))
			.map(fetch -> (Fetch<?, ?>) fetch)
			.findFirst()
			.orElseGet(() -> root.fetch(association, JoinType.LEFT));
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
		final var value = query.trim().toLowerCase(Locale.ROOT);
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
			.map(word -> cb.like(name, "%" + escapeWildcards(word.toLowerCase(Locale.ROOT)) + "%", LIKE_ESCAPE))
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
	 * implements both. There is no fetch in the count queries, where a plain join is reused the same way — the counter
	 * that groups on the association creates it first, and every filter through the association then shares it —
	 * and created when there is none. Works from the root and from a join alike, so a lookup row's own associations
	 * are reused the same way.
	 */
	private Join<?, ?> reuseFetchOrJoin(final From<?, ?> from, final String association) {
		final var fetched = from.getFetches().stream()
			.filter(fetch -> fetch.getAttribute().getName().equals(association))
			.filter(Join.class::isInstance)
			.map(fetch -> (Join<?, ?>) fetch);
		final var joined = from.getJoins().stream()
			.filter(join -> join.getAttribute().getName().equals(association))
			.map(join -> (Join<?, ?>) join);
		return Stream.concat(fetched, joined)
			.findFirst()
			.orElseGet(() -> from.join(association, JoinType.LEFT));
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

	/** The matched columns followed by the search expression, which is what the registered pattern function expects. */
	private Expression<?>[] matchArguments(final Root<T> root, final CriteriaBuilder cb, final List<String> attributes, final String expression) {
		return Stream.concat(
			attributes.stream().map(attribute -> (Expression<?>) root.get(attribute)),
			Stream.<Expression<?>>of(cb.literal(expression)))
			.toArray(Expression[]::new);
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
