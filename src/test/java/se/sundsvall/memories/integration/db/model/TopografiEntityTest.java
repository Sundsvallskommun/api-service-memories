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

class TopografiEntityTest {

	@Test
	void testBean() {
		assertThat(TopografiEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = TopografiEntity.create()
			.withTId(42)
			.withTopNamn("Sundsvall")
			.withTopKod("2281")
			.withPlats("Sundsvalls kommun")
			.withLand("Sverige");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTId()).isEqualTo(42);
		assertThat(result.getTopNamn()).isEqualTo("Sundsvall");
		assertThat(result.getTopKod()).isEqualTo("2281");
		assertThat(result.getPlats()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getLand()).isEqualTo("Sverige");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TopografiEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new TopografiEntity()).hasAllNullFieldsOrProperties();
	}
}
