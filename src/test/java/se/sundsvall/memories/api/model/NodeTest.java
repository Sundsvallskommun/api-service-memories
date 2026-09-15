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
			.withOptions(6);

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
	}

	/** The fields read from the node's attribute row, built and read back separately from the tree's own. */
	@Test
	void testBuilderMethodsForTheAttributes() {
		final var creator = Creator.create().withLegalEntityId(10).withLegalEntity("Galtströms Bruk");
		final var subject = Subject.create().withCode("MUS").withText("Musik");

		final var result = Node.create()
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

		assertThat(result)
			.extracting(Node::getCreator, Node::getActivityStartDate, Node::getActivityEndDate, Node::getInstitutionId, Node::getInstitution, Node::getInstitutionCode,
				Node::getCategoryId, Node::getCategory, Node::getTopographyId, Node::getLocation, Node::getLocationText, Node::getSubject)
			.containsExactly(creator, "1673", "1916", 3, "Sundsvalls museum", "SVM", 5, "Företag", 4, "Kvissleby, Njurunda", "Okänd by", subject);
		assertThat(result)
			.extracting(Node::getSeriesSignum, Node::getOldSeriesSignum, Node::getVolumeNumber, Node::getVolumeCount, Node::getVolumePlacement,
				Node::getAccessionNumber, Node::getHoldingsCode, Node::getHistoryFilename)
			.containsExactly("A1", "A I", "001", 3, "Hylla 3", "ACC-1862", "B1", "arkiv_100_historik.xml");
		assertThat(result.getShelfMeters()).isEqualByComparingTo("12.50");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Node.create()).hasAllNullFieldsOrProperties();
	}
}
