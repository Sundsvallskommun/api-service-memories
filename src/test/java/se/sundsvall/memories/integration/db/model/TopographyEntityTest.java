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
			.withName("Anundsjö")
			.withCode("228471")
			.withPlace("Bredbyn")
			.withMunicipality("Örnsköldsvik");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getName()).isEqualTo("Anundsjö");
		assertThat(result.getCode()).isEqualTo("228471");
		assertThat(result.getPlace()).isEqualTo("Bredbyn");
		assertThat(result.getMunicipality()).isEqualTo("Örnsköldsvik");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TopographyEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new TopographyEntity()).hasAllNullFieldsOrProperties();
	}

	/** The place and the parish it sits in, in that order — the parish alone is shared by every place under it. */
	@Test
	void getDisplayNameNamesThePlaceAndTheParish() {
		final var result = TopographyEntity.create().withName("Anundsjö").withPlace("Bredbyn").withCode("228471");

		assertThat(result.getDisplayName()).isEqualTo("Bredbyn, Anundsjö");
	}

	/** A parish-level row carries no place of its own, and a stray place none of a parish. Either stands alone. */
	@Test
	void getDisplayNameUsesWhicheverColumnIsFilled() {
		assertThat(TopographyEntity.create().withName("Anundsjö").withPlace("  ").withCode("228471").getDisplayName())
			.isEqualTo("Anundsjö");
		assertThat(TopographyEntity.create().withName("").withPlace("Bredbyn").withCode("228471").getDisplayName())
			.isEqualTo("Bredbyn");
	}

	/** The code names nothing — it is a parish code shared by every place under it, so it is never a label. */
	@Test
	void getDisplayNameNeverFallsBackToTheCode() {
		assertThat(TopographyEntity.create().withName("").withPlace("  ").withCode("228471").getDisplayName()).isNull();
	}

	@Test
	void getDisplayNameReturnsNullWhenAllSourcesAreMissingOrBlank() {
		assertThat(TopographyEntity.create().getDisplayName()).isNull();
		assertThat(TopographyEntity.create().withName("").withPlace("  ").withCode("").getDisplayName()).isNull();
	}
}
