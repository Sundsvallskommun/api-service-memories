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
	public static Text toTextSummary(final TextEntity entity, final String location, final String subject) {
		return toBase(entity, location, subject);
	}

	/** Detail mapping including XMLTEXT and extra media files, used for get-by-id. */
	public static Text toText(final TextEntity entity, final String location, final String subject, final List<TextMediaEntity> mediaEntities) {
		return ofNullable(toBase(entity, location, subject))
			.map(text -> text.withXmltext(entity.getXmltext())
				.withMediaFiles(toMediaFiles(mediaEntities)))
			.orElse(null);
	}

	/**
	 * Map a list of {@link TextEntity} to summary {@link Text}s, resolving each entity's location and subject via the
	 * provided lookups.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup resolver from topographyId → location string (nullable)
	 * @param  subjectLookup  resolver from subjectId → OCM subject label (nullable)
	 * @return                list of mapped texts (empty if entities is null)
	 */
	public static List<Text> toTextList(final List<TextEntity> entities, final ReferenceResolver locationLookup, final ReferenceResolver subjectLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toTextSummary(e, locationLookup.resolve(e.getTopographyId()), subjectLookup.resolve(e.getSubjectId())))
			.toList();
	}

	public static List<TextMediaFile> toMediaFiles(final List<TextMediaEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> TextMediaFile.create()
				.withId(e.getId())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOriginalFilename(e.getOriginalFilename()))
			.toList();
	}

	private static Text toBase(final TextEntity entity, final String location, final String subject) {
		return ofNullable(entity)
			.map(e -> Text.create()
				.withTextId(e.getTextId())
				.withFilename(e.getFilename())
				.withDocumentDate(e.getDocumentDate())
				.withDocumentEndDate(e.getDocumentEndDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withLocationText(e.getLocationText())
				.withLocation(location)
				.withSubjectId(e.getSubjectId())
				.withSubject(subject)
				.withComment(e.getComment())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOcrFilename(e.getOcrFilename()))
			.orElse(null);
	}
}
