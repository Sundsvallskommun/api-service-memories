package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextMediaFile;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class TextMapper {

	private TextMapper() {}

	/** Summary mapping (no XMLTEXT, no media files) used for list responses. */
	public static Text toTextSummary(final TextEntity entity, final String subject) {
		return toBase(entity, subject);
	}

	/** Detail mapping including XMLTEXT and extra media files, used for get-by-id. */
	public static Text toText(final TextEntity entity, final String subject, final List<TextMediaEntity> mediaEntities) {
		return ofNullable(toBase(entity, subject))
			.map(text -> text.withXmltext(entity.getXmltext())
				.withMediaFiles(toMediaFiles(mediaEntities)))
			.orElse(null);
	}

	/**
	 * Map a list of {@link TextEntity} to summary {@link Text}s, resolving each entity's subject via the provided
	 * lookup. The place is read from the topography association.
	 *
	 * @param  entities      source entities
	 * @param  subjectLookup resolver from subjectId → OCM subject label (nullable)
	 * @return               list of mapped texts (empty if entities is null)
	 */
	public static List<Text> toTextList(final List<TextEntity> entities, final ReferenceResolver subjectLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toTextSummary(e, subjectLookup.resolve(e.getSubjectId())))
			.toList();
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the text has
	 * no place and when {@code D_T_ID} points at a row that does not exist.
	 */
	private static String location(final TextEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
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

	private static Text toBase(final TextEntity entity, final String subject) {
		return ofNullable(entity)
			.map(e -> Text.create()
				.withTextId(e.getTextId())
				.withFilename(e.getFilename())
				.withDocumentDate(e.getDocumentDate())
				.withDocumentEndDate(e.getDocumentEndDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withLocationText(e.getLocationText())
				.withLocation(location(e))
				.withSubjectId(e.getSubjectId())
				.withSubject(subject)
				.withComment(e.getComment())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOcrFilename(e.getOcrFilename()))
			.orElse(null);
	}
}
