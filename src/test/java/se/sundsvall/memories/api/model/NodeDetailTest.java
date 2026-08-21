package se.sundsvall.memories.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class NodeDetailTest {

	@Test
	void testBean() {
		assertThat(NodeDetail.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var node = Node.create().withId(111).withName("Volym 1");
		final var path = List.of(Node.create().withId(100), Node.create().withId(110));

		final var result = NodeDetail.create()
			.withNode(node)
			.withPath(path);

		assertThat(result.getNode()).isEqualTo(node);
		assertThat(result.getPath()).isEqualTo(path);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NodeDetail.create()).hasAllNullFieldsOrProperties();
	}
}
