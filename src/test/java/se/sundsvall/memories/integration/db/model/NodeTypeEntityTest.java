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

class NodeTypeEntityTest {

	@Test
	void testBean() {
		assertThat(NodeTypeEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = NodeTypeEntity.create()
			.withId(2)
			.withParentId(1)
			.withName("Arkiv")
			.withAllowedParentTypeIds("1,3");

		assertThat(result.getId()).isEqualTo(2);
		assertThat(result.getParentId()).isEqualTo(1);
		assertThat(result.getName()).isEqualTo("Arkiv");
		assertThat(result.getAllowedParentTypeIds()).isEqualTo("1,3");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NodeTypeEntity.create()).hasAllNullFieldsOrProperties();
	}
}
