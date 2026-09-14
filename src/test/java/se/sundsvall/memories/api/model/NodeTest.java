package se.sundsvall.memories.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.math.BigDecimal;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class NodeTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(100000), 2), BigDecimal.class);
	}

	@Test
	void testBean() {
		assertThat(Node.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var creator = Creator.create().withLegalEntityId(10).withLegalEntity("Galtströms Bruk");
		final var subject = Subject.create().withCode("MUS").withText("Musik");

		final var result = Node.create()
			.withId(100)
			.withParentId(10)
			.withName("Sundsvalls stads arkiv")
			.withNodeTypeId(1)
			.withNodeType("Arkiv")
			.withStartYear(1862)
			.withStopYear(1951)
			.withDescription("Handlingar från stadsfullmäktige")
			.withSortOrder(10)
			.withSubItemCount(42)
			.withPublishedSubItemCount(40)
			.withOptions(6)
			.withCreator(creator)
			.withActivityStartDate("1673")
			.withActivityEndDate("1916")
			.withInstitutionId(3)
			.withInstitution("Sundsvalls museum")
			.withInstitutionCode("SVM")
			.withCategoryId(5)
			.withCategory("Företag")
			.withTopographyId(4)
			.withLocation("Kvissleby, Njurunda")
			.withLocationText("Okänd by")
			.withSubject(subject)
			.withSeriesSignum("A1")
			.withOldSeriesSignum("A I")
			.withVolumeNumber("001")
			.withVolumeCount(3)
			.withShelfMeters(new BigDecimal("12.50"))
			.withVolumePlacement("Hylla 3")
			.withAccessionNumber("ACC-1862")
			.withHoldingsCode("B1")
			.withHistoryFilename("arkiv_100_historik.xml");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(100);
		assertThat(result.getParentId()).isEqualTo(10);
		assertThat(result.getName()).isEqualTo("Sundsvalls stads arkiv");
		assertThat(result.getNodeTypeId()).isEqualTo(1);
		assertThat(result.getNodeType()).isEqualTo("Arkiv");
		assertThat(result.getStartYear()).isEqualTo(1862);
		assertThat(result.getStopYear()).isEqualTo(1951);
		assertThat(result.getDescription()).isEqualTo("Handlingar från stadsfullmäktige");
		assertThat(result.getSortOrder()).isEqualTo(10);
		assertThat(result.getSubItemCount()).isEqualTo(42);
		assertThat(result.getPublishedSubItemCount()).isEqualTo(40);
		assertThat(result.getOptions()).isEqualTo(6);
		assertThat(result.getCreator()).isEqualTo(creator);
		assertThat(result.getActivityStartDate()).isEqualTo("1673");
		assertThat(result.getActivityEndDate()).isEqualTo("1916");
		assertThat(result.getInstitutionId()).isEqualTo(3);
		assertThat(result.getInstitution()).isEqualTo("Sundsvalls museum");
		assertThat(result.getInstitutionCode()).isEqualTo("SVM");
		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getCategory()).isEqualTo("Företag");
		assertThat(result.getTopographyId()).isEqualTo(4);
		assertThat(result.getLocation()).isEqualTo("Kvissleby, Njurunda");
		assertThat(result.getLocationText()).isEqualTo("Okänd by");
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getSeriesSignum()).isEqualTo("A1");
		assertThat(result.getOldSeriesSignum()).isEqualTo("A I");
		assertThat(result.getVolumeNumber()).isEqualTo("001");
		assertThat(result.getVolumeCount()).isEqualTo(3);
		assertThat(result.getShelfMeters()).isEqualByComparingTo("12.50");
		assertThat(result.getVolumePlacement()).isEqualTo("Hylla 3");
		assertThat(result.getAccessionNumber()).isEqualTo("ACC-1862");
		assertThat(result.getHoldingsCode()).isEqualTo("B1");
		assertThat(result.getHistoryFilename()).isEqualTo("arkiv_100_historik.xml");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Node.create()).hasAllNullFieldsOrProperties();
	}
}
