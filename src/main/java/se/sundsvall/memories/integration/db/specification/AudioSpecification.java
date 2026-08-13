package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.AudioEntity;

import static se.sundsvall.memories.integration.db.model.AudioEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.ID;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.SUBJECT;
import static se.sundsvall.memories.integration.db.model.AudioEntity_.TOPOGRAPHY;

public interface AudioSpecification {

	SpecificationBuilder<AudioEntity> BUILDER = new SpecificationBuilder<>();

	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

	static Specification<AudioEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<AudioEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<AudioEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<AudioEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	static Specification<AudioEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}

	static Specification<AudioEntity> fetchSubject() {
		return BUILDER.buildFetchJoin(SUBJECT);
	}
}
