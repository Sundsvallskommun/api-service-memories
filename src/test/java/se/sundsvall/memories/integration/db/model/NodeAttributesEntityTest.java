package se.sundsvall.memories.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.math.BigDecimal;
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

class NodeAttributesEntityTest {

	private static final String[] ASSOCIATIONS = {
		"legalEntity", "person", "institution", "category", "topography", "subject"
	};

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(100000), 2), BigDecimal.class);
	}

	/** The lookups are lazy associations and stay out of equality, the way {@link CombinedObjectEntity} keeps its out. */
	@Test
	void testBean() {
		assertThat(NodeAttributesEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding(ASSOCIATIONS),
			hasValidBeanEqualsExcluding(ASSOCIATIONS),
			hasValidBeanToStringExcluding(ASSOCIATIONS)));
	}

	@Test
	void testBuilderMethods() {
		final var legalEntity = LegalEntityEntity.create().withLegalEntityId(10).withName("Galtströms Bruk");
		final var person = PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin");
		final var institution = InstitutionEntity.create().withId(3).withName("Sundsvalls museum");
		final var category = CategoryEntity.create().withCategoryId(5).withName("Företag");
		final var topography = TopographyEntity.create().withId(4).withName("Njurunda");
		final var subject = OcmEntity.create().withId(20).withText("Musik");

		final var result = NodeAttributesEntity.create()
			.withNodeId(100)
			.withLegalEntity(legalEntity)
			.withPerson(person)
			.withInstitution(institution)
			.withCategory(category)
			.withTopography(topography)
			.withVolumeNumber("001")
			.withShelfMeters(new BigDecimal("12.50"))
			.withVolumeCount(3)
			.withSubject(subject)
			.withOldSeriesSignum("A I")
			.withSeriesSignum("A1")
			.withAccessionNumber("ACC-1862")
			.withHoldingsCode("B1")
			.withLocationText("Okänd by")
			.withVolumePlacement("Hylla 3")
			.withHistoryFilename("arkiv_100_historik.xml");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getNodeId()).isEqualTo(100);
		assertThat(result.getLegalEntity()).isEqualTo(legalEntity);
		assertThat(result.getPerson()).isEqualTo(person);
		assertThat(result.getInstitution()).isEqualTo(institution);
		assertThat(result.getCategory()).isEqualTo(category);
		assertThat(result.getTopography()).isEqualTo(topography);
		assertThat(result.getVolumeNumber()).isEqualTo("001");
		assertThat(result.getShelfMeters()).isEqualByComparingTo("12.50");
		assertThat(result.getVolumeCount()).isEqualTo(3);
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getOldSeriesSignum()).isEqualTo("A I");
		assertThat(result.getSeriesSignum()).isEqualTo("A1");
		assertThat(result.getAccessionNumber()).isEqualTo("ACC-1862");
		assertThat(result.getHoldingsCode()).isEqualTo("B1");
		assertThat(result.getLocationText()).isEqualTo("Okänd by");
		assertThat(result.getVolumePlacement()).isEqualTo("Hylla 3");
		assertThat(result.getHistoryFilename()).isEqualTo("arkiv_100_historik.xml");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NodeAttributesEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new NodeAttributesEntity()).hasAllNullFieldsOrProperties();
	}
}
