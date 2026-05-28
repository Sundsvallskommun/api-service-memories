package se.sundsvall.memories.integration.db.model;

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

class TextEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(TextEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var textId = 1001;
		final var documentDate = "1920-01-01";
		final var documentEndDate = "1920-12-31";
		final var documentTitle = "Minne från Sundsvall";
		final var ueId = 1;
		final var ujId = 2;
		final var topographyId = 3;
		final var locationText = "Sundsvall";
		final var subjectId = 7;
		final var comment = "Memoir";
		final var filename = "minne.xml";
		final var thumbnailFilename = "TEXT.id_1001_fil_liten.jpeg";
		final var largeImageFilename = "TEXT.id_1001_fil_stor.jpeg";
		final var originalFilename = "TEXT.id_1001_fil_original.jpeg";
		final var ocrFilename = "TEXT.id_1001_fil_txt.xml";
		final var xmltext = "<text>content</text>";
		final var filXtra = "TEXT.id_1001_fil_xtra.jpeg";
		final var nodeId = 5000;
		final var options = 4;
		final var filFormat = "text";
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = TextEntity.create()
			.withTextId(textId)
			.withDocumentDate(documentDate)
			.withDocumentEndDate(documentEndDate)
			.withDocumentTitle(documentTitle)
			.withUeId(ueId)
			.withUjId(ujId)
			.withTopographyId(topographyId)
			.withLocationText(locationText)
			.withSubjectId(subjectId)
			.withComment(comment)
			.withFilename(filename)
			.withThumbnailFilename(thumbnailFilename)
			.withLargeImageFilename(largeImageFilename)
			.withOriginalFilename(originalFilename)
			.withOcrFilename(ocrFilename)
			.withXmltext(xmltext)
			.withFilXtra(filXtra)
			.withNodeId(nodeId)
			.withOptions(options)
			.withFilFormat(filFormat)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTextId()).isEqualTo(textId);
		assertThat(result.getDocumentDate()).isEqualTo(documentDate);
		assertThat(result.getDocumentEndDate()).isEqualTo(documentEndDate);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getUeId()).isEqualTo(ueId);
		assertThat(result.getUjId()).isEqualTo(ujId);
		assertThat(result.getTopographyId()).isEqualTo(topographyId);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getSubjectId()).isEqualTo(subjectId);
		assertThat(result.getComment()).isEqualTo(comment);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getThumbnailFilename()).isEqualTo(thumbnailFilename);
		assertThat(result.getLargeImageFilename()).isEqualTo(largeImageFilename);
		assertThat(result.getOriginalFilename()).isEqualTo(originalFilename);
		assertThat(result.getOcrFilename()).isEqualTo(ocrFilename);
		assertThat(result.getXmltext()).isEqualTo(xmltext);
		assertThat(result.getFilXtra()).isEqualTo(filXtra);
		assertThat(result.getNodeId()).isEqualTo(nodeId);
		assertThat(result.getOptions()).isEqualTo(options);
		assertThat(result.getFilFormat()).isEqualTo(filFormat);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TextEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new TextEntity()).hasAllNullFieldsOrProperties();
	}
}
