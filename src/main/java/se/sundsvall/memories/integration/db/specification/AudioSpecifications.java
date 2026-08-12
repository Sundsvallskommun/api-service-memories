package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.AudioEntity;

import static se.sundsvall.memories.integration.db.model.AudioEntity_.AUDIO_ID;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.TOPOGRAPHY;

/**
 * Criteria specifications for searching the {@code LJUD} table. The predicates themselves are built by
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
public final class AudioSpecifications {

	private static final SpecificationBuilder<AudioEntity> BUILDER = new SpecificationBuilder<>();

	private static final int PUBLISHED_BIT = 4;

	/** Matches the {@code MATCH (DOKTITEL, KOMMENT_LJUD)} index the free-text search replaces. */
	private static final List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	private AudioSpecifications() {}

	/** Restricts the result to published rows. */
	public static Specification<AudioEntity> published() {
		return BUILDER.buildBitmaskFilter(OPTIONS, PUBLISHED_BIT);
	}

	/** Excludes soft-deleted rows. Deletion sets {@code DELETEDDATE} but leaves the published bit set. */
	public static Specification<AudioEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	/** Matches a single row by primary key, so reads by id compose from the same filters as a search. */
	public static Specification<AudioEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(AUDIO_ID, id);
	}

	/** Free-text search across {@code DOKTITEL} and {@code KOMMENT_LJUD}. Every word must occur in one of them. */
	public static Specification<AudioEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	/** Fetches the place in the same query, so mapping a page does not fire one select per row. */
	public static Specification<AudioEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
