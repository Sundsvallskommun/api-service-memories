package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.TextEntity;

import static se.sundsvall.memories.integration.db.model.TextEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.TextEntity_.ID;
import static se.sundsvall.memories.integration.db.model.TextEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.TextEntity_.SUBJECT;
import static se.sundsvall.memories.integration.db.model.TextEntity_.TOPOGRAPHY;

public interface TextSpecification {

	SpecificationBuilder<TextEntity> BUILDER = new SpecificationBuilder<>();

	// The native query this replaces also searched XMLTEXT. Measured against production the column holds zero bytes
	// across all 3 942 rows, so including it would match nothing while making every search scan a longtext column.
	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

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

	static Specification<TextEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	static Specification<TextEntity> fetchSubject() {
		return BUILDER.buildFetchJoin(SUBJECT);
	}
}
