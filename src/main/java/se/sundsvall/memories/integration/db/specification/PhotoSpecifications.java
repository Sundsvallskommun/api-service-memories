package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

import static se.sundsvall.memories.integration.db.model.PhotoEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.PHOTO_ID;
import static se.sundsvall.memories.integration.db.model.PhotoEntity_.TOPOGRAPHY;

/**
 * Criteria specifications for searching the {@code FOTO} table. The predicates themselves are built by
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
public final class PhotoSpecifications {

	private static final SpecificationBuilder<PhotoEntity> BUILDER = new SpecificationBuilder<>();

	private static final int PUBLISHED_BIT = 4;

	/** Matches the {@code MATCH (DOKTITEL, KOMMENT_FF)} index the free-text search replaces. */
	private static final List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	private PhotoSpecifications() {}

	/** Restricts the result to published rows. */
	public static Specification<PhotoEntity> published() {
		return BUILDER.buildBitmaskFilter(OPTIONS, PUBLISHED_BIT);
	}

	/** Excludes soft-deleted rows. Deletion sets {@code DELETEDDATE} but leaves the published bit set. */
	public static Specification<PhotoEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	/** Matches a single row by primary key, so reads by id compose from the same filters as a search. */
	public static Specification<PhotoEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(PHOTO_ID, id);
	}

	/** Filters on the {@code OBJTYP} column (e.g. {@code Foto} or {@code Föremål}). */
	public static Specification<PhotoEntity> hasObjectType(final String objectType) {
		return BUILDER.buildEqualFilter(OBJECT_TYPE, objectType);
	}

	/** Free-text search across {@code DOKTITEL} and {@code KOMMENT_FF}. Every word must occur in one of them. */
	public static Specification<PhotoEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	/** Fetches the place in the same query, so mapping a page does not fire one select per row. */
	public static Specification<PhotoEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
