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
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PagedCombinedObjectResponseTest {

	@Test
	void testBean() {
		assertThat(PagedCombinedObjectResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var objects = List.of(CombinedObject.create().withObjectKey("foto-1"));
		final var meta = PagingAndSortingMetaData.create().withPage(1).withLimit(100);

		final var result = PagedCombinedObjectResponse.create()
			.withObjects(objects)
			.withTypeCounts(List.of(ObjectTypeCount.create().withObjectType("Foto").withCount(1L)))
			.withGenderCounts(List.of(GenderCount.create().withGender("man").withCount(2L)))
			.withMetaData(meta);

		assertThat(result.getObjects()).hasSize(1);
		assertThat(result.getTypeCounts()).extracting(ObjectTypeCount::getObjectType, ObjectTypeCount::getCount)
			.containsExactly(tuple("Foto", 1L));
		assertThat(result.getGenderCounts()).extracting(GenderCount::getGender, GenderCount::getCount)
			.containsExactly(tuple("man", 2L));
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PagedCombinedObjectResponse.create()).hasAllNullFieldsOrProperties();
	}
}
