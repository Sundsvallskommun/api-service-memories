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

class OcmEntityTest {

	@Test
	void testBean() {
		assertThat(OcmEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = OcmEntity.create()
			.withId(42)
			.withText("Midsommar")
			.withCode("MID")
			.withDescription("Swedish midsummer celebration");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getText()).isEqualTo("Midsommar");
		assertThat(result.getCode()).isEqualTo("MID");
		assertThat(result.getDescription()).isEqualTo("Swedish midsummer celebration");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(OcmEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new OcmEntity()).hasAllNullFieldsOrProperties();
	}
}
