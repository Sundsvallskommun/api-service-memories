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

class FotoOcmEntityTest {

	@Test
	void testBean() {
		assertThat(FotoOcmEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = FotoOcmEntity.create()
			.withId(1)
			.withPhotoId(1001)
			.withOcmId(20);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getPhotoId()).isEqualTo(1001);
		assertThat(result.getOcmId()).isEqualTo(20);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FotoOcmEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new FotoOcmEntity()).hasAllNullFieldsOrProperties();
	}
}
