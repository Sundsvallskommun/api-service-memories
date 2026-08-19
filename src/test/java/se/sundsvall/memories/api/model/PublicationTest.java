package se.sundsvall.memories.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PublicationTest {

	@Test
	void testBean() {
		assertThat(Publication.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var publicationId = 207;
		final var filename = "alfwar-1841.xml";
		final var publicationType = "";
		final var date = "1841-02-18";
		final var periodicalTitle = "Alfwar och Skämt";
		final var issueNumber = "8";
		final var pageNumber = "3";
		final var publisherLocation = "Sundsvall";
		final var documentTitle = "Page 3 Alfwar och Skämt nr 8 1841";
		final var locationText = "Sundsvall";
		final var location = "Sundsvalls kommun";
		final var comment = "Archive comment";
		final var thumbnailFilename = "PUBL.id_207_fil_liten.jpeg";
		final var largeImageFilename = "PUBL.id_207_fil_stor.jpeg";
		final var ocrFilename = "PUBL.id_207_fil_txt.xml";
		final var xmltext = "<text>OCR content</text>";

		final var result = Publication.create()
			.withPublicationId(publicationId)
			.withFilename(filename)
			.withPublicationType(publicationType)
			.withDate(date)
			.withPeriodicalTitle(periodicalTitle)
			.withIssueNumber(issueNumber)
			.withPageNumber(pageNumber)
			.withPublisherLocation(publisherLocation)
			.withDocumentTitle(documentTitle)
			.withLocationText(locationText)
			.withLocation(location)
			.withComment(comment)
			.withCreator(Creator.create().withPersonId(1).withPerson("Anton Nordin").withLegalEntityId(10).withLegalEntity("Nödhjälpskommittén 1888-1889"))
			.withThumbnailFilename(thumbnailFilename)
			.withLargeImageFilename(largeImageFilename)
			.withOcrFilename(ocrFilename)
			.withXmltext(xmltext);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCreator().getPerson()).isEqualTo("Anton Nordin");
		assertThat(result.getCreator().getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getPublicationId()).isEqualTo(publicationId);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getPublicationType()).isEqualTo(publicationType);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getPeriodicalTitle()).isEqualTo(periodicalTitle);
		assertThat(result.getIssueNumber()).isEqualTo(issueNumber);
		assertThat(result.getPageNumber()).isEqualTo(pageNumber);
		assertThat(result.getPublisherLocation()).isEqualTo(publisherLocation);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getLocation()).isEqualTo(location);
		assertThat(result.getComment()).isEqualTo(comment);
		assertThat(result.getThumbnailFilename()).isEqualTo(thumbnailFilename);
		assertThat(result.getLargeImageFilename()).isEqualTo(largeImageFilename);
		assertThat(result.getOcrFilename()).isEqualTo(ocrFilename);
		assertThat(result.getXmltext()).isEqualTo(xmltext);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Publication.create()).hasAllNullFieldsOrProperties();
	}
}
