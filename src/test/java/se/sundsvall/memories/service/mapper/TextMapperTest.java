package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class TextMapperTest {

	private static final ReferenceResolver NULL_LOOKUP = id -> null;

	private static TextEntity sampleEntity() {
		return TextEntity.create()
			.withTextId(1001)
			.withFilename("minne.xml")
			.withDocumentDate("1920-01-01")
			.withDocumentEndDate("1920-12-31")
			.withDocumentTitle("Minne från Sundsvall")
			.withTopography(TopographyEntity.create().withTId(4).withName("Sundsvall"))
			.withLocationText("Sundsvall")
			.withSubjectId(20)
			.withComment("Memoir")
			.withThumbnailFilename("TEXT.id_1001_fil_liten.jpeg")
			.withLargeImageFilename("TEXT.id_1001_fil_stor.jpeg")
			.withOcrFilename("TEXT.id_1001_fil_txt.xml")
			.withXmltext("<text>OCR content</text>")
			.withOptions(4);
	}

	@Test
	void toTextSummaryExcludesXmltextAndMediaFiles() {
		final var result = TextMapper.toTextSummary(sampleEntity(), "Musik");

		assertThat(result).isNotNull();
		assertThat(result.getTextId()).isEqualTo(1001);
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getSubjectId()).isEqualTo(20);
		assertThat(result.getSubject()).isEqualTo("Musik");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getMediaFiles()).isNull();
	}

	@Test
	void toTextIncludesXmltextAndMediaFiles() {
		final var mediaEntities = List.of(
			TextMediaEntity.create().withTextId(1001).withId(1).withThumbnailFilename("a-liten.jpg").withLargeImageFilename("a-stor.jpg").withOriginalFilename("a-orig.jpg"),
			TextMediaEntity.create().withTextId(1001).withId(2).withThumbnailFilename("b-liten.jpg"));

		final var result = TextMapper.toText(sampleEntity(), "Musik", mediaEntities);

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
		final var result = TextMapper.toText(sampleEntity(), "Musik", null);

		assertThat(result.getMediaFiles()).isEqualTo(emptyList());
	}

	@Test
	void toTextWithNullEntityReturnsNull() {
		assertThat(TextMapper.toTextSummary(null, "ignored")).isNull();
		assertThat(TextMapper.toText(null, "ignored", List.of())).isNull();
	}

	@Test
	void toTextListMapsAllEntities() {
		final var entities = List.of(
			TextEntity.create().withTextId(1).withSubjectId(100).withDocumentTitle("A").withXmltext("hidden")
				.withTopography(TopographyEntity.create().withTId(10).withName("Sundsvall")),
			TextEntity.create().withTextId(2).withSubjectId(200).withDocumentTitle("B").withXmltext("hidden")
				.withTopography(TopographyEntity.create().withTId(20).withName("Timrå")),
			TextEntity.create().withTextId(3).withSubjectId(300).withDocumentTitle("C").withXmltext("hidden"));
		final ReferenceResolver subjectLookup = id -> switch (id) {
			case 100 -> "Intervju";
			case 200 -> "Musik";
			default -> null;
		};

		final var result = TextMapper.toTextList(entities, subjectLookup);

		assertThat(result)
			.extracting("textId", "documentTitle", "location", "subject", "xmltext")
			.containsExactly(
				tuple(1, "A", "Sundsvall", "Intervju", null),
				tuple(2, "B", "Timrå", "Musik", null),
				tuple(3, "C", null, null, null));
	}

	@Test
	void toTextWithoutTopographyHasNoLocation() {
		// Both a text without a place and one whose D_T_ID points at a missing row arrive here as a null association —
		// see TextSpecificationTest for the dangling foreign key case.
		final var entity = TextEntity.create().withTextId(1).withLocationText("Sundsvall");

		final var result = TextMapper.toTextSummary(entity, null);

		assertThat(result.getLocation()).isNull();
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toTextListWithNullReturnsEmpty() {
		assertThat(TextMapper.toTextList(null, NULL_LOOKUP)).isEmpty();
	}

	@Test
	void toMediaFilesNullReturnsEmpty() {
		assertThat(TextMapper.toMediaFiles(null)).isEmpty();
	}
}
