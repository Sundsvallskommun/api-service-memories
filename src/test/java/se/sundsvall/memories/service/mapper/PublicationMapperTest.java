package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PublicationMapperTest {

	private static final ReferenceResolver NULL_LOOKUP = id -> null;

	private static PublicationEntity sampleEntity() {
		return PublicationEntity.create()
			.withPublicationId(207)
			.withFilename("alfwar-1841.xml")
			.withPublicationType("Tidningar")
			.withDate("1841-02-18")
			.withPeriodicalTitle("Alfwar och Skämt")
			.withIssueNumber("8")
			.withPageNumber("3")
			.withPublisherLocation("Sundsvall")
			.withPublisherTopographyId(1)
			.withDocumentTitle("Page 3 Alfwar och Skämt nr 8 1841")
			.withPublicationTypeId(4)
			.withLocationText("Sundsvall")
			.withComment("Archive comment")
			.withThumbnailFilename("PUBL.id_207_fil_liten.jpeg")
			.withLargeImageFilename("PUBL.id_207_fil_stor.jpeg")
			.withOcrFilename("PUBL.id_207_fil_txt.xml")
			.withXmltext("<text>OCR content</text>")
			.withNodeId(18407)
			.withOptions(4)
			.withFilFormat("text")
			.withDeletedDate(LocalDate.of(2026, 1, 15));
	}

	@Test
	void toPublicationSummaryExcludesXmltext() {
		final var result = PublicationMapper.toPublicationSummary(sampleEntity(), "Sundsvall", "Tidningar");

		assertThat(result).isNotNull();
		assertThat(result.getPublicationId()).isEqualTo(207);
		assertThat(result.getPublicationType()).isEqualTo("Tidningar");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getDocumentTitle()).isEqualTo("Page 3 Alfwar och Skämt nr 8 1841");
	}

	@Test
	void toPublicationIncludesXmltext() {
		final var result = PublicationMapper.toPublication(sampleEntity(), "Sundsvall", "Tidningar");

		assertThat(result).isNotNull();
		assertThat(result.getXmltext()).isEqualTo("<text>OCR content</text>");
		assertThat(result.getPublicationType()).isEqualTo("Tidningar");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
	}

	@Test
	void publicationTypeFallsBackToFritextWhenLookupReturnsNull() {
		final var entity = sampleEntity().withPublicationType("FritextTyp");

		final var result = PublicationMapper.toPublication(entity, "Sundsvall", null);

		assertThat(result.getPublicationType()).isEqualTo("FritextTyp");
	}

	@Test
	void publicationTypeFallsBackToFritextWhenLookupReturnsBlank() {
		final var entity = sampleEntity().withPublicationType("FritextTyp");

		final var result = PublicationMapper.toPublication(entity, "Sundsvall", "   ");

		assertThat(result.getPublicationType()).isEqualTo("FritextTyp");
	}

	@Test
	void toPublicationWithNullEntityReturnsNull() {
		assertThat(PublicationMapper.toPublicationSummary(null, "ignored", "ignored")).isNull();
		assertThat(PublicationMapper.toPublication(null, "ignored", "ignored")).isNull();
	}

	@Test
	void toPublicationListMapsAllEntitiesWithoutXmltext() {
		final var entities = List.of(
			PublicationEntity.create().withPublicationId(1).withPublisherTopographyId(10).withPublicationTypeId(100).withDocumentTitle("A").withXmltext("hidden"),
			PublicationEntity.create().withPublicationId(2).withPublisherTopographyId(20).withPublicationTypeId(200).withDocumentTitle("B").withXmltext("hidden"));
		final ReferenceResolver locationLookup = id -> id == 10 ? "Sundsvall" : "Timrå";
		final ReferenceResolver typeLookup = id -> id == 100 ? "Broschyrer" : "Tidningar";

		final var result = PublicationMapper.toPublicationList(entities, locationLookup, typeLookup);

		assertThat(result)
			.extracting("publicationId", "documentTitle", "publicationType", "location", "xmltext")
			.containsExactly(
				tuple(1, "A", "Broschyrer", "Sundsvall", null),
				tuple(2, "B", "Tidningar", "Timrå", null));
	}

	@Test
	void toPublicationListWithNullReturnsEmpty() {
		assertThat(PublicationMapper.toPublicationList(null, NULL_LOOKUP, NULL_LOOKUP)).isEmpty();
	}
}
