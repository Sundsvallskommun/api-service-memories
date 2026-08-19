package se.sundsvall.memories.integration.db.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity_;

import static se.sundsvall.memories.integration.db.model.NodeEntity_.DESCRIPTION;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.NAME;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.NODE_TYPE;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.OPTIONS;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.START_YEAR;
import static se.sundsvall.memories.integration.db.model.NodeEntity_.STOP_YEAR;

public interface NodeSpecification {

	SpecificationBuilder<NodeEntity> BUILDER = new SpecificationBuilder<>();

	/**
	 * The free text has to reach the description as well as the name: a series is often findable only through what its
	 * description says it contains.
	 */
	static Specification<NodeEntity> matches(final String query) {
		return BUILDER.buildLikeAnyFilter(List.of(NAME, DESCRIPTION), query);
	}

	static Specification<NodeEntity> hasNodeType(final Integer nodeTypeId) {
		return BUILDER.buildAssociationEqualFilter(NODE_TYPE, NodeTypeEntity_.ID, nodeTypeId);
	}

	/**
	 * Keeps nodes whose period overlaps the requested range. A node without a stop year has not ended, so it runs into
	 * every range that starts later.
	 */
	static Specification<NodeEntity> activeFrom(final Integer yearFrom) {
		return BUILDER.buildNumberAtLeastOrOpenFilter(STOP_YEAR, yearFrom);
	}

	static Specification<NodeEntity> activeUntil(final Integer yearTo) {
		return BUILDER.buildNumberAtMostOrOpenFilter(START_YEAR, yearTo);
	}

	static Specification<NodeEntity> published() {
		return BUILDER.buildPublishedFilter(OPTIONS);
	}

	static Specification<NodeEntity> fetchNodeType() {
		return BUILDER.buildFetchJoin(NODE_TYPE);
	}
}
