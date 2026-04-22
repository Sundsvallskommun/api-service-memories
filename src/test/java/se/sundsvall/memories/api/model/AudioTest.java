package se.sundsvall.memories.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
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

class AudioTest {

	private static final Random random = new Random();

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(random.nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(Audio.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var audioId = 1;
		final var filename = "interview1980.mp3";
		final var objectFilePath = "/path/to/interview.mp3";
		final var objectType = "LJUD";
		final var date = "1980-04-12";
		final var documentTitle = "Interview with mayor";
		final var topographyId = 2;
		final var locationText = "Sundsvall";
		final var location = "Sundsvall";
		final var subjectId = 3;
		final var subject = "Intervju";
		final var authorPersonId = 4;
		final var authorEntityId = 5;
		final var comment = "Audio recording";
		final var audioMimeType = "audio/mpeg";
		final var nodeId = 6;
		final var options = 4;
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = Audio.create()
			.withAudioId(audioId)
			.withFilename(filename)
			.withObjectFilePath(objectFilePath)
			.withObjectType(objectType)
			.withDate(date)
			.withDocumentTitle(documentTitle)
			.withTopographyId(topographyId)
			.withLocationText(locationText)
			.withLocation(location)
			.withSubjectId(subjectId)
			.withSubject(subject)
			.withAuthorPersonId(authorPersonId)
			.withAuthorEntityId(authorEntityId)
			.withComment(comment)
			.withAudioMimeType(audioMimeType)
			.withNodeId(nodeId)
			.withOptions(options)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getAudioId()).isEqualTo(audioId);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getObjectFilePath()).isEqualTo(objectFilePath);
		assertThat(result.getObjectType()).isEqualTo(objectType);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getTopographyId()).isEqualTo(topographyId);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getLocation()).isEqualTo(location);
		assertThat(result.getSubjectId()).isEqualTo(subjectId);
		assertThat(result.getSubject()).isEqualTo(subject);
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
		assertThat(Audio.create()).hasAllNullFieldsOrProperties();
	}
}
