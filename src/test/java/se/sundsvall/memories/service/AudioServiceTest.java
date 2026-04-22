package se.sundsvall.memories.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class AudioServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/");

	@Mock
	private AudioRepository repositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private OcmLookup ocmLookupMock;

	private AudioService service;

	@BeforeEach
	void setUp() {
		service = new AudioService(repositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES, topographyLookupMock, ocmLookupMock);
	}

	@Test
	void searchWithQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1).withDocumentTitle("Sundsvall intervju");

		when(repositoryMock.searchPublished("sundsvall*", pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create().withQuery("sundsvall"));

		assertThat(result.getAudios()).hasSize(1);
		assertThat(result.getAudios().getFirst().getDocumentTitle()).isEqualTo("Sundsvall intervju");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).searchPublished("sundsvall*", pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchSanitizesOperatorsInQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = AudioEntity.create().withAudioId(1).withDocumentTitle("Midsommar");

		when(repositoryMock.searchPublished("midsommar* 1985*", pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(AudioParameters.create().withQuery("+midsommar -1985"));

		assertThat(result.getAudios()).hasSize(1);
		verify(repositoryMock).searchPublished("midsommar* 1985*", pageable);
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
	void streamFile() throws IOException {
		final var id = 1;
		final var filePath = "/ljud/test.mp3";
		final var entity = AudioEntity.create()
			.withAudioId(id)
			.withObjectFilePath(filePath)
			.withAudioMimeType("audio/mpeg");
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(id, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "audio/mpeg");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"test.mp3\"");
		verify(repositoryMock).findById(id);
		verify(sambaIntegrationMock).streamFile("/ljud/" + filePath, outputStreamMock);
	}

	@Test
	void streamFileDerivesFilenameFromBackslashPath() throws IOException {
		final var id = 2;
		final var filePath = "\\\\server\\share\\ljud\\intervju1980.mp3";
		final var entity = AudioEntity.create()
			.withAudioId(id)
			.withObjectFilePath(filePath)
			.withAudioMimeType("audio/mpeg");
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(id, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "audio/mpeg");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"intervju1980.mp3\"");
		verify(sambaIntegrationMock).streamFile("/ljud/" + filePath, outputStreamMock);
	}

	@Test
	void streamFileFallsBackWhenObjFilIsBlank() throws IOException {
		final var id = 3;
		final var entity = AudioEntity.create()
			.withAudioId(id)
			.withObjectFilePath("   ");
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(id, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"audio-3\"");
		verify(sambaIntegrationMock).streamFile("/ljud/" + "   ", outputStreamMock);
	}

	@Test
	void openForPlaybackReturnsPayload() {
		final var id = 1;
		final var entity = AudioEntity.create()
			.withAudioId(id)
			.withObjectFilePath("/a/interview.mp3")
			.withAudioMimeType("audio/mpeg");
		final var resourceMock = org.mockito.Mockito.mock(org.springframework.core.io.Resource.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(sambaIntegrationMock.openResource("/ljud//a/interview.mp3")).thenReturn(resourceMock);

		final var payload = service.openForPlayback(id);

		assertThat(payload.resource()).isSameAs(resourceMock);
		assertThat(payload.mimeType()).isEqualTo("audio/mpeg");
		assertThat(payload.filename()).isEqualTo("interview.mp3");
		verify(sambaIntegrationMock).openResource("/ljud//a/interview.mp3");
	}

	@Test
	void openForPlaybackFallsBackToOctetStreamWhenMimeMissing() {
		final var id = 2;
		final var entity = AudioEntity.create()
			.withAudioId(id)
			.withObjectFilePath("   ");
		final var resourceMock = org.mockito.Mockito.mock(org.springframework.core.io.Resource.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(sambaIntegrationMock.openResource("/ljud/   ")).thenReturn(resourceMock);

		final var payload = service.openForPlayback(id);

		assertThat(payload.mimeType()).isEqualTo("application/octet-stream");
		assertThat(payload.filename()).isEqualTo("audio-2");
	}

	@Test
	void openForPlaybackNotFound() {
		final var id = 999;
		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.openForPlayback(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Audio with id '999' not found");
		verifyNoInteractions(sambaIntegrationMock);
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
		verifyNoInteractions(sambaIntegrationMock);
	}
}
