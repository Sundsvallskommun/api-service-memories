package se.sundsvall.memories.service.util;

import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.service.model.FileVariant;

public final class FileVariants {

	private FileVariants() {}

	public static String filename(final PhotoEntity entity, final FileVariant variant) {
		return switch (variant) {
			case THUMBNAIL -> entity.getThumbnailFilename();
			case LARGE -> entity.getLargeImageFilename();
			default -> null;
		};
	}

	public static String filename(final TextEntity entity, final FileVariant variant) {
		return switch (variant) {
			case THUMBNAIL -> entity.getThumbnailFilename();
			case LARGE -> entity.getLargeImageFilename();
			case TEXT -> entity.getOcrFilename();
			default -> null;
		};
	}

	public static String filename(final TextMediaEntity entity, final FileVariant variant) {
		return switch (variant) {
			case THUMBNAIL -> entity.getThumbnailFilename();
			case LARGE -> entity.getLargeImageFilename();
			case ORIGINAL -> entity.getOriginalFilename();
			default -> null;
		};
	}

	public static String filename(final PublicationEntity entity, final FileVariant variant) {
		return switch (variant) {
			case THUMBNAIL -> entity.getThumbnailFilename();
			case LARGE -> entity.getLargeImageFilename();
			case TEXT -> entity.getOcrFilename();
			default -> null;
		};
	}
}
