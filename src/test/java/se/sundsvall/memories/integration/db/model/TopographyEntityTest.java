package se.sundsvall.memories.integration.db.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class TopographyEntityTest {

	@Test
	void testBean() {
		assertThat(TopographyEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = TopographyEntity.create()
			.withTId(42)
			.withName("Sundsvall")
			.withCode("2281")
			.withPlace("Sundsvalls kommun")
			.withCountry("Sverige");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("Sundsvall");
		assertThat(result.getCode()).isEqualTo("2281");
		assertThat(result.getPlace()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getCountry()).isEqualTo("Sverige");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TopographyEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new TopographyEntity()).hasAllNullFieldsOrProperties();
	}
}
