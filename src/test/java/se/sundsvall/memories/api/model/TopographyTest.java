package se.sundsvall.memories.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class TopographyTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Topography.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = Topography.create()
			.withTopographyId(1)
			.withDisplayName("Sundsvall")
			.withName("Sundsvall")
			.withCode("SUN")
			.withPlace("Sundsvalls kommun")
			.withMunicipality("Sverige");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTopographyId()).isEqualTo(1);
		assertThat(result.getDisplayName()).isEqualTo("Sundsvall");
		assertThat(result.getName()).isEqualTo("Sundsvall");
		assertThat(result.getCode()).isEqualTo("SUN");
		assertThat(result.getPlace()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getMunicipality()).isEqualTo("Sverige");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Topography.create()).hasAllNullFieldsOrProperties();
	}
}
