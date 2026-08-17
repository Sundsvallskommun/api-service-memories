package se.sundsvall.memories.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class CensusRecordTest {

	@Test
	void testBean() {
		assertThat(CensusRecord.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = CensusRecord.create()
			.withId(123)
			.withObjectNumber("SE/1234")
			.withSource("MTL")
			.withPropertyNumber1("Norrmalm 3")
			.withPropertyPart1("1/2")
			.withPropertyNumber2("Söder 5")
			.withPropertyPart2("1/4")
			.withPropertyNumber3("Väster 7")
			.withPropertyPart3("1/8")
			.withSerialNumber("12")
			.withHouseholdNumber("3")
			.withOrderNumber("1")
			.withFarmNumber("7")
			.withOccupationRelation("Bonde")
			.withRelationCode("H")
			.withFirstName("Anton")
			.withLastName("Nordin")
			.withGender("man")
			.withBirthYear("1852")
			.withNote("Flyttade in 1875");

		assertThat(result.getId()).isEqualTo(123);
		assertThat(result.getObjectNumber()).isEqualTo("SE/1234");
		assertThat(result.getSource()).isEqualTo("MTL");
		assertThat(result.getPropertyNumber1()).isEqualTo("Norrmalm 3");
		assertThat(result.getFirstName()).isEqualTo("Anton");
		assertThat(result.getLastName()).isEqualTo("Nordin");
		assertThat(result.getGender()).isEqualTo("man");
		assertThat(result.getBirthYear()).isEqualTo("1852");
		assertThat(result.getNote()).isEqualTo("Flyttade in 1875");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(CensusRecord.create()).hasAllNullFieldsOrProperties();
	}
}
