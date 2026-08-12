package se.sundsvall.memories.service.mapper;

import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PhotoMapperTest {

	private static PhotoEntity sampleEntity() {
		return PhotoEntity.create()
			.withPhotoId(1234)
			.withTopography(TopographyEntity.create().withTId(42).withName("Sundsvall"))
			.withDocumentTitle("Stadsvy från Norra berget")
			.withEarliest("1920")
			.withLatest("1925")
			.withLocationText("Sundsvall")
			.withThumbnailFilename("FOTO.id_1234_fil_liten.jpg")
			.withLargeImageFilename("FOTO.id_1234_fil_stor.jpg")
			.withRights("Free use")
			.withRestricted("Nej")
			.withOptions(4);
	}

	@Test
	void toPhotoSummary() {
		final var result = PhotoMapper.toPhotoSummary(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getPhotoId()).isEqualTo(1234);
		assertThat(result.getDocumentTitle()).isEqualTo("Stadsvy från Norra berget");
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getLargeImageFilename()).isEqualTo("FOTO.id_1234_fil_stor.jpg");
		assertThat(result.getRights()).isEqualTo("Free use");
		assertThat(result.getRelatedPhotoIds()).isNull();
		assertThat(result.getSubjects()).isNull();
	}

	@Test
	void toPhotoSummaryFallsBackThroughTheTopographyDisplayName() {
		final var entity = sampleEntity()
			.withTopography(TopographyEntity.create().withTId(42).withName("").withPlace("Indal"));

		assertThat(PhotoMapper.toPhotoSummary(entity).getLocation()).isEqualTo("Indal");
	}

	@Test
	void toPhotoSummaryWithoutTopographyHasNoLocation() {
		// Both a photo without a place and a photo whose F_T_ID points at a missing row arrive here as a null
		// association — see PhotoSpecificationTest for the dangling foreign key case.
		final var entity = sampleEntity().withTopography(null);

		assertThat(PhotoMapper.toPhotoSummary(entity).getLocation()).isNull();
		assertThat(PhotoMapper.toPhotoSummary(entity).getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toPhotoDetailAttachesRelatedPhotosAndSubjects() {
		final var entity = sampleEntity().withSubjects(new LinkedHashSet<>(List.of(
			OcmEntity.create().withId(1).withCode("ALM").withText("Allmänt").withDescription("Allmänt ämne"),
			OcmEntity.create().withId(20).withCode("MUS").withText("Musik").withDescription("Musikinspelning"))));

		final var result = PhotoMapper.toPhoto(entity, List.of(2001, 2002));

		assertThat(result).isNotNull();
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getRelatedPhotoIds()).containsExactly(2001, 2002);
		assertThat(result.getSubjects())
			.extracting(Subject::getCode, Subject::getText, Subject::getDescription)
			.containsExactly(
				tuple("ALM", "Allmänt", "Allmänt ämne"),
				tuple("MUS", "Musik", "Musikinspelning"));
	}

	@Test
	void toPhotoDetailWithoutRelationsBecomesEmptyLists() {
		final var result = PhotoMapper.toPhoto(sampleEntity(), null);

		assertThat(result).isNotNull();
		assertThat(result.getRelatedPhotoIds()).isEqualTo(emptyList());
		assertThat(result.getSubjects()).isEqualTo(emptyList());
	}

	@Test
	void toPhotoDetailWithNullEntityReturnsNull() {
		assertThat(PhotoMapper.toPhoto(null, List.of())).isNull();
	}

	@Test
	void toPhotoListMapsAllEntities() {
		final var entities = List.of(
			PhotoEntity.create().withPhotoId(1).withDocumentTitle("A")
				.withTopography(TopographyEntity.create().withTId(10).withName("Sundsvall")),
			PhotoEntity.create().withPhotoId(2).withDocumentTitle("B")
				.withTopography(TopographyEntity.create().withTId(20).withName("Timrå")),
			PhotoEntity.create().withPhotoId(3).withDocumentTitle("C"));

		final var result = PhotoMapper.toPhotoList(entities);

		assertThat(result)
			.extracting("photoId", "documentTitle", "location")
			.containsExactly(tuple(1, "A", "Sundsvall"), tuple(2, "B", "Timrå"), tuple(3, "C", null));
	}

	@Test
	void toPhotoListWithNullReturnsEmpty() {
		assertThat(PhotoMapper.toPhotoList(null)).isEmpty();
	}
}
