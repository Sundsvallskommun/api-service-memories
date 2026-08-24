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

class ObjectTypeCountTest {

	@Test
	void testBean() {
		assertThat(ObjectTypeCount.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = ObjectTypeCount.create()
			.withObjectType("Foto")
			.withCount(12L);

		assertThat(result.getObjectType()).isEqualTo("Foto");
		assertThat(result.getCount()).isEqualTo(12L);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ObjectTypeCount.create()).hasAllNullFieldsOrProperties();
	}
}
