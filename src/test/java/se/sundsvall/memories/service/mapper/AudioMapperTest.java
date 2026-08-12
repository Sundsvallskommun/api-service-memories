package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class AudioMapperTest {

	private static Stream<Arguments> toAudioArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(
				AudioEntity.create()
					.withAudioId(1)
					.withFilename("test.mp3")
					.withObjectFilePath("/path/test.mp3")
					.withObjectType("LJUD")
					.withDate("2020-01-01")
					.withDocumentTitle("Test audio")
					.withTopography(TopographyEntity.create().withTId(2).withName("Sundsvalls kommun"))
					.withLocationText("Sundsvall")
					.withSubject(OcmEntity.create().withId(7).withText("Intervju"))
					.withAuthorPersonId(4)
					.withAuthorEntityId(5)
					.withComment("A comment")
					.withAudioMimeType("audio/mpeg")
					.withNodeId(6)
					.withOptions(4)
					.withDeletedDate(LocalDate.of(2026, 1, 15)),
				Audio.create()
					.withAudioId(1)
					.withFilename("test.mp3")
					.withObjectFilePath("/path/test.mp3")
					.withObjectType("LJUD")
					.withDate("2020-01-01")
					.withDocumentTitle("Test audio")
					.withTopographyId(2)
					.withLocationText("Sundsvall")
					.withLocation("Sundsvalls kommun")
					.withSubjectId(7)
					.withSubject("Intervju")
					.withAuthorPersonId(4)
					.withAuthorEntityId(5)
					.withComment("A comment")
					.withAudioMimeType("audio/mpeg")
					.withNodeId(6)
					.withOptions(4)
					.withDeletedDate(LocalDate.of(2026, 1, 15))));
	}

	@ParameterizedTest
	@MethodSource("toAudioArguments")
	void toAudio(final AudioEntity input, final Audio expected) {
		final var result = AudioMapper.toAudio(input);

		if (expected == null) {
			assertThat(result).isNull();
		} else {
			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}
	}

	@Test
	void toAudioFallsBackThroughTheTopographyDisplayName() {
		final var entity = AudioEntity.create().withAudioId(1)
			.withTopography(TopographyEntity.create().withTId(2).withName("").withPlace("Indal"));

		assertThat(AudioMapper.toAudio(entity).getLocation()).isEqualTo("Indal");
	}

	@Test
	void toAudioWithoutTopographyHasNeitherLocationNorTopographyId() {
		// Both an audio without a place and one whose LJUD_T_ID points at a missing row arrive here as a null
		// association — see AudioSpecificationTest for the dangling foreign key case. Since topographyId is read
		// through the association too, the two can never disagree.
		final var entity = AudioEntity.create().withAudioId(1).withLocationText("Sundsvall");

		final var result = AudioMapper.toAudio(entity);

		assertThat(result.getLocation()).isNull();
		assertThat(result.getTopographyId()).isNull();
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toAudioList() {
		final var entities = List.of(
			AudioEntity.create().withAudioId(1).withDocumentTitle("Audio A")
				.withTopography(TopographyEntity.create().withTId(10).withName("Sundsvall"))
				.withSubject(OcmEntity.create().withId(100).withText("Intervju")),
			AudioEntity.create().withAudioId(2).withDocumentTitle("Audio B")
				.withTopography(TopographyEntity.create().withTId(20).withName("Timrå"))
				.withSubject(OcmEntity.create().withId(200).withText("Musik")),
			AudioEntity.create().withAudioId(3).withDocumentTitle("Audio C"));

		final var result = AudioMapper.toAudioList(entities);

		assertThat(result)
			.extracting(Audio::getAudioId, Audio::getDocumentTitle, Audio::getTopographyId, Audio::getLocation, Audio::getSubjectId, Audio::getSubject)
			.containsExactly(
				tuple(1, "Audio A", 10, "Sundsvall", 100, "Intervju"),
				tuple(2, "Audio B", 20, "Timrå", 200, "Musik"),
				tuple(3, "Audio C", null, null, null, null));
	}

	@Test
	void toAudioListWithNull() {
		assertThat(AudioMapper.toAudioList(null)).isEmpty();
	}
}
