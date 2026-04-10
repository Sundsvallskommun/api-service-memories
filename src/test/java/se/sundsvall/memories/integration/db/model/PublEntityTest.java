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

class PublEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(PublEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var publId = 207;
		final var filnamn = "F21051/1841-02-18_Sida 3 Alfwar och Skämt nr 8 1841.xml";
		final var publiktyp = "";
		final var datum = "1841-02-18";
		final var tidtitel = "Alfwar och Skämt";
		final var tidnr = "8";
		final var tidsida = "3";
		final var bfJId = 1;
		final var forlagTId = 1;
		final var forlagOplats = "Sundsvall";
		final var dokdatum = "1841-02-18";
		final var doktitel = "Sida 3 Alfwar och Skämt nr 8 1841";
		final var feId = 0;
		final var reId = 0;
		final var ujId = 1;
		final var ueId = 0;
		final var ptId = 4;
		final var pubOplats = "Sundsvall";
		final var meOId = 1;
		final var kommentPubl = "Archive comment";
		final var filLiten = "PUBL.id_207_fil_liten.jpeg";
		final var filStor = "PUBL.id_207_fil_stor.jpeg";
		final var filOriginal = "PUBL.id_207_fil_original.jpeg";
		final var filTxt = "PUBL.id_207_fil_txt.xml";
		final var xmltext = "<text>OCR content</text>";
		final var filXtra = "PUBL.id_207_fil_xtra.jpeg";
		final var nodeId = 18407;
		final var options = 4;
		final var filFormat = "text";
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = PublEntity.create()
			.withPublId(publId)
			.withFilnamn(filnamn)
			.withPubliktyp(publiktyp)
			.withDatum(datum)
			.withTidtitel(tidtitel)
			.withTidnr(tidnr)
			.withTidsida(tidsida)
			.withBfJId(bfJId)
			.withForlagTId(forlagTId)
			.withForlagOplats(forlagOplats)
			.withDokdatum(dokdatum)
			.withDoktitel(doktitel)
			.withFeId(feId)
			.withReId(reId)
			.withUjId(ujId)
			.withUeId(ueId)
			.withPtId(ptId)
			.withPubOplats(pubOplats)
			.withMeOId(meOId)
			.withKommentPubl(kommentPubl)
			.withFilLiten(filLiten)
			.withFilStor(filStor)
			.withFilOriginal(filOriginal)
			.withFilTxt(filTxt)
			.withXmltext(xmltext)
			.withFilXtra(filXtra)
			.withNodeId(nodeId)
			.withOptions(options)
			.withFilFormat(filFormat)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getPublId()).isEqualTo(publId);
		assertThat(result.getFilnamn()).isEqualTo(filnamn);
		assertThat(result.getPubliktyp()).isEqualTo(publiktyp);
		assertThat(result.getDatum()).isEqualTo(datum);
		assertThat(result.getTidtitel()).isEqualTo(tidtitel);
		assertThat(result.getTidnr()).isEqualTo(tidnr);
		assertThat(result.getTidsida()).isEqualTo(tidsida);
		assertThat(result.getBfJId()).isEqualTo(bfJId);
		assertThat(result.getForlagTId()).isEqualTo(forlagTId);
		assertThat(result.getForlagOplats()).isEqualTo(forlagOplats);
		assertThat(result.getDokdatum()).isEqualTo(dokdatum);
		assertThat(result.getDoktitel()).isEqualTo(doktitel);
		assertThat(result.getFeId()).isEqualTo(feId);
		assertThat(result.getReId()).isEqualTo(reId);
		assertThat(result.getUjId()).isEqualTo(ujId);
		assertThat(result.getUeId()).isEqualTo(ueId);
		assertThat(result.getPtId()).isEqualTo(ptId);
		assertThat(result.getPubOplats()).isEqualTo(pubOplats);
		assertThat(result.getMeOId()).isEqualTo(meOId);
		assertThat(result.getKommentPubl()).isEqualTo(kommentPubl);
		assertThat(result.getFilLiten()).isEqualTo(filLiten);
		assertThat(result.getFilStor()).isEqualTo(filStor);
		assertThat(result.getFilOriginal()).isEqualTo(filOriginal);
		assertThat(result.getFilTxt()).isEqualTo(filTxt);
		assertThat(result.getXmltext()).isEqualTo(xmltext);
		assertThat(result.getFilXtra()).isEqualTo(filXtra);
		assertThat(result.getNodeId()).isEqualTo(nodeId);
		assertThat(result.getOptions()).isEqualTo(options);
		assertThat(result.getFilFormat()).isEqualTo(filFormat);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PublEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new PublEntity()).hasAllNullFieldsOrProperties();
	}
}
