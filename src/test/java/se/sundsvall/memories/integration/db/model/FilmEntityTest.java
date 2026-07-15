package se.sundsvall.memories.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
import java.time.Month;
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

class FilmEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(FilmEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var filmId = 1;
		final var filename = "film001.mp4";
		final var objectFilePath = "/path/to/file.mp4";
		final var objectType = "VIDEO";
		final var date = "1985-06-15";
		final var documentTitle = "Midsummer celebration in Sundsvall";
		final var topographyId = 2;
		final var locationText = "Sundsvall";
		final var organizationId = 3;
		final var subEntityId = 4;
		final var unitId = 5;
		final var comment = "A film about midsummer celebrations";
		final var filmMimeType = "video/mp4";
		final var nodeId = 6;
		final var options = 0;
		final var deletedDate = LocalDate.of(2026, Month.JANUARY, 15);

		final var result = FilmEntity.create()
			.withFilmId(filmId)
			.withFilename(filename)
			.withObjectFilePath(objectFilePath)
			.withObjectType(objectType)
			.withDate(date)
			.withDocumentTitle(documentTitle)
			.withTopographyId(topographyId)
			.withLocationText(locationText)
			.withOrganizationId(organizationId)
			.withSubEntityId(subEntityId)
			.withUnitId(unitId)
			.withComment(comment)
			.withFilmMimeType(filmMimeType)
			.withNodeId(nodeId)
			.withOptions(options)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getFilmId()).isEqualTo(filmId);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getObjectFilePath()).isEqualTo(objectFilePath);
		assertThat(result.getObjectType()).isEqualTo(objectType);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getTopographyId()).isEqualTo(topographyId);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getOrganizationId()).isEqualTo(organizationId);
		assertThat(result.getSubEntityId()).isEqualTo(subEntityId);
		assertThat(result.getUnitId()).isEqualTo(unitId);
		assertThat(result.getComment()).isEqualTo(comment);
		assertThat(result.getFilmMimeType()).isEqualTo(filmMimeType);
		assertThat(result.getNodeId()).isEqualTo(nodeId);
		assertThat(result.getOptions()).isEqualTo(options);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FilmEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new FilmEntity()).hasAllNullFieldsOrProperties();
	}
}
