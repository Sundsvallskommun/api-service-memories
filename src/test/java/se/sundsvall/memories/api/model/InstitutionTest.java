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

class InstitutionTest {

	@Test
	void testBean() {
		assertThat(Institution.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = Institution.create()
			.withInstitutionId(3)
			.withCode("SVM")
			.withName("Sundsvalls museum")
			.withDescription("Kommunalt museum")
			.withUrl("https://sundsvallsmuseum.se")
			.withEmail("museet@sundsvall.se");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getInstitutionId()).isEqualTo(3);
		assertThat(result.getCode()).isEqualTo("SVM");
		assertThat(result.getName()).isEqualTo("Sundsvalls museum");
		assertThat(result.getDescription()).isEqualTo("Kommunalt museum");
		assertThat(result.getUrl()).isEqualTo("https://sundsvallsmuseum.se");
		assertThat(result.getEmail()).isEqualTo("museet@sundsvall.se");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Institution.create()).hasAllNullFieldsOrProperties();
	}
}
