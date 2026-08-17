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

class PublicationParametersTest {

	@Test
	void testBean() {
		assertThat(PublicationParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = PublicationParameters.create()
			.withQuery("Drunkningsolycka")
			.withYearFrom(1970)
			.withYearTo(1990)
			.withLocation("Sundsvall")
			.withPage(3)
			.withLimit(25);

		assertThat(result.getQuery()).isEqualTo("Drunkningsolycka");
		assertThat(result.getYearFrom()).isEqualTo(1970);
		assertThat(result.getYearTo()).isEqualTo(1990);
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getPage()).isEqualTo(3);
		assertThat(result.getLimit()).isEqualTo(25);
	}

	@Test
	void testDefaults() {
		final var result = PublicationParameters.create();

		assertThat(result.getQuery()).isNull();
		assertThat(result.getYearFrom()).isNull();
		assertThat(result.getYearTo()).isNull();
		assertThat(result.getLocation()).isNull();
		assertThat(result.getPage()).isEqualTo(1);
		assertThat(result.getLimit()).isEqualTo(100);
	}
}
