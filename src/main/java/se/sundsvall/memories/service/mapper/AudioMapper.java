package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.function.Function;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.integration.db.model.AudioEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class AudioMapper {

	private AudioMapper() {}

	/**
	 * Map a single AudioEntity to an Audio API model with resolved {@code location} and {@code subject}.
	 *
	 * @param  entity   the source entity
	 * @param  location the topography-resolved place name (nullable)
	 * @param  subject  the OCM-resolved subject label (nullable)
	 * @return          the mapped {@link Audio}, or {@code null} if {@code entity} is null
	 */
	public static Audio toAudio(final AudioEntity entity, final String location, final String subject) {
		return ofNullable(entity)
			.map(e -> Audio.create()
				.withAudioId(e.getAudioId())
				.withFilename(e.getFilename())
				.withObjectFilePath(e.getObjectFilePath())
				.withObjectType(e.getObjectType())
				.withDate(e.getDate())
				.withDocumentTitle(e.getDocumentTitle())
				.withTopographyId(e.getTopographyId())
				.withLocationText(e.getLocationText())
				.withLocation(location)
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
	 * Map a list of AudioEntities, resolving each entity's location and subject via the provided lookups.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup function from topographyId → resolved location string (nullable)
	 * @param  subjectLookup  function from subjectId → resolved OCM subject label (nullable)
	 * @return                list of mapped {@link Audio} objects (empty if entities is null)
	 */
	@SuppressWarnings("java:S4276") // IntFunction<String> would require unboxing; the source fields are nullable Integers.
	public static List<Audio> toAudioList(final List<AudioEntity> entities, final Function<Integer, String> locationLookup,
		final Function<Integer, String> subjectLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toAudio(e, locationLookup.apply(e.getTopographyId()), subjectLookup.apply(e.getSubjectId())))
				.toList())
			.orElse(emptyList());
	}
}
