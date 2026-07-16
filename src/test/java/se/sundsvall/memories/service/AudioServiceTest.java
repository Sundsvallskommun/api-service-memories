package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.service.model.StreamPayload;
import se.sundsvall.memories.service.util.FileStreamer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.integration.samba.SambaTestProperties.SAMBA_PROPERTIES;

@ExtendWith(MockitoExtension.class)
class AudioServiceTest {

	@Mock
	private AudioRepository repositoryMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private OcmLookup ocmLookupMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private AudioService service;

	@BeforeEach
	void setUp() {
		service = new AudioService(repositoryMock, SAMBA_PROPERTIES, topographyLookupMock, ocmLookupMock, fileStreamerMock);
	}

	@Test
	void searchWithQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1).withDocumentTitle("Sundsvall intervju");

		when(repositoryMock.searchPublished("+sundsvall*", pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create().withQuery("sundsvall"));

		assertThat(result.getAudios()).hasSize(1);
		assertThat(result.getAudios().getFirst().getDocumentTitle()).isEqualTo("Sundsvall intervju");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).searchPublished("+sundsvall*", pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithFiltersRoutesToSearchFiltered() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1).withDocumentTitle("Sundsvall intervju");

		when(repositoryMock.searchFiltered("+sundsvall*", 1970, 1990, "Sundsvall", pageable))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create().withQuery("sundsvall").withYearFrom(1970).withYearTo(1990).withLocation("Sundsvall"));

		assertThat(result.getAudios()).hasSize(1);
		verify(repositoryMock).searchFiltered("+sundsvall*", 1970, 1990, "Sundsvall", pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithOnlyLocationFilterUsesSearchFilteredWithNullQueryAndTrimsLocation() {
		final var pageable = PageRequest.of(0, 100);
		when(repositoryMock.searchFiltered(null, null, null, "Timrå", pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		service.search(AudioParameters.create().withLocation("  Timrå  "));

		verify(repositoryMock).searchFiltered(null, null, null, "Timrå", pageable);
	}

	@Test
	void searchSanitizesOperatorsInQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1).withDocumentTitle("Midsommar");

		when(repositoryMock.searchPublished("+midsommar* +1985*", pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create().withQuery("+midsommar -1985"));

		assertThat(result.getAudios()).hasSize(1);
		verify(repositoryMock).searchPublished("+midsommar* +1985*", pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithNullQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1);

		when(repositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create());

		assertThat(result.getAudios()).hasSize(1);
		verify(repositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithBlankQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1);

		when(repositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create().withQuery("   "));

		assertThat(result.getAudios()).hasSize(1);
		verify(repositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithOperatorOnlyQueryFallsBackToFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);

		when(repositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(AudioParameters.create().withQuery("+-*"));

		assertThat(result.getAudios()).isEmpty();
		verify(repositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void getById() {
		final var id = 1;
		final var entity = AudioEntity.create().withAudioId(id).withDocumentTitle("Test");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		final var result = service.getById(id);

		assertThat(result).isNotNull();
		assertThat(result.getAudioId()).isEqualTo(id);
		assertThat(result.getDocumentTitle()).isEqualTo("Test");
		verify(repositoryMock).findById(id);
	}

	@Test
	void getByIdNotFound() {
		final var id = 999;

		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Audio with id '999' not found");
		verify(repositoryMock).findById(id);
	}

	@Test
	void streamFileDelegatesToFileStreamer() {
		final var id = 1;
		final var entity = AudioEntity.create().withAudioId(id).withObjectFilePath("/ljud/test.mp3").withAudioMimeType("audio/mpeg");
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		service.streamFile(id, responseMock);

		verify(fileStreamerMock).streamAttachment("/ljud//ljud/test.mp3", "audio/mpeg", "test.mp3", responseMock,
			"IOException occurred when streaming file for audio with id '1'");
	}

	@Test
	void streamFileFallsBackToOctetStreamAndDerivedNameWhenObjectPathBlank() {
		final var id = 3;
		final var entity = AudioEntity.create().withAudioId(id).withObjectFilePath("   ");
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		service.streamFile(id, responseMock);

		verify(fileStreamerMock).streamAttachment("/ljud/   ", "application/octet-stream", "audio-3", responseMock,
			"IOException occurred when streaming file for audio with id '3'");
	}

	@Test
	void openForPlaybackReturnsPayloadFromStreamer() {
		final var id = 1;
		final var entity = AudioEntity.create().withAudioId(id).withObjectFilePath("/a/interview.mp3").withAudioMimeType("audio/mpeg");
		final var expected = new StreamPayload(mock(Resource.class), "audio/mpeg", "interview.mp3");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(fileStreamerMock.openForPlayback("/ljud//a/interview.mp3", "audio/mpeg", "interview.mp3")).thenReturn(expected);

		assertThat(service.openForPlayback(id)).isSameAs(expected);
	}

	@Test
	void openForPlaybackFallsBackToOctetStreamWhenMimeMissing() {
		final var id = 2;
		final var entity = AudioEntity.create().withAudioId(id).withObjectFilePath("   ");
		final var expected = new StreamPayload(mock(Resource.class), "application/octet-stream", "audio-2");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(fileStreamerMock.openForPlayback("/ljud/   ", "application/octet-stream", "audio-2")).thenReturn(expected);

		assertThat(service.openForPlayback(id)).isSameAs(expected);
	}

	@Test
	void openForPlaybackNotFound() {
		final var id = 999;
		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.openForPlayback(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Audio with id '999' not found");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileNotFound() {
		final var id = 999;
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.streamFile(id, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Audio with id '999' not found");
		verify(repositoryMock).findById(id);
		verifyNoInteractions(fileStreamerMock);
	}
}
