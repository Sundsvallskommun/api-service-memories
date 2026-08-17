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

class PhotoParametersTest {

	@Test
	void testBean() {
		assertThat(PhotoParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = PhotoParameters.create()
			.withQuery("Sundsvall")
			.withObjectType("Foto")
			.withYearFrom(1900)
			.withYearTo(1950)
			.withLocation("Sundsvall")
			.withPage(2)
			.withLimit(50);

		assertThat(result.getQuery()).isEqualTo("Sundsvall");
		assertThat(result.getObjectType()).isEqualTo("Foto");
		assertThat(result.getYearFrom()).isEqualTo(1900);
		assertThat(result.getYearTo()).isEqualTo(1950);
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getPage()).isEqualTo(2);
		assertThat(result.getLimit()).isEqualTo(50);
	}

	@Test
	void testDefaults() {
		final var result = PhotoParameters.create();

		assertThat(result.getQuery()).isNull();
		assertThat(result.getObjectType()).isNull();
		assertThat(result.getYearFrom()).isNull();
		assertThat(result.getYearTo()).isNull();
		assertThat(result.getLocation()).isNull();
		assertThat(result.getPage()).isEqualTo(1);
		assertThat(result.getLimit()).isEqualTo(100);
	}
}
