package se.sundsvall.memories.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class NodeParametersTest {

	@Test
	void testBean() {
		assertThat(NodeParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = NodeParameters.create()
			.withQuery("stadsfullmäktige")
			.withNodeTypeId(1)
			.withYearFrom(1862)
			.withYearTo(1951)
			.withPage(2)
			.withLimit(25);

		assertThat(result.getQuery()).isEqualTo("stadsfullmäktige");
		assertThat(result.getNodeTypeId()).isEqualTo(1);
		assertThat(result.getYearFrom()).isEqualTo(1862);
		assertThat(result.getYearTo()).isEqualTo(1951);
		assertThat(result.getPage()).isEqualTo(2);
		assertThat(result.getLimit()).isEqualTo(25);
	}

	@Test
	void testDefaults() {
		final var result = NodeParameters.create();

		assertThat(result.getPage()).isEqualTo(1);
		assertThat(result.getLimit()).isEqualTo(100);
		assertThat(result.getQuery()).isNull();
		assertThat(result.getNodeTypeId()).isNull();
		assertThat(result.getYearFrom()).isNull();
		assertThat(result.getYearTo()).isNull();
	}

	@Test
	void testSort() {
		final var parameters = NodeParameters.create();
		parameters.setSortBy(List.of("name"));
		parameters.setSortDirection(Sort.Direction.DESC);

		assertThat(parameters.sort()).isEqualTo(Sort.by(Sort.Direction.DESC, "name"));
		assertThat(parameters.getSortBy()).containsExactly("name");
	}
}
