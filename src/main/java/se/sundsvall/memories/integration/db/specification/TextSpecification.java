package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.TextEntity;

import static se.sundsvall.memories.integration.db.model.TextEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.TextEntity_.SUBJECT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.TEXT_ID;
import static se.sundsvall.memories.integration.db.model.TextEntity_.TOPOGRAPHY;

/**
 * Criteria specifications for searching the {@code TEXT} table. The predicates themselves are built by
 * {@link SpecificationBuilder}; this class only states which attributes each filter applies to.
 *
 * <p>
 * Each factory returns {@link Specification#unrestricted()} when its filter is not requested, so callers can combine
 * them unconditionally with {@link Specification#allOf}, which does not accept {@code null} elements.
 *
 * <p>
 * <strong>Sorting:</strong> unlike the native queries these replace, a sort property supplied via {@code Pageable} is
 * an entity property (e.g. {@code documentTitle}), not a physical column name.
 */
public interface TextSpecification {

	SpecificationBuilder<TextEntity> BUILDER = new SpecificationBuilder<>();

	int PUBLISHED_BIT = 4;

	/**
	 * The native query this replaces searched {@code MATCH (DOKTITEL, KOMMENT_DOC, XMLTEXT)}. {@code XMLTEXT} is
	 * excluded here: measured against production it holds zero bytes across all 3 942 rows, so including it would match
	 * nothing while making every search do an unindexable {@code LIKE} over a longtext column. Revisit this if the
	 * digitised document text is ever loaded — at that point the volume decides whether {@code LIKE} is still viable.
	 */
	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	/** Restricts the result to published rows. */
	static Specification<TextEntity> published() {
		return BUILDER.buildBitmaskFilter(OPTIONS, PUBLISHED_BIT);
	}

	/** Excludes soft-deleted rows. Deletion sets {@code DELETEDDATE} but leaves the published bit set. */
	static Specification<TextEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	/** Matches a single row by primary key, so reads by id compose from the same filters as a search. */
	static Specification<TextEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(TEXT_ID, id);
	}

	/** Free-text search across {@code DOKTITEL} and {@code KOMMENT_DOC}. Every word must occur in one of them. */
	static Specification<TextEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	/** Fetches the place in the same query, so mapping a page does not fire one select per row. */
	static Specification<TextEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	/** Fetches the subject in the same query, for the same reason as {@link #fetchTopography()}. */
	static Specification<TextEntity> fetchSubject() {
		return BUILDER.buildFetchJoin(SUBJECT);
	}
}
