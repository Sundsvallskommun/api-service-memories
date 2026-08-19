package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PublicationMapperTest {

	private static PublicationEntity sampleEntity() {
		final var entity = PublicationEntity.create()
			.withId(207)
			.withFilename("alfwar-1841.xml")
			.withPublicationType("Tidningar")
			.withDate("1841-02-18")
			.withPeriodicalTitle("Alfwar och Skämt")
			.withIssueNumber("8")
			.withPageNumber("3")
			.withPublisherLocation("Sundsvall")
			.withDocumentTitle("Page 3 Alfwar och Skämt nr 8 1841")
			.withTopography(TopographyEntity.create().withId(4).withName("Sundsvall"))
			.withLocationText("Sundsvall")
			.withComment("Archive comment")
			.withThumbnailFilename("PUBL.id_207_fil_liten.jpeg")
			.withLargeImageFilename("PUBL.id_207_fil_stor.jpeg")
			.withOcrFilename("PUBL.id_207_fil_txt.xml")
			.withXmltext("<text>OCR content</text>")
			.withNodeId(18407)
			.withOptions(4)
			.withFilFormat("text")
			.withDeletedDate(LocalDate.of(2026, Month.JANUARY, 15));

		// the originator associations carry no fluent builder: the application only ever reads them
		entity.setCreatorPerson(PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin"));
		entity.setCreatorLegalEntity(LegalEntityEntity.create().withLegalEntityId(10).withName("Nödhjälpskommittén 1888-1889"));

		return entity;
	}

	@Test
	void toPublicationSummaryExcludesXmltext() {
		final var result = PublicationMapper.toPublicationSummary(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getCreator().getPersonId()).isEqualTo(1);
		assertThat(result.getCreator().getPerson()).isEqualTo("Anton Nordin");
		assertThat(result.getCreator().getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
		assertThat(result.getPublicationId()).isEqualTo(207);
		assertThat(result.getPublicationType()).isEqualTo("Tidningar");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getDocumentTitle()).isEqualTo("Page 3 Alfwar och Skämt nr 8 1841");
	}

	@Test
	void toPublicationIncludesXmltext() {
		final var result = PublicationMapper.toPublication(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getXmltext()).isEqualTo("<text>OCR content</text>");
		assertThat(result.getPublicationType()).isEqualTo("Tidningar");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
	}

	@Test
	void toPublicationWithNullEntityReturnsNull() {
		assertThat(PublicationMapper.toPublicationSummary(null)).isNull();
		assertThat(PublicationMapper.toPublication(null)).isNull();
	}

	@Test
	void toPublicationListMapsAllEntitiesWithoutXmltext() {
		final var entities = List.of(
			PublicationEntity.create().withId(1).withPublicationType("Broschyrer").withDocumentTitle("A").withXmltext("hidden")
				.withTopography(TopographyEntity.create().withId(10).withName("Sundsvall")),
			PublicationEntity.create().withId(2).withPublicationType("Tidningar").withDocumentTitle("B").withXmltext("hidden")
				.withTopography(TopographyEntity.create().withId(20).withName("Timrå")),
			PublicationEntity.create().withId(3).withPublicationType("Broschyrer").withDocumentTitle("C").withXmltext("hidden"));

		final var result = PublicationMapper.toPublicationList(entities);

		assertThat(result)
			.extracting("publicationId", "documentTitle", "publicationType", "location", "xmltext")
			.containsExactly(
				tuple(1, "A", "Broschyrer", "Sundsvall", null),
				tuple(2, "B", "Tidningar", "Timrå", null),
				tuple(3, "C", "Broschyrer", null, null));
	}

	@Test
	void toPublicationWithoutTopographyHasNoLocation() {
		// Both a publication without a place and one whose P_T_ID points at a missing row arrive here as a null
		// association — see PublicationSpecificationTest for the dangling foreign key case.
		final var entity = PublicationEntity.create().withId(1).withLocationText("Sundsvall");

		final var result = PublicationMapper.toPublicationSummary(entity);

		assertThat(result.getLocation()).isNull();
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toPublicationListWithNullReturnsEmpty() {
		assertThat(PublicationMapper.toPublicationList(null)).isEmpty();
	}
}
