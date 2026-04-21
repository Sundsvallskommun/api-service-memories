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

class PhotoEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(PhotoEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = PhotoEntity.create()
			.withPhotoId(1234)
			.withTopographyId(42)
			.withFilename("original.jpg")
			.withAccessionNumber("ACC-1")
			.withReferenceCode("REF-1")
			.withInventoryNumber("INV-1")
			.withEarlierReference("OLD-1")
			.withDocumentTitle("Stadsvy")
			.withSubjectKeyword("Stad")
			.withComment("Kommentar")
			.withEarliest("1920")
			.withLatest("1925")
			.withObservationDate("1980-01-01")
			.withLocationText("Sundsvall")
			.withStorageLocation("Magasin A")
			.withObjectType("Foto")
			.withColorMode("Svartvit")
			.withNegativePositive("Negativ")
			.withTransmissiveReflective("Genomsikt")
			.withImageCarrier("Glas")
			.withMaterial("Glas")
			.withTechnique("Negativ")
			.withFunction("Dokumentation")
			.withHeight("10")
			.withWidth("15")
			.withDiameter("0")
			.withFramed("Inget")
			.withConditionCategory("Bra")
			.withConditionAssessment("Inga skador")
			.withObserverName("Curator")
			.withTreatment("Inget")
			.withTreatmentDate("1980-01-01")
			.withSignature("AB")
			.withRights("Free")
			.withRestricted("Nej")
			.withRestrictionNote("Får användas")
			.withProvenance("Donation")
			.withThumbnailFilename("FOTO.id_1234_fil_liten.jpg")
			.withLargeImageFilename("FOTO.id_1234_fil_stor.jpg")
			.withOriginalFilename("FOTO.id_1234_fil_original.jpg")
			.withNodeId(19000)
			.withOptions(4)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getPhotoId()).isEqualTo(1234);
		assertThat(result.getTopographyId()).isEqualTo(42);
		assertThat(result.getFilename()).isEqualTo("original.jpg");
		assertThat(result.getAccessionNumber()).isEqualTo("ACC-1");
		assertThat(result.getReferenceCode()).isEqualTo("REF-1");
		assertThat(result.getInventoryNumber()).isEqualTo("INV-1");
		assertThat(result.getEarlierReference()).isEqualTo("OLD-1");
		assertThat(result.getDocumentTitle()).isEqualTo("Stadsvy");
		assertThat(result.getSubjectKeyword()).isEqualTo("Stad");
		assertThat(result.getComment()).isEqualTo("Kommentar");
		assertThat(result.getEarliest()).isEqualTo("1920");
		assertThat(result.getLatest()).isEqualTo("1925");
		assertThat(result.getObservationDate()).isEqualTo("1980-01-01");
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getStorageLocation()).isEqualTo("Magasin A");
		assertThat(result.getObjectType()).isEqualTo("Foto");
		assertThat(result.getColorMode()).isEqualTo("Svartvit");
		assertThat(result.getNegativePositive()).isEqualTo("Negativ");
		assertThat(result.getTransmissiveReflective()).isEqualTo("Genomsikt");
		assertThat(result.getImageCarrier()).isEqualTo("Glas");
		assertThat(result.getMaterial()).isEqualTo("Glas");
		assertThat(result.getTechnique()).isEqualTo("Negativ");
		assertThat(result.getFunction()).isEqualTo("Dokumentation");
		assertThat(result.getHeight()).isEqualTo("10");
		assertThat(result.getWidth()).isEqualTo("15");
		assertThat(result.getDiameter()).isEqualTo("0");
		assertThat(result.getFramed()).isEqualTo("Inget");
		assertThat(result.getConditionCategory()).isEqualTo("Bra");
		assertThat(result.getConditionAssessment()).isEqualTo("Inga skador");
		assertThat(result.getObserverName()).isEqualTo("Curator");
		assertThat(result.getTreatment()).isEqualTo("Inget");
		assertThat(result.getTreatmentDate()).isEqualTo("1980-01-01");
		assertThat(result.getSignature()).isEqualTo("AB");
		assertThat(result.getRights()).isEqualTo("Free");
		assertThat(result.getRestricted()).isEqualTo("Nej");
		assertThat(result.getRestrictionNote()).isEqualTo("Får användas");
		assertThat(result.getProvenance()).isEqualTo("Donation");
		assertThat(result.getThumbnailFilename()).isEqualTo("FOTO.id_1234_fil_liten.jpg");
		assertThat(result.getLargeImageFilename()).isEqualTo("FOTO.id_1234_fil_stor.jpg");
		assertThat(result.getOriginalFilename()).isEqualTo("FOTO.id_1234_fil_original.jpg");
		assertThat(result.getNodeId()).isEqualTo(19000);
		assertThat(result.getOptions()).isEqualTo(4);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PhotoEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new PhotoEntity()).hasAllNullFieldsOrProperties();
	}
}
