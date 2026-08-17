package se.sundsvall.memories.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PagedSeamanResponseTest {

	@Test
	void testBean() {
		assertThat(PagedSeamanResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var seamen = List.of(Seaman.create().withId(1));
		final var meta = PagingAndSortingMetaData.create().withPage(1).withLimit(100);

		final var result = PagedSeamanResponse.create()
			.withSeamen(seamen)
			.withMetaData(meta);

		assertThat(result.getSeamen()).hasSize(1);
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PagedSeamanResponse.create()).hasAllNullFieldsOrProperties();
	}
}
