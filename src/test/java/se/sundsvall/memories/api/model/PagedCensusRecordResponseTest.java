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

class PagedCensusRecordResponseTest {

	@Test
	void testBean() {
		assertThat(PagedCensusRecordResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var records = List.of(CensusRecord.create().withId(1));
		final var meta = PagingAndSortingMetaData.create().withPage(1).withLimit(100);

		final var result = PagedCensusRecordResponse.create()
			.withCensusRecords(records)
			.withMetaData(meta);

		assertThat(result.getCensusRecords()).hasSize(1);
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PagedCensusRecordResponse.create()).hasAllNullFieldsOrProperties();
	}
}
