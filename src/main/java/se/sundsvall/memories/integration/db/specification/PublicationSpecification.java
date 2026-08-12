package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static se.sundsvall.memories.integration.db.model.PublicationEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.PUBLICATION_ID;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.TOPOGRAPHY;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.XMLTEXT;

/**
 * Criteria specifications for searching the {@code PUBL} table. The predicates themselves are built by
 * {@link SpecificationBuilder}; this interface only states which attributes each filter applies to.
 *
 * <p>
 * Each factory returns {@link Specification#unrestricted()} when its filter is not requested, so callers can combine
 * them unconditionally.
 *
 * <p>
 * <strong>Sorting:</strong> unlike the native queries these replace, a sort property supplied via {@code Pageable} is
 * an entity property (e.g. {@code documentTitle}), not a physical column name.
 */
public interface PublicationSpecification {

	SpecificationBuilder<PublicationEntity> BUILDER = new SpecificationBuilder<>();

	int PUBLISHED_BIT = 4;

	/**
	 * Matches the {@code MATCH (DOKTITEL, KOMMENT_PUBL, XMLTEXT)} index the free-text search replaces.
	 *
	 * <p>
	 * {@code XMLTEXT} is kept, unlike on TEXT where the column is empty: PUBL holds roughly 68 MB of digitised
	 * publication text across 20 326 rows, and dropping it would silently make anything that only appears in a
	 * publication's body unfindable. The cost is that a {@code LIKE} over a longtext column cannot use an index, so
	 * every free-text search reads that data — where the fulltext index this replaces did not. This is the one place in
	 * the service where the performance question is real rather than theoretical, and it is worth measuring once the
	 * new search is live.
	 */
	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT, XMLTEXT);

	/** Restricts the result to published rows. */
	static Specification<PublicationEntity> published() {
		return BUILDER.buildBitmaskFilter(OPTIONS, PUBLISHED_BIT);
	}

	/** Excludes soft-deleted rows. Deletion sets {@code DELETEDDATE} but leaves the published bit set. */
	static Specification<PublicationEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	/** Matches a single row by primary key, so reads by id compose from the same filters as a search. */
	static Specification<PublicationEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(PUBLICATION_ID, id);
	}

	/** Free-text search across the searchable columns. Every word must occur in at least one of them. */
	static Specification<PublicationEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	/**
	 * Fetches the place in the same query, so mapping a page does not fire one select per row. Only {@code P_T_ID} is
	 * modelled — {@code FORLAG_T_ID} is mapped but read nowhere, and modelling a dead column would add a join for no
	 * caller.
	 */
	static Specification<PublicationEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
