package se.sundsvall.memories.integration.db.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSettersExcluding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class TopographyEntityTest {

	@Test
	void testBean() {
		// displayName is derived from name/place/code and has no setter, so it is excluded from the bean contract.
		assertThat(TopographyEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSettersExcluding("displayName"),
			hasValidBeanHashCodeExcluding("displayName"),
			hasValidBeanEqualsExcluding("displayName"),
			hasValidBeanToStringExcluding("displayName")));
	}

	@Test
	void testBuilderMethods() {
		final var result = TopographyEntity.create()
			.withId(42)
			.withName("Sundsvall")
			.withCode("2281")
			.withPlace("Sundsvalls kommun")
			.withCountry("Sverige");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("Sundsvall");
		assertThat(result.getCode()).isEqualTo("2281");
		assertThat(result.getPlace()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getCountry()).isEqualTo("Sverige");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TopographyEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new TopographyEntity()).hasAllNullFieldsOrProperties();
	}

	@Test
	void getDisplayNamePrefersName() {
		final var result = TopographyEntity.create().withName("Sundsvall").withPlace("Indal").withCode("0001");

		assertThat(result.getDisplayName()).isEqualTo("Sundsvall");
	}

	@Test
	void getDisplayNameFallsBackToPlaceWhenNameIsBlank() {
		final var result = TopographyEntity.create().withName("  ").withPlace("Indal").withCode("0001");

		assertThat(result.getDisplayName()).isEqualTo("Indal");
	}

	@Test
	void getDisplayNameFallsBackToCodeWhenNameAndPlaceAreBlank() {
		final var result = TopographyEntity.create().withName("").withPlace("  ").withCode("0001");

		assertThat(result.getDisplayName()).isEqualTo("0001");
	}

	@Test
	void getDisplayNameReturnsNullWhenAllSourcesAreMissingOrBlank() {
		assertThat(TopographyEntity.create().getDisplayName()).isNull();
		assertThat(TopographyEntity.create().withName("").withPlace("  ").withCode("").getDisplayName()).isNull();
	}
}
