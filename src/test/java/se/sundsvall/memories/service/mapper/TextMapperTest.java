package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;

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
			.withTopographyId(4)
			.withLocationText("Sundsvall")
			.withComment("Memoir")
			.withThumbnailFilename("TEXT.id_1001_fil_liten.jpeg")
			.withLargeImageFilename("TEXT.id_1001_fil_stor.jpeg")
			.withOcrFilename("TEXT.id_1001_fil_txt.xml")
			.withXmltext("<text>OCR content</text>")
			.withOptions(4);
	}

	@Test
	void toTextSummaryExcludesXmltextAndMediaFiles() {
		final var result = TextMapper.toTextSummary(sampleEntity(), "Sundsvall");

		assertThat(result).isNotNull();
		assertThat(result.getTextId()).isEqualTo(1001);
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getMediaFiles()).isNull();
	}

	@Test
	void toTextIncludesXmltextAndMediaFiles() {
		final var mediaEntities = List.of(
			TextMediaEntity.create().withTextId(1001).withThumbnailFilename("a-liten.jpg").withLargeImageFilename("a-stor.jpg").withOriginalFilename("a-orig.jpg"),
			TextMediaEntity.create().withTextId(1001).withThumbnailFilename("b-liten.jpg"));

		final var result = TextMapper.toText(sampleEntity(), "Sundsvall", mediaEntities);

		assertThat(result).isNotNull();
		assertThat(result.getXmltext()).isEqualTo("<text>OCR content</text>");
		assertThat(result.getMediaFiles()).hasSize(2);
		assertThat(result.getMediaFiles())
			.extracting("thumbnailFilename", "largeImageFilename", "originalFilename")
			.containsExactly(
				tuple("a-liten.jpg", "a-stor.jpg", "a-orig.jpg"),
				tuple("b-liten.jpg", null, null));
	}

	@Test
	void toTextWithNullMediaListReturnsEmpty() {
		final var result = TextMapper.toText(sampleEntity(), "Sundsvall", null);

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
			TextEntity.create().withTextId(1).withTopographyId(10).withDocumentTitle("A").withXmltext("hidden"),
			TextEntity.create().withTextId(2).withTopographyId(20).withDocumentTitle("B").withXmltext("hidden"));
		final ReferenceResolver lookup = id -> id == 10 ? "Sundsvall" : "Timrå";

		final var result = TextMapper.toTextList(entities, lookup);

		assertThat(result)
			.extracting("textId", "documentTitle", "location", "xmltext")
			.containsExactly(
				tuple(1, "A", "Sundsvall", null),
				tuple(2, "B", "Timrå", null));
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
