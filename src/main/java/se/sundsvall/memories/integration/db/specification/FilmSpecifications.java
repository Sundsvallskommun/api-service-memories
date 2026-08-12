package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static se.sundsvall.memories.integration.db.model.FilmEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.FILM_ID;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.TOPOGRAPHY;

/**
 * Criteria specifications for searching the {@code FILM} table. The predicates themselves are built by
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
public final class FilmSpecifications {

	private static final SpecificationBuilder<FilmEntity> BUILDER = new SpecificationBuilder<>();

	private static final int PUBLISHED_BIT = 4;

	/** Matches the {@code MATCH (DOKTITEL, KOMMENT_FILM)} index the free-text search replaces. */
	private static final List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	private FilmSpecifications() {}

	/** Restricts the result to published rows. */
	public static Specification<FilmEntity> published() {
		return BUILDER.buildBitmaskFilter(OPTIONS, PUBLISHED_BIT);
	}

	/** Excludes soft-deleted rows. Deletion sets {@code DELETEDDATE} but leaves the published bit set. */
	public static Specification<FilmEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	/** Matches a single row by primary key, so reads by id compose from the same filters as a search. */
	public static Specification<FilmEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(FILM_ID, id);
	}

	/** Free-text search across {@code DOKTITEL} and {@code KOMMENT_FILM}. Every word must occur in one of them. */
	public static Specification<FilmEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	/** Fetches the place in the same query, so mapping a page does not fire one select per row. */
	public static Specification<FilmEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
