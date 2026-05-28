package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class PhotoMapperTest {

	private static final ReferenceResolver NULL_LOOKUP = id -> null;

	private static PhotoEntity sampleEntity() {
		return PhotoEntity.create()
			.withPhotoId(1234)
			.withTopographyId(42)
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
		final var result = PhotoMapper.toPhotoSummary(sampleEntity(), "Sundsvall");

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
	void toPhotoDetailAttachesRelatedPhotosAndSubjects() {
		final var subjects = List.of(Subject.create().withCode("ALM").withText("Allmänt").withDescription("Allmänt ämne"));
		final var related = List.of(2001, 2002);

		final var result = PhotoMapper.toPhoto(sampleEntity(), "Sundsvall", related, subjects);

		assertThat(result).isNotNull();
		assertThat(result.getRelatedPhotoIds()).containsExactly(2001, 2002);
		assertThat(result.getSubjects()).hasSize(1);
		assertThat(result.getSubjects().getFirst().getCode()).isEqualTo("ALM");
	}

	@Test
	void toPhotoDetailNullListsBecomeEmpty() {
		final var result = PhotoMapper.toPhoto(sampleEntity(), null, null, null);

		assertThat(result).isNotNull();
		assertThat(result.getLocation()).isNull();
		assertThat(result.getRelatedPhotoIds()).isEqualTo(emptyList());
		assertThat(result.getSubjects()).isEqualTo(emptyList());
	}

	@Test
	void toPhotoDetailWithNullEntityReturnsNull() {
		assertThat(PhotoMapper.toPhoto(null, "ignored", List.of(), List.of())).isNull();
	}

	@Test
	void toPhotoListMapsAllEntitiesWithResolvedLocation() {
		final var entities = List.of(
			PhotoEntity.create().withPhotoId(1).withTopographyId(10).withDocumentTitle("A"),
			PhotoEntity.create().withPhotoId(2).withTopographyId(20).withDocumentTitle("B"));
		final ReferenceResolver lookup = id -> id == 10 ? "Sundsvall" : "Timrå";

		final var result = PhotoMapper.toPhotoList(entities, lookup);

		assertThat(result)
			.extracting("photoId", "documentTitle", "location")
			.containsExactly(tuple(1, "A", "Sundsvall"), tuple(2, "B", "Timrå"));
	}

	@Test
	void toPhotoListWithNullReturnsEmpty() {
		assertThat(PhotoMapper.toPhotoList(null, NULL_LOOKUP)).isEmpty();
	}
}
