package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.PublEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PublicationMapperTest {

	private static final Map<Integer, String> PUBLIKTYP_LOOKUP = Map.of(
		4, "Broschyrer",
		16, "Tidningar");

	private static PublEntity sampleEntity() {
		return PublEntity.create()
			.withPublId(207)
			.withFilnamn("alfwar-1841.xml")
			.withPubliktyp("")
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
			.withFilOriginal("PUBL.id_207_fil_original.jpeg")
			.withFilTxt("PUBL.id_207_fil_txt.xml")
			.withFilXtra("PUBL.id_207_fil_xtra.jpeg")
			.withXmltext("<text>OCR content</text>")
			.withNodeId(18407)
			.withOptions(4)
			.withFilFormat("text")
			.withDeletedDate(LocalDate.of(2026, 1, 15));
	}

	@Test
	void toPublicationSummaryExcludesXmltext() {
		final var result = PublicationMapper.toPublicationSummary(sampleEntity(), PUBLIKTYP_LOOKUP);

		assertThat(result).isNotNull();
		assertThat(result.getPublId()).isEqualTo(207);
		assertThat(result.getPubliktypName()).isEqualTo("Broschyrer");
		assertThat(result.getXmltext()).isNull();
		assertThat(result.getDoktitel()).isEqualTo("Sida 3 Alfwar och Skämt nr 8 1841");
	}

	@Test
	void toPublicationIncludesXmltext() {
		final var result = PublicationMapper.toPublication(sampleEntity(), PUBLIKTYP_LOOKUP);

		assertThat(result).isNotNull();
		assertThat(result.getXmltext()).isEqualTo("<text>OCR content</text>");
		assertThat(result.getPubliktypName()).isEqualTo("Broschyrer");
	}

	@Test
	void toPublicationWithUnknownPubliktypIdLeavesNameNull() {
		final var entity = sampleEntity().withPtId(999);

		final var result = PublicationMapper.toPublicationSummary(entity, PUBLIKTYP_LOOKUP);

		assertThat(result).isNotNull();
		assertThat(result.getPubliktypName()).isNull();
	}

	@Test
	void toPublicationWithNullLookupIsSafe() {
		final var result = PublicationMapper.toPublicationSummary(sampleEntity(), null);

		assertThat(result).isNotNull();
		assertThat(result.getPubliktypName()).isNull();
	}

	@Test
	void toPublicationWithNullEntityReturnsNull() {
		assertThat(PublicationMapper.toPublicationSummary(null, PUBLIKTYP_LOOKUP)).isNull();
		assertThat(PublicationMapper.toPublication(null, PUBLIKTYP_LOOKUP)).isNull();
	}

	@Test
	void toPublicationListMapsAllEntitiesWithoutXmltext() {
		final var entities = List.of(
			PublEntity.create().withPublId(1).withDoktitel("A").withPtId(4).withXmltext("hidden"),
			PublEntity.create().withPublId(2).withDoktitel("B").withPtId(16).withXmltext("hidden"));

		final var result = PublicationMapper.toPublicationList(entities, PUBLIKTYP_LOOKUP);

		assertThat(result)
			.extracting("publId", "doktitel", "publiktypName", "xmltext")
			.containsExactly(
				tuple(1, "A", "Broschyrer", null),
				tuple(2, "B", "Tidningar", null));
	}

	@Test
	void toPublicationListWithNullReturnsEmpty() {
		assertThat(PublicationMapper.toPublicationList(null, PUBLIKTYP_LOOKUP)).isEmpty();
	}
}
