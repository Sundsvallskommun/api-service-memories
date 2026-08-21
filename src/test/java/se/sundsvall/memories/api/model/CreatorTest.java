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

class CreatorTest {

	@Test
	void testBean() {
		assertThat(Creator.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = Creator.create()
			.withPersonId(1)
			.withPerson("Anton Nordin")
			.withLegalEntityId(10)
			.withLegalEntity("Nödhjälpskommittén 1888-1889");

		assertThat(result.getPersonId()).isEqualTo(1);
		assertThat(result.getPerson()).isEqualTo("Anton Nordin");
		assertThat(result.getLegalEntityId()).isEqualTo(10);
		assertThat(result.getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Creator.create()).hasAllNullFieldsOrProperties();
	}
}
