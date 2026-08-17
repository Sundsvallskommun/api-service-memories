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

class PublicationEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(PublicationEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var publicationId = 207;
		final var filename = "F21051/1841-02-18_Sida 3 Alfwar och Skämt nr 8 1841.xml";
		final var publicationType = "";
		final var date = "1841-02-18";
		final var periodicalTitle = "Alfwar och Skämt";
		final var issueNumber = "8";
		final var pageNumber = "3";
		final var bfJId = 1;
		final var publisherTopographyId = 1;
		final var publisherLocation = "Sundsvall";
		final var documentDate = "1841-02-18";
		final var documentTitle = "Page 3 Alfwar och Skämt nr 8 1841";
		final var feId = 0;
		final var reId = 0;
		final var ujId = 1;
		final var ueId = 0;
		final var topographyId = 4;
		final var locationText = "Sundsvall";
		final var meOId = 1;
		final var comment = "Archive comment";
		final var thumbnailFilename = "PUBL.id_207_fil_liten.jpeg";
		final var largeImageFilename = "PUBL.id_207_fil_stor.jpeg";
		final var originalFilename = "PUBL.id_207_fil_original.jpeg";
		final var ocrFilename = "PUBL.id_207_fil_txt.xml";
		final var xmltext = "<text>OCR content</text>";
		final var filXtra = "PUBL.id_207_fil_xtra.jpeg";
		final var nodeId = 18407;
		final var options = 4;
		final var filFormat = "text";
		final var deletedDate = LocalDate.of(2026, Month.JANUARY, 15);

		final var result = PublicationEntity.create()
			.withPublicationId(publicationId)
			.withFilename(filename)
			.withPublicationType(publicationType)
			.withDate(date)
			.withPeriodicalTitle(periodicalTitle)
			.withIssueNumber(issueNumber)
			.withPageNumber(pageNumber)
			.withBfJId(bfJId)
			.withPublisherTopographyId(publisherTopographyId)
			.withPublisherLocation(publisherLocation)
			.withDocumentDate(documentDate)
			.withDocumentTitle(documentTitle)
			.withFeId(feId)
			.withReId(reId)
			.withUjId(ujId)
			.withUeId(ueId)
			.withTopographyId(topographyId)
			.withLocationText(locationText)
			.withMeOId(meOId)
			.withComment(comment)
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
		assertThat(result.getPublicationId()).isEqualTo(publicationId);
		assertThat(result.getFilename()).isEqualTo(filename);
		assertThat(result.getPublicationType()).isEqualTo(publicationType);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getPeriodicalTitle()).isEqualTo(periodicalTitle);
		assertThat(result.getIssueNumber()).isEqualTo(issueNumber);
		assertThat(result.getPageNumber()).isEqualTo(pageNumber);
		assertThat(result.getBfJId()).isEqualTo(bfJId);
		assertThat(result.getPublisherTopographyId()).isEqualTo(publisherTopographyId);
		assertThat(result.getPublisherLocation()).isEqualTo(publisherLocation);
		assertThat(result.getDocumentDate()).isEqualTo(documentDate);
		assertThat(result.getDocumentTitle()).isEqualTo(documentTitle);
		assertThat(result.getFeId()).isEqualTo(feId);
		assertThat(result.getReId()).isEqualTo(reId);
		assertThat(result.getUjId()).isEqualTo(ujId);
		assertThat(result.getUeId()).isEqualTo(ueId);
		assertThat(result.getTopographyId()).isEqualTo(topographyId);
		assertThat(result.getLocationText()).isEqualTo(locationText);
		assertThat(result.getMeOId()).isEqualTo(meOId);
		assertThat(result.getComment()).isEqualTo(comment);
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
		assertThat(PublicationEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new PublicationEntity()).hasAllNullFieldsOrProperties();
	}
}
