package se.sundsvall.memories.integration.db;

import jakarta.persistence.EntityManager;
import java.util.List;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filtersExcludingObjectType;

class CombinedObjectRepositoryCustomImpl implements CombinedObjectRepositoryCustom {

	private static final String OBJECT_TYPE_ALIAS = "objectType";
	private static final String TOTAL_ALIAS = "total";

	private final EntityManager entityManager;

	CombinedObjectRepositoryCustomImpl(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<TypeCount> countByType(final CombinedObjectParameters parameters) {
		final var cb = entityManager.getCriteriaBuilder();
		final var query = cb.createTupleQuery();
		final var root = query.from(CombinedObjectEntity.class);
		final var objectType = root.<String>get(OBJECT_TYPE);

		// Every filter but the type selection: a chip counts the rows that type would return if it were selected, so
		// it must not be narrowed by the selection it is the control for. A specification that restricts nothing
		// yields no predicate at all, which is not the same thing as one that restricts everything — hence the
		// fallback rather than a bare where(null).
		final var predicate = ofNullable(filtersExcludingObjectType(parameters).toPredicate(root, query, cb))
			.orElseGet(cb::conjunction);

		query.multiselect(objectType.alias(OBJECT_TYPE_ALIAS), cb.count(root).alias(TOTAL_ALIAS))
			.where(predicate)
			.groupBy(objectType)
			.orderBy(cb.asc(objectType));

		return entityManager.createQuery(query).getResultList().stream()
			.map(tuple -> new TypeCount(tuple.get(OBJECT_TYPE_ALIAS, String.class), tuple.get(TOTAL_ALIAS, Long.class)))
			.toList();
	}
}
