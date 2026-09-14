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

class InstitutionEntityTest {

	@Test
	void testBean() {
		assertThat(InstitutionEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = InstitutionEntity.create()
			.withId(3)
			.withName("Sundsvalls museum")
			.withCode("SVM")
			.withDescription("Kommunalt museum")
			.withUrl("https://sundsvallsmuseum.se")
			.withEmail("museet@sundsvall.se");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(3);
		assertThat(result.getName()).isEqualTo("Sundsvalls museum");
		assertThat(result.getCode()).isEqualTo("SVM");
		assertThat(result.getDescription()).isEqualTo("Kommunalt museum");
		assertThat(result.getUrl()).isEqualTo("https://sundsvallsmuseum.se");
		assertThat(result.getEmail()).isEqualTo("museet@sundsvall.se");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(InstitutionEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new InstitutionEntity()).hasAllNullFieldsOrProperties();
	}
}
