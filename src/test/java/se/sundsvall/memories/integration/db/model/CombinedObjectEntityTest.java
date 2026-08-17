package se.sundsvall.memories.integration.db.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class CombinedObjectEntityTest {

	@Test
	void testBean() {
		assertThat(CombinedObjectEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("topography"),
			hasValidBeanEqualsExcluding("topography"),
			hasValidBeanToStringExcluding("topography")));
	}

	@Test
	void testBuilderMethods() {
		final var result = CombinedObjectEntity.create()
			.withObjectKey("foto-1001")
			.withSourceId(1001)
			.withObjectType("Foto")
			.withTitle("Stadsvy")
			.withYear(1920)
			.withTopography(TopographyEntity.create().withId(1).withName("Sundsvalls kommun"))
			.withLocationText("Sundsvall");

		assertThat(result.getObjectKey()).isEqualTo("foto-1001");
		assertThat(result.getSourceId()).isEqualTo(1001);
		assertThat(result.getObjectType()).isEqualTo("Foto");
		assertThat(result.getTitle()).isEqualTo("Stadsvy");
		assertThat(result.getYear()).isEqualTo(1920);
		assertThat(result.getTopography().getId()).isEqualTo(1);
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(CombinedObjectEntity.create()).hasAllNullFieldsOrProperties();
	}
}
