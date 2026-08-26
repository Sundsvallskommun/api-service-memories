package se.sundsvall.memories.integration.db;

import jakarta.persistence.EntityManager;
import java.util.List;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.GENDER;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filtersExcludingGender;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filtersExcludingObjectType;

class CombinedObjectRepositoryCustomImpl implements CombinedObjectRepositoryCustom {

	private static final String OBJECT_TYPE_ALIAS = "objectType";
	private static final String GENDER_ALIAS = "gender";
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

		// Every filter but the type selection, so a chip counts what selecting that type would return. A specification
		// that restricts nothing yields no predicate at all, hence the fallback rather than a bare where(null).
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

	@Override
	public List<GenderCount> countByGender(final CombinedObjectParameters parameters) {
		final var cb = entityManager.getCriteriaBuilder();
		final var query = cb.createTupleQuery();
		final var root = query.from(CombinedObjectEntity.class);
		final var gender = root.<String>get(GENDER);

		// Every filter but the gender selection, mirroring countByType. Only the rows that record a gender are
		// counted: the objects have none, and a NULL group would count them as a gender of their own.
		final var predicate = ofNullable(filtersExcludingGender(parameters).toPredicate(root, query, cb))
			.orElseGet(cb::conjunction);

		query.multiselect(gender.alias(GENDER_ALIAS), cb.count(root).alias(TOTAL_ALIAS))
			.where(cb.and(predicate, cb.isNotNull(gender)))
			.groupBy(gender)
			.orderBy(cb.asc(gender));

		return entityManager.createQuery(query).getResultList().stream()
			.map(tuple -> new GenderCount(tuple.get(GENDER_ALIAS, String.class), tuple.get(TOTAL_ALIAS, Long.class)))
			.toList();
	}
}
