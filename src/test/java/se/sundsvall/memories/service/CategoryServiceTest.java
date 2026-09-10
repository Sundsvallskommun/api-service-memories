package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.integration.db.CategoryRepository;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.integration.db.LegalEntityRepositoryCustom.CategoryTotal;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Which categories are selectable and how they are sized is verified against a real database in the specification
// tests. These cover what the service does: read both, pair them, hand them on in the repository's order.
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepositoryMock;

	@Mock
	private LegalEntityRepository legalEntityRepositoryMock;

	@InjectMocks
	private CategoryService service;

	@Test
	void getCategoriesPairsTheSelectableCategoriesWithTheirSizes() {
		when(categoryRepositoryMock.findAllSelectable()).thenReturn(List.of(
			CategoryEntity.create().withCategoryId(2).withCode("AB").withName("Aktiebolag"),
			CategoryEntity.create().withCategoryId(5).withCode("KOM").withName("Kommitté")));
		when(legalEntityRepositoryMock.countByCategory()).thenReturn(List.of(new CategoryTotal(5, 4L)));

		final var result = service.getCategories();

		assertThat(result).extracting(Category::getCategoryId, Category::getCode, Category::getName, Category::getLegalEntityCount)
			.containsExactly(tuple(2, "AB", "Aktiebolag", 0L), tuple(5, "KOM", "Kommitté", 4L));
		verify(categoryRepositoryMock).findAllSelectable();
		verify(legalEntityRepositoryMock).countByCategory();
	}

	@Test
	void getCategoriesWhenThereAreNone() {
		when(categoryRepositoryMock.findAllSelectable()).thenReturn(List.of());
		when(legalEntityRepositoryMock.countByCategory()).thenReturn(List.of());

		assertThat(service.getCategories()).isEmpty();
		verify(categoryRepositoryMock).findAllSelectable();
		verify(legalEntityRepositoryMock).countByCategory();
	}
}
