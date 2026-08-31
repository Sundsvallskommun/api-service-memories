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

class CensusRecordIdTest {

	@Test
	void testBean() {
		assertThat(CensusRecordId.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testAllArgsConstructor() {
		final var result = new CensusRecordId("1845", 123);

		assertThat(result.getSource()).isEqualTo("1845");
		assertThat(result.getId()).isEqualTo(123);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new CensusRecordId()).hasAllNullFieldsOrProperties();
	}
}
