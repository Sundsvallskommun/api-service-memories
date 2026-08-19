package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity_;

import static se.sundsvall.memories.integration.db.model.TextEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.TextEntity_.CREATOR_PERSON;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DOCUMENT_DATE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DOCUMENT_END_DATE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.ID;
import static se.sundsvall.memories.integration.db.model.TextEntity_.LOCATION_TEXT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.TextEntity_.SUBJECT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.TOPOGRAPHY;

public interface TextSpecification {

	SpecificationBuilder<TextEntity> BUILDER = new SpecificationBuilder<>();

	// The native query this replaces also searched XMLTEXT. Measured against production the column holds zero bytes
	// across all 3 942 rows, so including it would match nothing while making every search scan a longtext column.
	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	List<String> LOCATION_ATTRIBUTES = List.of(TopographyEntity_.NAME, TopographyEntity_.PLACE);

	// A text covers a period: it starts at DOKDATUM and ends at DOKDATUM_SLUT, which falls back to DOKDATUM when the
	// period is a single point in time.
	List<String> PERIOD_END_ATTRIBUTES = List.of(DOCUMENT_END_DATE, DOCUMENT_DATE);

	List<String> PERIOD_START_ATTRIBUTES = List.of(DOCUMENT_DATE);

	static Specification<TextEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<TextEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<TextEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<TextEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	static Specification<TextEntity> matchesLocation(final String location) {
		return BUILDER.buildLocationFilter(TOPOGRAPHY, LOCATION_ATTRIBUTES, LOCATION_TEXT, location);
	}

	// A text whose period ends before the requested range falls outside it.
	static Specification<TextEntity> yearAtLeast(final Integer yearFrom) {
		return BUILDER.buildYearAtLeastFilter(PERIOD_END_ATTRIBUTES, yearFrom);
	}

	// A text whose period starts after the requested range falls outside it.
	static Specification<TextEntity> yearAtMost(final Integer yearTo) {
		return BUILDER.buildYearAtMostFilter(PERIOD_START_ATTRIBUTES, yearTo);
	}

	/**
	 * Fetches both originator associations, which the mapper reads a name from on every row. Without this each row
	 * costs two more queries.
	 */
	static Specification<TextEntity> fetchCreators() {
		return BUILDER.buildFetchJoin(CREATOR_PERSON)
			.and(BUILDER.buildFetchJoin(CREATOR_LEGAL_ENTITY));
	}

	static Specification<TextEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	static Specification<TextEntity> fetchSubject() {
		return BUILDER.buildFetchJoin(SUBJECT);
	}
}
