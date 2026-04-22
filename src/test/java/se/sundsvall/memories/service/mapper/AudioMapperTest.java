package se.sundsvall.memories.service.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.integration.db.model.AudioEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class AudioMapperTest {

	private static final Function<Integer, String> NULL_LOOKUP = id -> null;

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
					.withTopographyId(2)
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
		final var result = AudioMapper.toAudio(input, "Sundsvalls kommun", "Intervju");

		if (expected == null) {
			assertThat(result).isNull();
		} else {
			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}
	}

	@Test
	void toAudioList() {
		final var entities = List.of(
			AudioEntity.create().withAudioId(1).withTopographyId(10).withSubjectId(100).withDocumentTitle("Audio A"),
			AudioEntity.create().withAudioId(2).withTopographyId(20).withSubjectId(200).withDocumentTitle("Audio B"));
		final Function<Integer, String> locationLookup = id -> id == 10 ? "Sundsvall" : "Timrå";
		final Function<Integer, String> subjectLookup = id -> id == 100 ? "Intervju" : "Musik";

		final var result = AudioMapper.toAudioList(entities, locationLookup, subjectLookup);

		assertThat(result)
			.extracting(Audio::getAudioId, Audio::getDocumentTitle, Audio::getLocation, Audio::getSubject)
			.containsExactly(
				tuple(1, "Audio A", "Sundsvall", "Intervju"),
				tuple(2, "Audio B", "Timrå", "Musik"));
	}

	@Test
	void toAudioListWithNull() {
		assertThat(AudioMapper.toAudioList(null, NULL_LOOKUP, NULL_LOOKUP)).isEmpty();
	}
}
