package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static se.sundsvall.memories.integration.db.model.FilmEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.ID;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.FilmEntity_.TOPOGRAPHY;

public interface FilmSpecification {

	SpecificationBuilder<FilmEntity> BUILDER = new SpecificationBuilder<>();

	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT);

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

	static Specification<FilmEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
