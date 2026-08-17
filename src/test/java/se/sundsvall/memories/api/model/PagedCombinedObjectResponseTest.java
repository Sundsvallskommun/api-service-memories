package se.sundsvall.memories.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
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

class PagedCombinedObjectResponseTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> Map.of("type-" + new Random().nextInt(), (long) new Random().nextInt(1000)), Map.class);
	}

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
		final var objects = java.util.List.of(CombinedObject.create().withObjectKey("foto-1"));
		final var meta = PagingAndSortingMetaData.create().withPage(1).withLimit(100);

		final var result = PagedCombinedObjectResponse.create()
			.withObjects(objects)
			.withTypeCounts(Map.of("Foto", 1L))
			.withMetaData(meta);

		assertThat(result.getObjects()).hasSize(1);
		assertThat(result.getTypeCounts()).containsEntry("Foto", 1L);
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PagedCombinedObjectResponse.create()).hasAllNullFieldsOrProperties();
	}
}
