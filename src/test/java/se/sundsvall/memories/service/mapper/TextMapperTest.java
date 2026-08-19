package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class TextMapperTest {

	private static TextEntity sampleEntity() {
		return TextEntity.create()
			.withId(1001)
			.withFilename("minne.xml")
			.withDocumentDate("1920-01-01")
			.withDocumentEndDate("1920-12-31")
			.withDocumentTitle("Minne från Sundsvall")
			.withTopography(TopographyEntity.create().withId(4).withName("Sundsvall"))
			.withLocationText("Sundsvall")
			.withSubject(OcmEntity.create().withId(20).withText("Musik"))
			.withComment("Memoir")
			.withCreatorPerson(PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin"))
			.withCreatorLegalEntity(LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén 1888-1889"))
			.withThumbnailFilename("TEXT.id_1001_fil_liten.jpeg")
			.withLargeImageFilename("TEXT.id_1001_fil_stor.jpeg")
			.withOcrFilename("TEXT.id_1001_fil_txt.xml")
			.withXmltext("<text>OCR content</text>")
			.withOptions(4);
	}

	@Test
	void toTextSummaryExcludesXmltextAndMediaFiles() {
		final var result = TextMapper.toTextSummary(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getTextId()).isEqualTo(1001);
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getSubjectId()).isEqualTo(20);
		assertThat(result.getSubject()).isEqualTo("Musik");
		assertThat(result.getCreator().getPersonId()).isEqualTo(1);
		assertThat(result.getCreator().getPerson()).isEqualTo("Anton Nordin");
		assertThat(result.getCreator().getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getMediaFiles()).isNull();
	}

	@Test
	void toTextIncludesXmltextAndMediaFiles() {
		final var mediaEntities = List.of(
			TextMediaEntity.create().withTextId(1001).withId(1).withThumbnailFilename("a-liten.jpg").withLargeImageFilename("a-stor.jpg").withOriginalFilename("a-orig.jpg"),
			TextMediaEntity.create().withTextId(1001).withId(2).withThumbnailFilename("b-liten.jpg"));

		final var result = TextMapper.toText(sampleEntity(), mediaEntities);

		assertThat(result).isNotNull();
		assertThat(result.getXmltext()).isEqualTo("<text>OCR content</text>");
		assertThat(result.getSubject()).isEqualTo("Musik");
		assertThat(result.getMediaFiles()).hasSize(2);
		assertThat(result.getMediaFiles())
			.extracting("id", "thumbnailFilename", "largeImageFilename", "originalFilename")
			.containsExactly(
				tuple(1, "a-liten.jpg", "a-stor.jpg", "a-orig.jpg"),
				tuple(2, "b-liten.jpg", null, null));
	}

	@Test
	void toTextWithNullMediaListReturnsEmpty() {
		final var result = TextMapper.toText(sampleEntity(), null);

		assertThat(result.getMediaFiles()).isEqualTo(emptyList());
	}

	@Test
	void toTextWithNullEntityReturnsNull() {
		assertThat(TextMapper.toTextSummary(null)).isNull();
		assertThat(TextMapper.toText(null, List.of())).isNull();
	}

	@Test
	void toTextListMapsAllEntities() {
		final var entities = List.of(
			TextEntity.create().withId(1).withDocumentTitle("A").withXmltext("hidden")
				.withTopography(TopographyEntity.create().withId(10).withName("Sundsvall"))
				.withSubject(OcmEntity.create().withId(100).withText("Intervju")),
			TextEntity.create().withId(2).withDocumentTitle("B").withXmltext("hidden")
				.withTopography(TopographyEntity.create().withId(20).withName("Timrå"))
				.withSubject(OcmEntity.create().withId(200).withText("Musik")),
			TextEntity.create().withId(3).withDocumentTitle("C").withXmltext("hidden"));

		final var result = TextMapper.toTextList(entities);

		assertThat(result)
			.extracting("textId", "documentTitle", "location", "subjectId", "subject", "xmltext")
			.containsExactly(
				tuple(1, "A", "Sundsvall", 100, "Intervju", null),
				tuple(2, "B", "Timrå", 200, "Musik", null),
				tuple(3, "C", null, null, null, null));
	}

	@Test
	void toTextWithoutTopographyHasNoLocation() {
		// Both a text without a place and one whose D_T_ID points at a missing row arrive here as a null association —
		// see TextSpecificationTest for the dangling foreign key case.
		final var entity = TextEntity.create().withId(1).withLocationText("Sundsvall");

		final var result = TextMapper.toTextSummary(entity);

		assertThat(result.getLocation()).isNull();
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toTextListWithNullReturnsEmpty() {
		assertThat(TextMapper.toTextList(null)).isEmpty();
	}

	@Test
	void toMediaFilesNullReturnsEmpty() {
		assertThat(TextMapper.toMediaFiles(null)).isEmpty();
	}
}
