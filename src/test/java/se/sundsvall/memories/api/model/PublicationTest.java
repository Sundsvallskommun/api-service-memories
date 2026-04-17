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
		final var publId = 207;
		final var filnamn = "alfwar-1841.xml";
		final var publiktyp = "";
		final var datum = "1841-02-18";
		final var tidtitel = "Alfwar och Skämt";
		final var tidnr = "8";
		final var tidsida = "3";
		final var forlagOplats = "Sundsvall";
		final var doktitel = "Sida 3 Alfwar och Skämt nr 8 1841";
		final var pubOplats = "Sundsvall";
		final var plats = "Sundsvalls kommun";
		final var kommentPubl = "Archive comment";
		final var filLiten = "PUBL.id_207_fil_liten.jpeg";
		final var filStor = "PUBL.id_207_fil_stor.jpeg";
		final var filTxt = "PUBL.id_207_fil_txt.xml";
		final var xmltext = "<text>OCR content</text>";

		final var result = Publication.create()
			.withPublId(publId)
			.withFilnamn(filnamn)
			.withPubliktyp(publiktyp)
			.withDatum(datum)
			.withTidtitel(tidtitel)
			.withTidnr(tidnr)
			.withTidsida(tidsida)
			.withForlagOplats(forlagOplats)
			.withDoktitel(doktitel)
			.withPubOplats(pubOplats)
			.withPlats(plats)
			.withKommentPubl(kommentPubl)
			.withFilLiten(filLiten)
			.withFilStor(filStor)
			.withFilTxt(filTxt)
			.withXmltext(xmltext);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getPublId()).isEqualTo(publId);
		assertThat(result.getFilnamn()).isEqualTo(filnamn);
		assertThat(result.getPubliktyp()).isEqualTo(publiktyp);
		assertThat(result.getDatum()).isEqualTo(datum);
		assertThat(result.getTidtitel()).isEqualTo(tidtitel);
		assertThat(result.getTidnr()).isEqualTo(tidnr);
		assertThat(result.getTidsida()).isEqualTo(tidsida);
		assertThat(result.getForlagOplats()).isEqualTo(forlagOplats);
		assertThat(result.getDoktitel()).isEqualTo(doktitel);
		assertThat(result.getPubOplats()).isEqualTo(pubOplats);
		assertThat(result.getPlats()).isEqualTo(plats);
		assertThat(result.getKommentPubl()).isEqualTo(kommentPubl);
		assertThat(result.getFilLiten()).isEqualTo(filLiten);
		assertThat(result.getFilStor()).isEqualTo(filStor);
		assertThat(result.getFilTxt()).isEqualTo(filTxt);
		assertThat(result.getXmltext()).isEqualTo(xmltext);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Publication.create()).hasAllNullFieldsOrProperties();
	}
}
