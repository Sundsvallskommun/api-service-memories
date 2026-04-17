package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.PublEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PublicationMapperTest {

	private static final Function<Integer, String> NULL_LOOKUP = id -> null;

	private static PublEntity sampleEntity() {
		return PublEntity.create()
			.withPublId(207)
			.withFilnamn("alfwar-1841.xml")
			.withPubliktyp("Tidningar")
			.withDatum("1841-02-18")
			.withTidtitel("Alfwar och Skämt")
			.withTidnr("8")
			.withTidsida("3")
			.withForlagOplats("Sundsvall")
			.withDoktitel("Sida 3 Alfwar och Skämt nr 8 1841")
			.withPtId(4)
			.withPubOplats("Sundsvall")
			.withKommentPubl("Archive comment")
			.withFilLiten("PUBL.id_207_fil_liten.jpeg")
			.withFilStor("PUBL.id_207_fil_stor.jpeg")
			.withFilTxt("PUBL.id_207_fil_txt.xml")
			.withXmltext("<text>OCR content</text>")
			.withNodeId(18407)
			.withOptions(4)
			.withFilFormat("text")
			.withDeletedDate(LocalDate.of(2026, 1, 15));
	}

	@Test
	void toPublicationSummaryExcludesXmltext() {
		final var result = PublicationMapper.toPublicationSummary(sampleEntity(), "Sundsvall");

		assertThat(result).isNotNull();
		assertThat(result.getPublId()).isEqualTo(207);
		assertThat(result.getPubliktyp()).isEqualTo("Tidningar");
		assertThat(result.getPlats()).isEqualTo("Sundsvall");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getDoktitel()).isEqualTo("Sida 3 Alfwar och Skämt nr 8 1841");
	}

	@Test
	void toPublicationIncludesXmltext() {
		final var result = PublicationMapper.toPublication(sampleEntity(), "Sundsvall");

		assertThat(result).isNotNull();
		assertThat(result.getXmltext()).isEqualTo("<text>OCR content</text>");
		assertThat(result.getPubliktyp()).isEqualTo("Tidningar");
		assertThat(result.getPlats()).isEqualTo("Sundsvall");
	}

	@Test
	void toPublicationWithNullEntityReturnsNull() {
		assertThat(PublicationMapper.toPublicationSummary(null, "ignored")).isNull();
		assertThat(PublicationMapper.toPublication(null, "ignored")).isNull();
	}

	@Test
	void toPublicationListMapsAllEntitiesWithoutXmltext() {
		final var entities = List.of(
			PublEntity.create().withPublId(1).withPtId(10).withDoktitel("A").withPubliktyp("Broschyrer").withXmltext("hidden"),
			PublEntity.create().withPublId(2).withPtId(20).withDoktitel("B").withPubliktyp("Tidningar").withXmltext("hidden"));
		final Function<Integer, String> lookup = id -> id == 10 ? "Sundsvall" : "Timrå";

		final var result = PublicationMapper.toPublicationList(entities, lookup);

		assertThat(result)
			.extracting("publId", "doktitel", "publiktyp", "plats", "xmltext")
			.containsExactly(
				tuple(1, "A", "Broschyrer", "Sundsvall", null),
				tuple(2, "B", "Tidningar", "Timrå", null));
	}

	@Test
	void toPublicationListWithNullReturnsEmpty() {
		assertThat(PublicationMapper.toPublicationList(null, NULL_LOOKUP)).isEmpty();
	}
}
