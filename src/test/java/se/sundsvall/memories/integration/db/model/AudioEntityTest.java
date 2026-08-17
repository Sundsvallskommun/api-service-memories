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
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class AudioEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(AudioEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("topography", "subject"),
			hasValidBeanEqualsExcluding("topography", "subject"),
			hasValidBeanToStringExcluding("topography", "subject")));
	}

	@Test
	void testBuilderMethods() {
		final var audioId = 1;
		final var filename = "interview1980.mp3";
		final var objectFilePath = "/path/to/interview.mp3";
		final var objectType = "LJUD";
		final var date = "1980-04-12";
		final var documentTitle = "Intervju med borgmästaren";
		final var topographyId = 2;
		final var locationText = "Sundsvall";
		final var subjectId = 3;
		final var authorPersonId = 4;
		final var authorEntityId = 5;
		final var comment = "Ljudupptagning från intervju";
		final var audioMimeType = "audio/mpeg";
		final var nodeId = 6;
		final var options = 4;
		final var deletedDate = LocalDate.of(2026, JANUARY, 15);

		final var result = AudioEntity.create()
			.withId(audioId)
			.withFilename(filename)
			.withObjectFilePath(objectFilePath)
			.withObjectType(objectType)
			.withDate(date)
			.withDocumentTitle(documentTitle)
			.withTopography(TopographyEntity.create().withId(topographyId).withName("Sundsvall"))
			.withLocationText(locationText)
			.withSubject(OcmEntity.create().withId(subjectId).withText("Musik"))
			.withAuthorPersonId(authorPersonId)
			.withAuthorEntityId(authorEntityId)
			.withComment(comment)
			.withAudioMimeType(audioMimeType)
			.withNodeId(nodeId)
			.withOptions(options)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(audioId);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getObjectFilePath()).isEqualTo(objectFilePath);
		assertThat(result.getObjectType()).isEqualTo(objectType);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getTopography().getId()).isEqualTo(topographyId);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getSubject().getId()).isEqualTo(subjectId);
		assertThat(result.getAuthorPersonId()).isEqualTo(authorPersonId);
		assertThat(result.getAuthorEntityId()).isEqualTo(authorEntityId);
		assertThat(result.getComment()).isEqualTo(comment);
		assertThat(result.getAudioMimeType()).isEqualTo(audioMimeType);
		assertThat(result.getNodeId()).isEqualTo(nodeId);
		assertThat(result.getOptions()).isEqualTo(options);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AudioEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new AudioEntity()).hasAllNullFieldsOrProperties();
	}
}
