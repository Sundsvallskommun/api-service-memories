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

class OcmEntityTest {

	@Test
	void testBean() {
		assertThat(OcmEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSettersExcluding("displayName"),
			hasValidBeanHashCodeExcluding("displayName"),
			hasValidBeanEqualsExcluding("displayName"),
			hasValidBeanToStringExcluding("displayName")));
	}

	@Test
	void testBuilderMethods() {
		final var result = OcmEntity.create()
			.withId(42)
			.withText("Midsommar")
			.withCode("MID")
			.withDescription("Swedish midsummer celebration");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getText()).isEqualTo("Midsommar");
		assertThat(result.getCode()).isEqualTo("MID");
		assertThat(result.getDescription()).isEqualTo("Swedish midsummer celebration");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(OcmEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new OcmEntity()).hasAllNullFieldsOrProperties();
	}

	@Test
	void getDisplayNamePrefersText() {
		final var result = OcmEntity.create().withText("Midsommar").withDescription("Beskrivning").withCode("MID");

		assertThat(result.getDisplayName()).isEqualTo("Midsommar");
	}

	@Test
	void getDisplayNameFallsBackToDescriptionWhenTextIsBlank() {
		final var result = OcmEntity.create().withText("  ").withDescription("Beskrivning").withCode("MID");

		assertThat(result.getDisplayName()).isEqualTo("Beskrivning");
	}

	@Test
	void getDisplayNameFallsBackToCodeWhenTextAndDescriptionAreBlank() {
		final var result = OcmEntity.create().withText("").withDescription("  ").withCode("MID");

		assertThat(result.getDisplayName()).isEqualTo("MID");
	}

	@Test
	void getDisplayNameReturnsNullWhenAllSourcesAreMissingOrBlank() {
		assertThat(OcmEntity.create().getDisplayName()).isNull();
		assertThat(OcmEntity.create().withText("").withDescription("  ").withCode("").getDisplayName()).isNull();
	}
}
