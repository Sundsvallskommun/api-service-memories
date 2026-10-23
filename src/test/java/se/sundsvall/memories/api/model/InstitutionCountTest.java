package se.sundsvall.memories.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;

class InstitutionCountTest {
	@Test
	void testBean() {
		MatcherAssert.assertThat(InstitutionCount.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = InstitutionCount.create()
			.withInstitutionId(3)
			.withName("Föreningsarkivet Västernorrland")
			.withCount(12L);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getInstitutionId()).isEqualTo(3);
		assertThat(result.getName()).isEqualTo("Föreningsarkivet Västernorrland");
		assertThat(result.getCount()).isEqualTo(12L);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(InstitutionCount.create()).hasAllNullFieldsOrProperties();
	}
}
