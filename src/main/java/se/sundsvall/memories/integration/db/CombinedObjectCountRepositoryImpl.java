package se.sundsvall.memories.integration.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.LinkedHashMap;
import java.util.Map;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static java.util.stream.Collectors.toMap;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.matchesParameters;

/**
 * Spring Data finds this by name: a fragment implementation is the {@code Impl} suffixed class of the fragment
 * interface, and the repository picks it up by extending {@link CombinedObjectCountRepository}.
 */
class CombinedObjectCountRepositoryImpl implements CombinedObjectCountRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Map<String, Long> countByType(final CombinedObjectParameters parameters) {
		final var cb = entityManager.getCriteriaBuilder();
		final var query = cb.createTupleQuery();
		final var root = query.from(CombinedObjectEntity.class);
		final var objectType = root.<String>get(OBJECT_TYPE);

		query.multiselect(objectType, cb.count(root))
			.where(matchesParameters(parameters).toPredicate(root, query, cb))
			.groupBy(objectType)
			.orderBy(cb.asc(objectType));

		return entityManager.createQuery(query).getResultList().stream()
			.collect(toMap(
				tuple -> tuple.get(0, String.class),
				tuple -> tuple.get(1, Long.class),
				(first, _) -> first,
				LinkedHashMap::new));
	}
}
