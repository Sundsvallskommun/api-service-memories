package se.sundsvall.memories.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
import java.time.Month;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class LegalEntityEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(LegalEntityEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("topography", "category"),
			hasValidBeanEqualsExcluding("topography", "category"),
			hasValidBeanToStringExcluding("topography", "category")));
	}

	@Test
	void testBuilderMethods() {
		final var deletedDate = LocalDate.of(2026, Month.JANUARY, 15);

		final var result = LegalEntityEntity.create()
			.withLegalEntityId(123)
			.withName("Nödhjälpskommittén 1888-1889")
			.withAlternativeNames("Nödhjälpskommittén")
			.withTopography(TopographyEntity.create().withId(42).withName("Sundsvalls kommun"))
			.withLocationText("Sundsvall")
			.withStartDate("1888")
			.withEndDate("1889")
			.withPrincipal("Sundsvalls stad")
			.withComment("Bildad efter branden 1888")
			.withHistoryFilename("jurpers_123_historia.xml")
			.withCategory(CategoryEntity.create().withCategoryId(5).withName("Kommitté"))
			.withOptions(6)
			.withDeletedDate(deletedDate);

		assertThat(result.getLegalEntityId()).isEqualTo(123);
		assertThat(result.getName()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getAlternativeNames()).isEqualTo("Nödhjälpskommittén");
		assertThat(result.getTopography().getId()).isEqualTo(42);
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getStartDate()).isEqualTo("1888");
		assertThat(result.getEndDate()).isEqualTo("1889");
		assertThat(result.getPrincipal()).isEqualTo("Sundsvalls stad");
		assertThat(result.getComment()).isEqualTo("Bildad efter branden 1888");
		assertThat(result.getHistoryFilename()).isEqualTo("jurpers_123_historia.xml");
		assertThat(result.getCategory().getCategoryId()).isEqualTo(5);
		assertThat(result.getOptions()).isEqualTo(6);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(LegalEntityEntity.create()).hasAllNullFieldsOrProperties();
	}
}
