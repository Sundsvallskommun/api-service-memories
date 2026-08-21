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
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSettersExcluding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PersonEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(PersonEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSettersExcluding("displayName"),
			hasValidBeanHashCodeExcluding("displayName"),
			hasValidBeanEqualsExcluding("displayName"),
			hasValidBeanToStringExcluding("displayName")));
	}

	@Test
	void testBuilderMethods() {
		final var deletedDate = LocalDate.of(2026, Month.JANUARY, 15);

		final var result = PersonEntity.create()
			.withPersonId(123)
			.withPersonNumber("42")
			.withLastName("Nordin")
			.withFirstName("Anton")
			.withGender("man")
			.withBirthDate("1852-03-14")
			.withBirthParish("Sundsvall")
			.withDeathDate("1921-11-02")
			.withOccupation("Handlare")
			.withRelatedPersonName("Erik Nordin")
			.withRelatedPersonOccupation("Bonde")
			.withMovedInParish("Selånger")
			.withMovedOutParish("Njurunda")
			.withSources("Kyrkoarkiv")
			.withComment("Flyttade till Sundsvall 1875")
			.withBiographyFilename("person_123_biografi.xml")
			.withOptions(6)
			.withDeletedDate(deletedDate);

		assertThat(result.getPersonId()).isEqualTo(123);
		assertThat(result.getPersonNumber()).isEqualTo("42");
		assertThat(result.getLastName()).isEqualTo("Nordin");
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getGender()).isEqualTo("man");
		assertThat(result.getBirthDate()).isEqualTo("1852-03-14");
		assertThat(result.getBirthParish()).isEqualTo("Sundsvall");
		assertThat(result.getDeathDate()).isEqualTo("1921-11-02");
		assertThat(result.getOccupation()).isEqualTo("Handlare");
		assertThat(result.getRelatedPersonName()).isEqualTo("Erik Nordin");
		assertThat(result.getRelatedPersonOccupation()).isEqualTo("Bonde");
		assertThat(result.getMovedInParish()).isEqualTo("Selånger");
		assertThat(result.getMovedOutParish()).isEqualTo("Njurunda");
		assertThat(result.getSources()).isEqualTo("Kyrkoarkiv");
		assertThat(result.getComment()).isEqualTo("Flyttade till Sundsvall 1875");
		assertThat(result.getBiographyFilename()).isEqualTo("person_123_biografi.xml");
		assertThat(result.getOptions()).isEqualTo(6);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	/**
	 * The display name joins the two halves the archive stores separately, and either half can be missing.
	 */
	@Test
	void testDisplayName() {
		assertThat(PersonEntity.create().withFirstName("Anton").withLastName("Nordin").getDisplayName()).isEqualTo("Anton Nordin");
		assertThat(PersonEntity.create().withLastName("Nordin").getDisplayName()).isEqualTo("Nordin");
		assertThat(PersonEntity.create().withFirstName("Anton").getDisplayName()).isEqualTo("Anton");
		assertThat(PersonEntity.create().withFirstName(" ").withLastName("").getDisplayName()).isNull();
		assertThat(PersonEntity.create().getDisplayName()).isNull();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PersonEntity.create()).hasAllNullFieldsOrProperties();
	}
}
