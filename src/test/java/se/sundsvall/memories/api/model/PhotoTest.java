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

class PhotoTest {

	@Test
	void testBean() {
		assertThat(Photo.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = Photo.create()
			.withPhotoId(1234)
			.withDocumentTitle("Stadsvy")
			.withEarliest("1920")
			.withLatest("1925")
			.withLocationText("Sundsvall")
			.withLocation("Sundsvalls kommun")
			.withTopography(Topography.create().withTopographyId(22).withName("Sundsvalls Gustav Adolf (Sundsvalls stad)").withPlace("Stenstan"))
			.withInstitution(Institution.create().withInstitutionId(23).withName("Medelpads fornminnesförening"))
			.withThumbnailFilename("FOTO.id_1234_fil_liten.jpg")
			.withLargeImageFilename("FOTO.id_1234_fil_stor.jpg")
			.withRights("Free")
			.withRestricted("Nej")
			.withNodeId(19000)
			.withCreator(Creator.create().withPersonId(1).withPerson("Anton Nordin").withLegalEntityId(10).withLegalEntity("Nödhjälpskommittén 1888-1889"));

		assertThat(result.getTopography().getName()).isEqualTo("Sundsvalls Gustav Adolf (Sundsvalls stad)");
		assertThat(result.getTopography().getPlace()).isEqualTo("Stenstan");
		assertThat(result.getInstitution().getName()).isEqualTo("Medelpads fornminnesförening");
		assertThat(result.getPhotoId()).isEqualTo(1234);
		assertThat(result.getDocumentTitle()).isEqualTo("Stadsvy");
		assertThat(result.getEarliest()).isEqualTo("1920");
		assertThat(result.getLatest()).isEqualTo("1925");
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getThumbnailFilename()).isEqualTo("FOTO.id_1234_fil_liten.jpg");
		assertThat(result.getRights()).isEqualTo("Free");
		assertThat(result.getRestricted()).isEqualTo("Nej");
		assertThat(result.getNodeId()).isEqualTo(19000);
		assertThat(result.getCreator().getPerson()).isEqualTo("Anton Nordin");
		assertThat(result.getCreator().getLegalEntity()).isEqualTo("Nödhjälpskommittén 1888-1889");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Photo.create()).hasAllNullFieldsOrProperties();
	}
}
