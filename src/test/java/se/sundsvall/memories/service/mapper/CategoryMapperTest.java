package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.integration.db.LegalEntityRepositoryCustom.CategoryTotal;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class CategoryMapperTest {

	@Test
	void toCategory() {
		final var result = CategoryMapper.toCategory(CategoryEntity.create().withCategoryId(5).withCode("KOM").withName("Kommitté"), 3L);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getCode()).isEqualTo("KOM");
		assertThat(result.getName()).isEqualTo("Kommitté");
		assertThat(result.getLegalEntityCount()).isEqualTo(3L);
	}

	@Test
	void toCategoryWhenNull() {
		assertThat(CategoryMapper.toCategory(null, 3L)).isNull();
	}

	/** The categories keep the order they came in, and a category the sizes do not mention holds zero, not null. */
	@Test
	void toCategoryListPairsEachCategoryWithItsSize() {
		final var entities = List.of(
			CategoryEntity.create().withCategoryId(2).withCode("AB").withName("Aktiebolag"),
			CategoryEntity.create().withCategoryId(5).withCode("KOM").withName("Kommitté"),
			CategoryEntity.create().withCategoryId(7).withCode("BY").withName("By"));
		final var totals = List.of(new CategoryTotal(5, 4L), new CategoryTotal(2, 1L), new CategoryTotal(null, 9L));

		final var result = CategoryMapper.toCategoryList(entities, totals);

		assertThat(result).extracting(Category::getCategoryId, Category::getName, Category::getLegalEntityCount)
			.containsExactly(tuple(2, "Aktiebolag", 1L), tuple(5, "Kommitté", 4L), tuple(7, "By", 0L));
	}

	@Test
	void toCategoryListWithoutSizes() {
		final var result = CategoryMapper.toCategoryList(List.of(CategoryEntity.create().withCategoryId(2).withName("Aktiebolag")), null);

		assertThat(result).extracting(Category::getCategoryId, Category::getLegalEntityCount).containsExactly(tuple(2, 0L));
	}

	@Test
	void toCategoryListWhenNull() {
		assertThat(CategoryMapper.toCategoryList(null, null)).isEqualTo(emptyList());
	}
}
