package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextMediaFile;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class TextMapper {

	private TextMapper() {}

	/** Summary mapping (no XMLTEXT, no media files) used for list responses. */
	public static Text toTextSummary(final TextEntity entity) {
		return toBase(entity);
	}

	/** Detail mapping including XMLTEXT and extra media files, used for get-by-id. */
	public static Text toText(final TextEntity entity, final List<TextMediaEntity> mediaEntities) {
		return ofNullable(toBase(entity))
			.map(text -> text.withXmltext(entity.getXmltext())
				.withMediaFiles(toMediaFiles(mediaEntities)))
			.orElse(null);
	}

	/**
	 * Map a list of {@link TextEntity} to summary {@link Text}s. Place and subject are both read from their
	 * associations.
	 *
	 * @param  entities source entities
	 * @return          list of mapped texts (empty if entities is null)
	 */
	public static List<Text> toTextList(final List<TextEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(TextMapper::toTextSummary)
			.toList();
	}

	/** Resolves the subject label through the OCM association, {@code null} when there is none. */
	private static String subject(final TextEntity entity) {
		return ofNullable(entity.getSubject())
			.map(OcmEntity::getDisplayName)
			.orElse(null);
	}

	/**
	 * Resolves the raw OCM id, which the API exposes alongside the resolved {@code subject}. Read through the
	 * association rather than from a second mapping of {@code D_O_ID}, so the two can never disagree.
	 */
	private static Integer subjectId(final TextEntity entity) {
		return ofNullable(entity.getSubject())
			.map(OcmEntity::getId)
			.orElse(null);
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

	private static Text toBase(final TextEntity entity) {
		return ofNullable(entity)
			.map(e -> Text.create()
				.withTextId(e.getTextId())
				.withFilename(e.getFilename())
				.withDocumentDate(e.getDocumentDate())
				.withDocumentEndDate(e.getDocumentEndDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withLocationText(e.getLocationText())
				.withLocation(location(e))
				.withSubjectId(subjectId(e))
				.withSubject(subject(e))
				.withComment(e.getComment())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOcrFilename(e.getOcrFilename()))
			.orElse(null);
	}
}
