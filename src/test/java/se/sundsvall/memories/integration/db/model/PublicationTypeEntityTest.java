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

class PublicationTypeEntityTest {

	@Test
	void testBean() {
		assertThat(PublicationTypeEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = PublicationTypeEntity.create()
			.withId(1)
			.withPublicationType("Tidning");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getPublicationType()).isEqualTo("Tidning");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PublicationTypeEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new PublicationTypeEntity()).hasAllNullFieldsOrProperties();
	}
}
