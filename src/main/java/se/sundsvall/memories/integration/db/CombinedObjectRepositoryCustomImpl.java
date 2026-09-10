package se.sundsvall.memories.integration.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.CategoryEntity_;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity_;

import static java.util.Optional.ofNullable;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.CREATOR_LEGAL_ENTITY;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.GENDER;
import static se.sundsvall.memories.integration.db.model.CombinedObjectEntity_.OBJECT_TYPE;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filtersExcludingCategory;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filtersExcludingGender;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.filtersExcludingObjectType;
import static se.sundsvall.memories.integration.db.specification.CombinedObjectSpecification.hasCategorisedCreator;

class CombinedObjectRepositoryCustomImpl implements CombinedObjectRepositoryCustom {

	private static final String OBJECT_TYPE_ALIAS = "objectType";
	private static final String GENDER_ALIAS = "gender";
	private static final String CATEGORY_ID_ALIAS = "categoryId";
	private static final String CATEGORY_NAME_ALIAS = "categoryName";
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

	@Override
	public List<CategoryCount> countByCategory(final CombinedObjectParameters parameters) {
		final var cb = entityManager.getCriteriaBuilder();
		final var query = cb.createTupleQuery();
		final var root = query.from(CombinedObjectEntity.class);

		// The joins are created before the filters, which then reuse the first one instead of joining JURPERS again.
		// The category's name is grouped on alongside its id so the chip can be labelled without a second lookup.
		final var legalEntity = root.<CombinedObjectEntity, LegalEntityEntity>join(CREATOR_LEGAL_ENTITY, JoinType.LEFT);
		final var category = legalEntity.<LegalEntityEntity, CategoryEntity>join(LegalEntityEntity_.CATEGORY, JoinType.LEFT);
		final var categoryId = category.<Integer>get(CategoryEntity_.CATEGORY_ID);
		final var categoryName = category.<String>get(CategoryEntity_.NAME);

		// Every filter but the category selection, mirroring the other two counters. Only the rows with a categorised
		// originator are counted: the registers and the objects without one would otherwise form a NULL group.
		final var predicate = ofNullable(filtersExcludingCategory(parameters).and(hasCategorisedCreator()).toPredicate(root, query, cb))
			.orElseGet(cb::conjunction);

		// hasCategorisedCreator() guards the foreign key, which Hibernate reads without touching KATEGORI. The chip is
		// labelled from the joined row instead, so that row needs the guards the dropdown applies as well: a KAT_ID
		// pointing at no row would group as (null, null) and a blank-named category as a chip /categories never lists.
		// The same rule as CategorySpecification.hasName(), which is what the frontend picks its categories from.
		final var listable = cb.and(cb.isNotNull(categoryId), cb.isNotNull(cb.nullif(cb.trim(categoryName), "")));

		query.multiselect(categoryId.alias(CATEGORY_ID_ALIAS), categoryName.alias(CATEGORY_NAME_ALIAS), cb.count(root).alias(TOTAL_ALIAS))
			.where(cb.and(predicate, listable))
			.groupBy(categoryId, categoryName)
			.orderBy(cb.asc(categoryName), cb.asc(categoryId));

		return entityManager.createQuery(query).getResultList().stream()
			.map(tuple -> new CategoryCount(tuple.get(CATEGORY_ID_ALIAS, Integer.class), tuple.get(CATEGORY_NAME_ALIAS, String.class), tuple.get(TOTAL_ALIAS, Long.class)))
			.toList();
	}
}
