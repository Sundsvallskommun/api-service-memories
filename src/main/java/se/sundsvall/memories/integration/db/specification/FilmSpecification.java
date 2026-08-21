package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.FilmEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DATE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.ID;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.TOPOGRAPHY;

public interface FilmSpecification {

	SpecificationBuilder<FilmEntity> BUILDER = new SpecificationBuilder<>();

	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	// A film is dated by a single DATUM, so the period it covers starts and ends on the same attribute.
	List<String> PERIOD_ATTRIBUTES = List.of(DATE);

	static Specification<FilmEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<FilmEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<FilmEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<FilmEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	static Specification<FilmEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	static Specification<FilmEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(PERIOD_ATTRIBUTES, yearFrom);
	}

	static Specification<FilmEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(PERIOD_ATTRIBUTES, yearTo);
	}

	/**
	 * Fetches both originator associations, which the mapper reads a name from on every row. Without this each row
	 * costs two more queries.
	 */
	static Specification<FilmEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<FilmEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
