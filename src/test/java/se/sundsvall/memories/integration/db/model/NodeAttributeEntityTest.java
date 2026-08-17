package se.sundsvall.memories.integration.db.model;

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

class NodeAttributeEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(10000)), BigDecimal.class);
	}

	@Test
	void testBean() {
		assertThat(NodeAttributeEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = NodeAttributeEntity.create()
			.withNodeId(100)
			.withLegalEntityId(5)
			.withPersonId(6)
			.withInstitutionId(7)
			.withCategoryId(8)
			.withTopographyId(9)
			.withVolumeNumber("A1:3")
			.withShelfMeters(new BigDecimal("1.50"))
			.withVolumeCount(4)
			.withSubjectId(11)
			.withOldSeriesSignum("Ö-1")
			.withNewSeriesSignum("N-1")
			.withAccessionNumber("ACC-1")
			.withHoldingsCode("B-CODE")
			.withUndeterminedPlace("Okänd plats")
			.withVolumePlacement("Hylla 5")
			.withSubItems(2)
			.withBlobFilename("nod_100_blob.xml")
			.withBlobFormat("xml");

		assertThat(result.getNodeId()).isEqualTo(100);
		assertThat(result.getLegalEntityId()).isEqualTo(5);
		assertThat(result.getPersonId()).isEqualTo(6);
		assertThat(result.getInstitutionId()).isEqualTo(7);
		assertThat(result.getCategoryId()).isEqualTo(8);
		assertThat(result.getTopographyId()).isEqualTo(9);
		assertThat(result.getVolumeNumber()).isEqualTo("A1:3");
		assertThat(result.getShelfMeters()).isEqualTo(new BigDecimal("1.50"));
		assertThat(result.getVolumeCount()).isEqualTo(4);
		assertThat(result.getSubjectId()).isEqualTo(11);
		assertThat(result.getOldSeriesSignum()).isEqualTo("Ö-1");
		assertThat(result.getNewSeriesSignum()).isEqualTo("N-1");
		assertThat(result.getAccessionNumber()).isEqualTo("ACC-1");
		assertThat(result.getHoldingsCode()).isEqualTo("B-CODE");
		assertThat(result.getUndeterminedPlace()).isEqualTo("Okänd plats");
		assertThat(result.getVolumePlacement()).isEqualTo("Hylla 5");
		assertThat(result.getSubItems()).isEqualTo(2);
		assertThat(result.getBlobFilename()).isEqualTo("nod_100_blob.xml");
		assertThat(result.getBlobFormat()).isEqualTo("xml");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NodeAttributeEntity.create()).hasAllNullFieldsOrProperties();
	}
}
