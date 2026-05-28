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

class TextTest {

	@Test
	void testBean() {
		assertThat(Text.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var textId = 1001;
		final var filename = "minne.xml";
		final var documentDate = "1920-01-01";
		final var documentEndDate = "1920-12-31";
		final var documentTitle = "Minne från Sundsvall";
		final var locationText = "Sundsvall";
		final var location = "Sundsvalls kommun";
		final var subjectId = 20;
		final var subject = "Musik";
		final var comment = "Memoir";
		final var thumbnailFilename = "liten.jpg";
		final var largeImageFilename = "stor.jpg";
		final var ocrFilename = "txt.xml";
		final var xmltext = "<text>content</text>";
		final var mediaFiles = List.of(TextMediaFile.create().withThumbnailFilename("t.jpg"));

		final var result = Text.create()
			.withTextId(textId)
			.withFilename(filename)
			.withDocumentDate(documentDate)
			.withDocumentEndDate(documentEndDate)
			.withDocumentTitle(documentTitle)
			.withLocationText(locationText)
			.withLocation(location)
			.withSubjectId(subjectId)
			.withSubject(subject)
			.withComment(comment)
			.withThumbnailFilename(thumbnailFilename)
			.withLargeImageFilename(largeImageFilename)
			.withOcrFilename(ocrFilename)
			.withXmltext(xmltext)
			.withMediaFiles(mediaFiles);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTextId()).isEqualTo(textId);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getDocumentDate()).isEqualTo(documentDate);
		assertThat(result.getDocumentEndDate()).isEqualTo(documentEndDate);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getLocation()).isEqualTo(location);
		assertThat(result.getSubjectId()).isEqualTo(subjectId);
		assertThat(result.getSubject()).isEqualTo(subject);
		assertThat(result.getComment()).isEqualTo(comment);
		assertThat(result.getThumbnailFilename()).isEqualTo(thumbnailFilename);
		assertThat(result.getLargeImageFilename()).isEqualTo(largeImageFilename);
		assertThat(result.getOcrFilename()).isEqualTo(ocrFilename);
		assertThat(result.getXmltext()).isEqualTo(xmltext);
		assertThat(result.getMediaFiles()).isEqualTo(mediaFiles);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Text.create()).hasAllNullFieldsOrProperties();
	}
}
