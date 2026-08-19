package se.sundsvall.memories.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
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
			hasValidBeanHashCodeExcluding("nodeType"),
			hasValidBeanEqualsExcluding("nodeType"),
			hasValidBeanToStringExcluding("nodeType")));
	}

	@Test
	void testBuilderMethods() {
		final var nodeType = NodeTypeEntity.create().withId(1).withName("Arkiv");
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = NodeEntity.create()
			.withId(100)
			.withParentId(10)
			.withName("Sundsvalls stads arkiv")
			.withNodeType(nodeType)
			.withStartYear(1862)
			.withStopYear(1951)
			.withDescription("Handlingar från stadsfullmäktige")
			.withSortOrder(10)
			.withSubItemCount(42)
			.withPublishedSubItemCount(40)
			.withOptions(6)
			.withDeletedDate(deletedDate);

		assertThat(result.getId()).isEqualTo(100);
		assertThat(result.getParentId()).isEqualTo(10);
		assertThat(result.getName()).isEqualTo("Sundsvalls stads arkiv");
		assertThat(result.getNodeType()).isEqualTo(nodeType);
		assertThat(result.getStartYear()).isEqualTo(1862);
		assertThat(result.getStopYear()).isEqualTo(1951);
		assertThat(result.getDescription()).isEqualTo("Handlingar från stadsfullmäktige");
		assertThat(result.getSortOrder()).isEqualTo(10);
		assertThat(result.getSubItemCount()).isEqualTo(42);
		assertThat(result.getPublishedSubItemCount()).isEqualTo(40);
		assertThat(result.getOptions()).isEqualTo(6);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NodeEntity.create()).hasAllNullFieldsOrProperties();
	}
}
