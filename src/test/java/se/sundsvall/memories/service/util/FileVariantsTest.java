package se.sundsvall.memories.service.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.service.model.FileVariant;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.memories.service.model.FileVariant.LARGE;
import static se.sundsvall.memories.service.model.FileVariant.ORIGINAL;
import static se.sundsvall.memories.service.model.FileVariant.TEXT;
import static se.sundsvall.memories.service.model.FileVariant.THUMBNAIL;

class FileVariantsTest {

	private static PhotoEntity photo() {
		return PhotoEntity.create()
			.withThumbnailFilename("foto-liten.jpg")
			.withLargeImageFilename("foto-stor.jpg")
			.withOriginalFilename("foto-original.jpg");
	}

	private static TextEntity text() {
		return TextEntity.create()
			.withThumbnailFilename("text-liten.jpg")
			.withLargeImageFilename("text-stor.jpg")
			.withOcrFilename("text-txt.xml")
			.withOriginalFilename("text-original.jpg");
	}

	private static TextMediaEntity media() {
		return TextMediaEntity.create()
			.withThumbnailFilename("media-liten.jpg")
			.withLargeImageFilename("media-stor.jpg")
			.withOriginalFilename("media-original.jpg");
	}

	private static PublicationEntity publication() {
		return PublicationEntity.create()
			.withThumbnailFilename("publ-liten.jpg")
			.withLargeImageFilename("publ-stor.jpg")
			.withOcrFilename("publ-txt.xml")
			.withOriginalFilename("publ-original.jpg");
	}

	@Test
	void photoOffersThumbnailAndLarge() {
		assertThat(FileVariants.filename(photo(), THUMBNAIL)).isEqualTo("foto-liten.jpg");
		assertThat(FileVariants.filename(photo(), LARGE)).isEqualTo("foto-stor.jpg");
	}

	@Test
	void textOffersThumbnailLargeAndOcr() {
		assertThat(FileVariants.filename(text(), THUMBNAIL)).isEqualTo("text-liten.jpg");
		assertThat(FileVariants.filename(text(), LARGE)).isEqualTo("text-stor.jpg");
		assertThat(FileVariants.filename(text(), TEXT)).isEqualTo("text-txt.xml");
	}

	@Test
	void mediaOffersThumbnailLargeAndOriginal() {
		assertThat(FileVariants.filename(media(), THUMBNAIL)).isEqualTo("media-liten.jpg");
		assertThat(FileVariants.filename(media(), LARGE)).isEqualTo("media-stor.jpg");
		assertThat(FileVariants.filename(media(), ORIGINAL)).isEqualTo("media-original.jpg");
	}

	@Test
	void publicationOffersThumbnailLargeAndOcr() {
		assertThat(FileVariants.filename(publication(), THUMBNAIL)).isEqualTo("publ-liten.jpg");
		assertThat(FileVariants.filename(publication(), LARGE)).isEqualTo("publ-stor.jpg");
		assertThat(FileVariants.filename(publication(), TEXT)).isEqualTo("publ-txt.xml");
	}

	// A variant an entity does not offer returns null even when the underlying column holds a value — the sample
	// entities all set FIL_ORIGINAL, and no endpoint exposes it for anything but text media files. The services turn
	// the null into "has no file for variant", which is the 404 an unsupported combination should produce.

	@Test
	void photoOffersNeitherOcrNorOriginal() {
		assertThat(FileVariants.filename(photo(), TEXT)).isNull();
		assertThat(FileVariants.filename(photo(), ORIGINAL)).isNull();
	}

	@Test
	void textDoesNotOfferItsOriginal() {
		assertThat(FileVariants.filename(text(), ORIGINAL)).isNull();
	}

	@Test
	void publicationDoesNotOfferItsOriginal() {
		assertThat(FileVariants.filename(publication(), ORIGINAL)).isNull();
	}

	@Test
	void mediaDoesNotOfferOcr() {
		assertThat(FileVariants.filename(media(), TEXT)).isNull();
	}

	@ParameterizedTest
	@EnumSource(FileVariant.class)
	void everyVariantNamesASubfolder(final FileVariant variant) {
		assertThat(variant.getSubfolder()).isNotBlank();
	}
}
