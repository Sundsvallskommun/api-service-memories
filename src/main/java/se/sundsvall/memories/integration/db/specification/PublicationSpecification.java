package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static se.sundsvall.memories.integration.db.model.PublicationEntity_.COMMENT;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DELETED_DATE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.DOCUMENT_TITLE;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.ID;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.TOPOGRAPHY;
import static se.sundsvall.memories.integration.db.model.PublicationEntity_.XMLTEXT;

public interface PublicationSpecification {

	SpecificationBuilder<PublicationEntity> BUILDER = new SpecificationBuilder<>();

	// XMLTEXT is searched here but not on TEXT, where the column is empty. PUBL holds roughly 68 MB of digitised text
	// across 20 326 rows, and a LIKE over a longtext column cannot use an index — worth measuring once this is live.
	List<String> SEARCHABLE_ATTRIBUTES = List.of(DOCUMENT_TITLE, COMMENT, XMLTEXT);

	static Specification<PublicationEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	// Deletion sets DELETEDDATE but leaves the published bit set, so published() alone does not hide the row.
	static Specification<PublicationEntity> notDeleted() {
		return BUILDER.buildIsNullFilter(DELETED_DATE);
	}

	static Specification<PublicationEntity> hasId(final Integer id) {
		return BUILDER.buildEqualFilter(ID, id);
	}

	static Specification<PublicationEntity> matches(final String query) {
		return BUILDER.buildLikeAllWordsFilter(SEARCHABLE_ATTRIBUTES, query);
	}

	// Only P_T_ID is modelled — FORLAG_T_ID is mapped but read nowhere.
	static Specification<PublicationEntity> fetchTopography() {
		return BUILDER.buildFetchJoin(TOPOGRAPHY);
	}
}
