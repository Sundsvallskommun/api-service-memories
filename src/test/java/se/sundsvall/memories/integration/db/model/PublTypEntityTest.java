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

class PublTypEntityTest {

	@Test
	void testBean() {
		assertThat(PublTypEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var id = 4;
		final var publiktyp = "Broschyrer";

		final var result = PublTypEntity.create()
			.withId(id)
			.withPubliktyp(publiktyp);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getPubliktyp()).isEqualTo(publiktyp);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PublTypEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new PublTypEntity()).hasAllNullFieldsOrProperties();
	}
}
