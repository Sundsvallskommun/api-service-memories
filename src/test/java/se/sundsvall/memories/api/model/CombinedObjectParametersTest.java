package se.sundsvall.memories.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class CombinedObjectParametersTest {

	@Test
	void testBean() {
		assertThat(CombinedObjectParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = CombinedObjectParameters.create()
			.withQuery("Sundsvall")
			.withYearFrom(1900)
			.withYearTo(1950)
			.withLocation("Sundsvall")
			.withTopographyId(List.of(1, 4))
			.withObjectType(List.of("Foto", "Ljud"))
			.withGender("man")
			.withCategoryId(List.of(2, 5))
			.withNodeId(List.of(19000, 20001))
			.withPage(2)
			.withLimit(50);

		assertThat(result.getQuery()).isEqualTo("Sundsvall");
		assertThat(result.getYearFrom()).isEqualTo(1900);
		assertThat(result.getYearTo()).isEqualTo(1950);
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getTopographyId()).containsExactly(1, 4);
		assertThat(result.getObjectType()).containsExactly("Foto", "Ljud");
		assertThat(result.getGender()).isEqualTo("man");
		assertThat(result.getCategoryId()).containsExactly(2, 5);
		assertThat(result.getNodeId()).containsExactly(19000, 20001);
		assertThat(result.getPage()).isEqualTo(2);
		assertThat(result.getLimit()).isEqualTo(50);
	}

	@Test
	void testDefaults() {
		final var result = CombinedObjectParameters.create();

		assertThat(result.getQuery()).isNull();
		assertThat(result.getYearFrom()).isNull();
		assertThat(result.getYearTo()).isNull();
		assertThat(result.getLocation()).isNull();
		assertThat(result.getTopographyId()).isNull();
		assertThat(result.getObjectType()).isNull();
		assertThat(result.getGender()).isNull();
		assertThat(result.getCategoryId()).isNull();
		assertThat(result.getNodeId()).isNull();
		assertThat(result.getPage()).isEqualTo(1);
		assertThat(result.getLimit()).isEqualTo(100);
	}
}
