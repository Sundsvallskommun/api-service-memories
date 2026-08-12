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
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class AudioMapperTest {

	private static final ReferenceResolver NULL_LOOKUP = id -> null;

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
					.withSubjectId(7)
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
		final var result = AudioMapper.toAudio(input, "Intervju");

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

		assertThat(AudioMapper.toAudio(entity, null).getLocation()).isEqualTo("Indal");
	}

	@Test
	void toAudioWithoutTopographyHasNeitherLocationNorTopographyId() {
		// Both an audio without a place and one whose LJUD_T_ID points at a missing row arrive here as a null
		// association — see AudioSpecificationsTest for the dangling foreign key case. Since topographyId is read
		// through the association too, the two can never disagree.
		final var entity = AudioEntity.create().withAudioId(1).withLocationText("Sundsvall");

		final var result = AudioMapper.toAudio(entity, null);

		assertThat(result.getLocation()).isNull();
		assertThat(result.getTopographyId()).isNull();
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
	}

	@Test
	void toAudioList() {
		final var entities = List.of(
			AudioEntity.create().withAudioId(1).withSubjectId(100).withDocumentTitle("Audio A")
				.withTopography(TopographyEntity.create().withTId(10).withName("Sundsvall")),
			AudioEntity.create().withAudioId(2).withSubjectId(200).withDocumentTitle("Audio B")
				.withTopography(TopographyEntity.create().withTId(20).withName("Timrå")),
			AudioEntity.create().withAudioId(3).withSubjectId(300).withDocumentTitle("Audio C"));
		final ReferenceResolver subjectLookup = id -> switch (id) {
			case 100 -> "Intervju";
			case 200 -> "Musik";
			default -> null;
		};

		final var result = AudioMapper.toAudioList(entities, subjectLookup);

		assertThat(result)
			.extracting(Audio::getAudioId, Audio::getDocumentTitle, Audio::getTopographyId, Audio::getLocation, Audio::getSubject)
			.containsExactly(
				tuple(1, "Audio A", 10, "Sundsvall", "Intervju"),
				tuple(2, "Audio B", 20, "Timrå", "Musik"),
				tuple(3, "Audio C", null, null, null));
	}

	@Test
	void toAudioListWithNull() {
		assertThat(AudioMapper.toAudioList(null, NULL_LOOKUP)).isEmpty();
	}
}
