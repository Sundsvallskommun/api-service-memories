package se.sundsvall.memories.integration.db;

import jakarta.persistence.EntityManager;
import java.util.List;
import se.sundsvall.memories.integration.db.model.CategoryEntity_;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.integration.db.model.LegalEntityEntity_.CATEGORY;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.notPlaceholder;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.published;

class LegalEntityRepositoryCustomImpl implements LegalEntityRepositoryCustom {

	private static final String CATEGORY_ID_ALIAS = "categoryId";
	private static final String TOTAL_ALIAS = "total";

	private final EntityManager entityManager;

	LegalEntityRepositoryCustomImpl(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<CategoryTotal> countByCategory() {
		final var cb = entityManager.getCriteriaBuilder();
		final var query = cb.createTupleQuery();
		final var root = query.from(LegalEntityEntity.class);
		// the foreign key column, which Hibernate reads without joining KATEGORI
		final var categoryId = root.get(CATEGORY).<Integer>get(CategoryEntity_.CATEGORY_ID);

		// The rows the legal entity search returns, so a size never promises more than selecting the category gives.
		final var predicate = ofNullable(published().and(notDeleted()).and(notPlaceholder()).toPredicate(root, query, cb))
			.orElseGet(cb::conjunction);

		query.multiselect(categoryId.alias(CATEGORY_ID_ALIAS), cb.count(root).alias(TOTAL_ALIAS))
			.where(cb.and(predicate, cb.isNotNull(categoryId)))
			.groupBy(categoryId)
			.orderBy(cb.asc(categoryId));

		return entityManager.createQuery(query).getResultList().stream()
			.map(tuple -> new CategoryTotal(tuple.get(CATEGORY_ID_ALIAS, Integer.class), tuple.get(TOTAL_ALIAS, Long.class)))
			.toList();
	}
}
