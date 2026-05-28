package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextMediaFile;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class TextMapper {

	private TextMapper() {}

	/** Summary mapping (no XMLTEXT, no media files) used for list responses. */
	public static Text toTextSummary(final TextEntity entity, final String location) {
		return toBase(entity, location);
	}

	/** Detail mapping including XMLTEXT and extra media files, used for get-by-id. */
	public static Text toText(final TextEntity entity, final String location, final List<TextMediaEntity> mediaEntities) {
		return ofNullable(toBase(entity, location))
			.map(text -> text.withXmltext(entity.getXmltext())
				.withMediaFiles(toMediaFiles(mediaEntities)))
			.orElse(null);
	}

	/**
	 * Map a list of {@link TextEntity} to summary {@link Text}s, resolving each entity's location via the lookup.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup resolver from topographyId → location string (nullable)
	 * @return                list of mapped texts (empty if entities is null)
	 */
	public static List<Text> toTextList(final List<TextEntity> entities, final ReferenceResolver locationLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toTextSummary(e, locationLookup.resolve(e.getTopographyId())))
				.toList())
			.orElse(emptyList());
	}

	public static List<TextMediaFile> toMediaFiles(final List<TextMediaEntity> entities) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> TextMediaFile.create()
					.withThumbnailFilename(e.getThumbnailFilename())
					.withLargeImageFilename(e.getLargeImageFilename())
					.withOriginalFilename(e.getOriginalFilename()))
				.toList())
			.orElse(emptyList());
	}

	private static Text toBase(final TextEntity entity, final String location) {
		return ofNullable(entity)
			.map(e -> Text.create()
				.withTextId(e.getTextId())
				.withFilename(e.getFilename())
				.withDocumentDate(e.getDocumentDate())
				.withDocumentEndDate(e.getDocumentEndDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withLocationText(e.getLocationText())
				.withLocation(location)
				.withComment(e.getComment())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOcrFilename(e.getOcrFilename()))
			.orElse(null);
	}
}
