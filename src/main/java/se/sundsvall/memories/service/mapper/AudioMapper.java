package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class AudioMapper {

	private AudioMapper() {}

	/**
	 * Map a single AudioEntity to an Audio API model. The place is read from the topography association; the subject is
	 * still resolved by the caller, since {@code LJUD_O_ID} is not modelled as a relation yet.
	 *
	 * @param  entity  the source entity
	 * @param  subject the OCM-resolved subject label (nullable)
	 * @return         the mapped {@link Audio}, or {@code null} if {@code entity} is null
	 */
	public static Audio toAudio(final AudioEntity entity, final String subject) {
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
				.withSubjectId(e.getSubjectId())
				.withSubject(subject)
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
	 * Map a list of AudioEntities, resolving each entity's subject via the provided lookup.
	 *
	 * @param  entities      source entities
	 * @param  subjectLookup resolver from subjectId → OCM subject label (nullable)
	 * @return               list of mapped {@link Audio} objects (empty if entities is null)
	 */
	public static List<Audio> toAudioList(final List<AudioEntity> entities, final ReferenceResolver subjectLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toAudio(e, subjectLookup.resolve(e.getSubjectId())))
			.toList();
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
