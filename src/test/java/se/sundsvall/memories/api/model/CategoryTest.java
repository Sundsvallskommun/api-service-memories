package se.sundsvall.memories.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class CategoryTest {

	@Test
	void testBean() {
		assertThat(Category.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = Category.create()
			.withCategoryId(5)
			.withCode("FÖR")
			.withName("Förening");

		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getCode()).isEqualTo("FÖR");
		assertThat(result.getName()).isEqualTo("Förening");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Category.create()).hasAllNullFieldsOrProperties();
	}
}
