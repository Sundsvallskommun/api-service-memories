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

class FilmParametersTest {

	@Test
	void testBean() {
		assertThat(FilmParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = FilmParameters.create()
			.withQuery("midsommar")
			.withPage(2)
			.withLimit(50);

		assertThat(result.getQuery()).isEqualTo("midsommar");
		assertThat(result.getPage()).isEqualTo(2);
		assertThat(result.getLimit()).isEqualTo(50);
	}

	@Test
	void testDefaults() {
		final var result = FilmParameters.create();

		assertThat(result.getQuery()).isNull();
		assertThat(result.getPage()).isEqualTo(1);
		assertThat(result.getLimit()).isEqualTo(100);
	}
}
