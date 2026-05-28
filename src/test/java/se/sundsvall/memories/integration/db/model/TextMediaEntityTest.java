package se.sundsvall.memories.integration.db.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class TextMediaEntityTest {

	@Test
	void testBean() {
		assertThat(TextMediaEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = TextMediaEntity.create()
			.withId(1)
			.withTextId(1001)
			.withThumbnailFilename("liten.jpg")
			.withLargeImageFilename("stor.jpg")
			.withOriginalFilename("orig.jpg");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getTextId()).isEqualTo(1001);
		assertThat(result.getThumbnailFilename()).isEqualTo("liten.jpg");
		assertThat(result.getLargeImageFilename()).isEqualTo("stor.jpg");
		assertThat(result.getOriginalFilename()).isEqualTo("orig.jpg");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TextMediaEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new TextMediaEntity()).hasAllNullFieldsOrProperties();
	}
}
