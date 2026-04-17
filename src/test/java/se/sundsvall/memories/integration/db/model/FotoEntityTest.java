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

class FotoEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(FotoEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = FotoEntity.create()
			.withFotoId(1234)
			.withFotoTId(42)
			.withFilnamn("original.jpg")
			.withAccNr("ACC-1")
			.withRefKod("REF-1")
			.withInventNr("INV-1")
			.withTidigNr("OLD-1")
			.withDoktitel("Stadsvy")
			.withSakord("Stad")
			.withKommentFf("Kommentar")
			.withTidig("1920")
			.withSenast("1925")
			.withObsdatum("1980-01-01")
			.withFotoOplats("Sundsvall")
			.withForplats("Magasin A")
			.withObjtyp("Foto")
			.withSvvitfarg("Svartvit")
			.withNegPos("Negativ")
			.withGenPas("Genomsikt")
			.withBildbar("Glas")
			.withMaterial("Glas")
			.withTeknik("Negativ")
			.withFunktion("Dokumentation")
			.withHojd("10")
			.withBredd("15")
			.withDiam("0")
			.withRam("Inget")
			.withTillSkat("Bra")
			.withTillstand("Inga skador")
			.withObsnamn("Curator")
			.withAtgard("Inget")
			.withAtdDatum("1980-01-01")
			.withSign("AB")
			.withGivRattigh("Free")
			.withGivForbeh("Nej")
			.withAnvando("Får användas")
			.withKommentUpph("Donation")
			.withFilLiten("FOTO.id_1234_fil_liten.jpg")
			.withFilStor("FOTO.id_1234_fil_stor.jpg")
			.withFilOriginal("FOTO.id_1234_fil_original.jpg")
			.withNodeId(19000)
			.withOptions(4)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getFotoId()).isEqualTo(1234);
		assertThat(result.getFotoTId()).isEqualTo(42);
		assertThat(result.getFilnamn()).isEqualTo("original.jpg");
		assertThat(result.getAccNr()).isEqualTo("ACC-1");
		assertThat(result.getRefKod()).isEqualTo("REF-1");
		assertThat(result.getInventNr()).isEqualTo("INV-1");
		assertThat(result.getTidigNr()).isEqualTo("OLD-1");
		assertThat(result.getDoktitel()).isEqualTo("Stadsvy");
		assertThat(result.getSakord()).isEqualTo("Stad");
		assertThat(result.getKommentFf()).isEqualTo("Kommentar");
		assertThat(result.getTidig()).isEqualTo("1920");
		assertThat(result.getSenast()).isEqualTo("1925");
		assertThat(result.getObsdatum()).isEqualTo("1980-01-01");
		assertThat(result.getFotoOplats()).isEqualTo("Sundsvall");
		assertThat(result.getForplats()).isEqualTo("Magasin A");
		assertThat(result.getObjtyp()).isEqualTo("Foto");
		assertThat(result.getSvvitfarg()).isEqualTo("Svartvit");
		assertThat(result.getNegPos()).isEqualTo("Negativ");
		assertThat(result.getGenPas()).isEqualTo("Genomsikt");
		assertThat(result.getBildbar()).isEqualTo("Glas");
		assertThat(result.getMaterial()).isEqualTo("Glas");
		assertThat(result.getTeknik()).isEqualTo("Negativ");
		assertThat(result.getFunktion()).isEqualTo("Dokumentation");
		assertThat(result.getHojd()).isEqualTo("10");
		assertThat(result.getBredd()).isEqualTo("15");
		assertThat(result.getDiam()).isEqualTo("0");
		assertThat(result.getRam()).isEqualTo("Inget");
		assertThat(result.getTillSkat()).isEqualTo("Bra");
		assertThat(result.getTillstand()).isEqualTo("Inga skador");
		assertThat(result.getObsnamn()).isEqualTo("Curator");
		assertThat(result.getAtgard()).isEqualTo("Inget");
		assertThat(result.getAtdDatum()).isEqualTo("1980-01-01");
		assertThat(result.getSign()).isEqualTo("AB");
		assertThat(result.getGivRattigh()).isEqualTo("Free");
		assertThat(result.getGivForbeh()).isEqualTo("Nej");
		assertThat(result.getAnvando()).isEqualTo("Får användas");
		assertThat(result.getKommentUpph()).isEqualTo("Donation");
		assertThat(result.getFilLiten()).isEqualTo("FOTO.id_1234_fil_liten.jpg");
		assertThat(result.getFilStor()).isEqualTo("FOTO.id_1234_fil_stor.jpg");
		assertThat(result.getFilOriginal()).isEqualTo("FOTO.id_1234_fil_original.jpg");
		assertThat(result.getNodeId()).isEqualTo(19000);
		assertThat(result.getOptions()).isEqualTo(4);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FotoEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new FotoEntity()).hasAllNullFieldsOrProperties();
	}
}
