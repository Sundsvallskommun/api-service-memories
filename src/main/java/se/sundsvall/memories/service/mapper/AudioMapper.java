package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class AudioMapper {

	private AudioMapper() {}

	/**
	 * Map a single AudioEntity to an Audio API model. Both the place and the subject are read from their associations.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Audio}, or {@code null} if {@code entity} is null
	 */
	public static Audio toAudio(final AudioEntity entity) {
		return ofNullable(entity)
			.map(e -> Audio.create()
				.withAudioId(e.getAudioId())
				.withFilename(e.getFilename())
				.withObjectFilePath(e.getObjectFilePath())
				.withObjectType(e.getObjectType())
				.withDate(e.getDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withTopographyId(topographyId(e))
				.withLocationText(e.getLocationText())
				.withLocation(location(e))
				.withSubjectId(subjectId(e))
				.withSubject(subject(e))
				.withAuthorPersonId(e.getAuthorPersonId())
				.withAuthorEntityId(e.getAuthorEntityId())
				.withComment(e.getComment())
				.withAudioMimeType(e.getAudioMimeType())
				.withNodeId(e.getNodeId())
				.withOptions(e.getOptions())
				.withDeletedDate(e.getDeletedDate()))
			.orElse(null);
	}

	/**
	 * Map a list of AudioEntities.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Audio} objects (empty if entities is null)
	 */
	public static List<Audio> toAudioList(final List<AudioEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(AudioMapper::toAudio)
			.toList();
	}

	/** Resolves the subject label through the OCM association, {@code null} when there is none. */
	private static String subject(final AudioEntity entity) {
		return ofNullable(entity.getSubject())
			.map(OcmEntity::getDisplayName)
			.orElse(null);
	}

	/**
	 * Resolves the raw OCM id, which the API exposes alongside the resolved {@code subject}. Read through the
	 * association rather than from a second mapping of {@code LJUD_O_ID}, so the two can never disagree.
	 */
	private static Integer subjectId(final AudioEntity entity) {
		return ofNullable(entity.getSubject())
			.map(OcmEntity::getId)
			.orElse(null);
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the audio
	 * has no place and when {@code LJUD_T_ID} points at a row that does not exist.
	 */
	private static String location(final AudioEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	/**
	 * Resolves the raw topography id, which the API exposes alongside the resolved {@code location}. Read through the
	 * association rather than from a second mapping of {@code LJUD_T_ID}, so the two can never disagree.
	 */
	private static Integer topographyId(final AudioEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getTId)
			.orElse(null);
	}
}
