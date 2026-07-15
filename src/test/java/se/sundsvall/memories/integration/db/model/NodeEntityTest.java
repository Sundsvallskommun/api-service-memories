package se.sundsvall.memories.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
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

class NodeEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(NodeEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = NodeEntity.create()
			.withId(100)
			.withParentId(1)
			.withName("Nödhjälpskommittén 1888-1889")
			.withNodeTypeId(2)
			.withRuId(555)
			.withStartYear(1888)
			.withStopYear(1889)
			.withOptions(6)
			.withSort(10)
			.withDescription("Arkivbeskrivning")
			.withSubItems(3)
			.withSubItems4(12)
			.withDeletedDate(deletedDate);

		assertThat(result.getId()).isEqualTo(100);
		assertThat(result.getParentId()).isEqualTo(1);
		assertThat(result.getName()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getNodeTypeId()).isEqualTo(2);
		assertThat(result.getRuId()).isEqualTo(555);
		assertThat(result.getStartYear()).isEqualTo(1888);
		assertThat(result.getStopYear()).isEqualTo(1889);
		assertThat(result.getOptions()).isEqualTo(6);
		assertThat(result.getSort()).isEqualTo(10);
		assertThat(result.getDescription()).isEqualTo("Arkivbeskrivning");
		assertThat(result.getSubItems()).isEqualTo(3);
		assertThat(result.getSubItems4()).isEqualTo(12);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NodeEntity.create()).hasAllNullFieldsOrProperties();
	}
}
