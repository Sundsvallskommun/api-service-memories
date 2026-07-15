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

class CensusRecordParametersTest {

	@Test
	void testBean() {
		assertThat(CensusRecordParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = CensusRecordParameters.create()
			.withLastName("Nordin")
			.withFirstName("Anton")
			.withYearFrom(1850)
			.withYearTo(1900)
			.withGender("man")
			.withPage(2)
			.withLimit(50);

		assertThat(result.getLastName()).isEqualTo("Nordin");
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getYearFrom()).isEqualTo(1850);
		assertThat(result.getYearTo()).isEqualTo(1900);
		assertThat(result.getGender()).isEqualTo("man");
		assertThat(result.getPage()).isEqualTo(2);
		assertThat(result.getLimit()).isEqualTo(50);
	}

	@Test
	void testDefaults() {
		final var result = CensusRecordParameters.create();

		assertThat(result.getLastName()).isNull();
		assertThat(result.getFirstName()).isNull();
		assertThat(result.getYearFrom()).isNull();
		assertThat(result.getYearTo()).isNull();
		assertThat(result.getGender()).isNull();
		assertThat(result.getPage()).isEqualTo(1);
		assertThat(result.getLimit()).isEqualTo(100);
	}
}
