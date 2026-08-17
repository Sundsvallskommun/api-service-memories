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

class SeamanEntityTest {

	@Test
	void testBean() {
		assertThat(SeamanEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = SeamanEntity.create()
			.withId(123)
			.withFirstName("Anton")
			.withLastName1("Nordin")
			.withLastName2("Sjöberg")
			.withIdNumber(4711)
			.withBirthDate("1852-03-14")
			.withAge("28")
			.withBirthParish("Sundsvall")
			.withBirthPlace("Sundsvall")
			.withHomeParish("Njurunda")
			.withHomePlace("Njurunda")
			.withCivilStatus("Gift")
			.withFather("Erik Nordin")
			.withMother("Anna Nordin")
			.withSeamensHouse("Sundsvalls sjömanshus")
			.withEnrollmentNumber("112")
			.withEnrollmentDate("1875-04-01")
			.withRank("Matros")
			.withSignOnPlace("Sundsvall")
			.withSignOnDate("1876-05-01")
			.withSignOffPlace("Göteborg")
			.withSignOffDate("1877-09-01")
			.withShip("Briggen Freja")
			.withHomePort("Sundsvall")
			.withShipType("Brigg")
			.withShipOwner("Rederi AB Nord")
			.withCaptain("Olof Berg")
			.withDestination("London")
			.withOther("Övrigt")
			.withNote("Avmönstrad")
			.withArchive("Sundsvalls sjömanshus arkiv")
			.withVolume("A1:3")
			.withArchiveNumber("SE/HLA/1234")
			.withPage("42");

		assertThat(result.getId()).isEqualTo(123);
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getLastName1()).isEqualTo("Nordin");
		assertThat(result.getLastName2()).isEqualTo("Sjöberg");
		assertThat(result.getIdNumber()).isEqualTo(4711);
		assertThat(result.getBirthParish()).isEqualTo("Sundsvall");
		assertThat(result.getSeamensHouse()).isEqualTo("Sundsvalls sjömanshus");
		assertThat(result.getRank()).isEqualTo("Matros");
		assertThat(result.getShip()).isEqualTo("Briggen Freja");
		assertThat(result.getArchive()).isEqualTo("Sundsvalls sjömanshus arkiv");
		assertThat(result.getVolume()).isEqualTo("A1:3");
		assertThat(result.getPage()).isEqualTo("42");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SeamanEntity.create()).hasAllNullFieldsOrProperties();
	}
}
