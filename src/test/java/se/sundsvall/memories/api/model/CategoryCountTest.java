package se.sundsvall.memories.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class CategoryCountTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(CategoryCount.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = CategoryCount.create()
			.withCategoryId(5)
			.withName("Kommitté")
			.withCount(12L);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getName()).isEqualTo("Kommitté");
		assertThat(result.getCount()).isEqualTo(12L);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(CategoryCount.create()).hasAllNullFieldsOrProperties();
	}
}
