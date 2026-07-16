package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.integration.db.CategoryRepository;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryLookupTest {

	@Mock
	private CategoryRepository repositoryMock;

	@InjectMocks
	private CategoryLookup categoryLookup;

	private static List<CategoryEntity> sample() {
		return List.of(
			CategoryEntity.create().withCategoryId(5).withCode("FÖR").withName("Förening"),
			CategoryEntity.create().withCategoryId(2).withCode("KOM").withName("Aktiebolag"),
			CategoryEntity.create().withCategoryId(9).withCode("NUL").withName(null));
	}

	@Test
	void resolveReturnsName() {
		when(repositoryMock.findAll()).thenReturn(sample());
		categoryLookup.loadCache();

		assertThat(categoryLookup.resolve(5)).isEqualTo("Förening");
		assertThat(categoryLookup.resolve(2)).isEqualTo("Aktiebolag");
	}

	@Test
	void resolveReturnsNullForNullOrUnknownOrNamelessId() {
		when(repositoryMock.findAll()).thenReturn(sample());
		categoryLookup.loadCache();

		assertThat(categoryLookup.resolve(null)).isNull();
		assertThat(categoryLookup.resolve(404)).isNull();
		assertThat(categoryLookup.resolve(9)).isNull();
	}

	@Test
	void resolveLazilyLoadsWhenCacheEmpty() {
		when(repositoryMock.findAll()).thenReturn(sample());

		assertThat(categoryLookup.resolve(5)).isEqualTo("Förening");
	}

	@Test
	void getAllCategoriesReturnsMappedAndSortedByName() {
		when(repositoryMock.findAll()).thenReturn(sample());
		categoryLookup.loadCache();

		final var result = categoryLookup.getAllCategories();

		assertThat(result).extracting("categoryId", "name")
			.containsExactly(
				tuple(2, "Aktiebolag"),
				tuple(5, "Förening"),
				tuple(9, null));
	}

	@Test
	void getAllCategoriesLazilyLoadsWhenCacheEmpty() {
		when(repositoryMock.findAll()).thenReturn(sample());

		assertThat(categoryLookup.getAllCategories()).hasSize(3);
	}
}
