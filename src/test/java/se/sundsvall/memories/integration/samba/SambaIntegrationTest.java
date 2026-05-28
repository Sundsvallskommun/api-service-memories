package se.sundsvall.memories.integration.samba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class SambaIntegrationTest {

	private static final SambaIntegrationProperties PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/", "/text/");

	@Test
	void streamFile() {
		final var fileContent = "test-file-content".getBytes();
		final var outputStream = new ByteArrayOutputStream();

		try (final var _ = mockConstruction(SmbFile.class);
			final MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
				(mock, _) -> doAnswer(invocation -> {
					new ByteArrayInputStream(fileContent).transferTo(invocation.getArgument(0));
					return null;
				}).when(mock).transferTo(any()))) {

			final var integration = new SambaIntegration(PROPERTIES);
			integration.streamFile("/films/test.mp4", outputStream);

			assertThat(outputStream.toByteArray()).isEqualTo(fileContent);
			assertThat(smbInputStreamConstruction.constructed()).hasSize(1);
		}
	}

	@Test
	void streamFileNotFound() {
		final var outputStream = new ByteArrayOutputStream();

		try (var _ = mockConstruction(SmbFile.class);
			final MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
				(mock, _) -> doThrow(new IOException("The system cannot find the file specified"))
					.when(mock).transferTo(any()))) {

			final var integration = new SambaIntegration(PROPERTIES);

			final var exception = assertThrows(ThrowableProblem.class,
				() -> integration.streamFile("/films/missing.mp4", outputStream));

			assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
			assertThat(exception.getMessage()).contains("File not found at path '/films/missing.mp4'");
			assertThat(smbInputStreamConstruction.constructed()).hasSize(1);
		}
	}

	@Test
	void streamFileIOException() {
		final var outputStream = new ByteArrayOutputStream();

		try (final var _ = mockConstruction(SmbFile.class);
			final MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
				(mock, _) -> doThrow(new IOException("Connection reset"))
					.when(mock).transferTo(any()))) {

			final var integration = new SambaIntegration(PROPERTIES);

			final var exception = assertThrows(ThrowableProblem.class,
				() -> integration.streamFile("/films/error.mp4", outputStream));

			assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
			assertThat(exception.getMessage()).contains("Failed to read file from SMB share");
			assertThat(smbInputStreamConstruction.constructed()).hasSize(1);
		}
	}

	@Test
	void openResourceExposesLengthAndCachesIt() throws IOException {
		try (final var smbFileConstruction = mockConstruction(SmbFile.class,
			(mock, _) -> org.mockito.Mockito.when(mock.length()).thenReturn(4711L))) {

			final var integration = new SambaIntegration(PROPERTIES);
			final var resource = integration.openResource("/ljud/intervju.mp3");

			assertThat(resource.contentLength()).isEqualTo(4711L);
			assertThat(resource.contentLength()).isEqualTo(4711L);
			// Second lookup is served from the cache: only one SmbFile should have been constructed.
			assertThat(smbFileConstruction.constructed()).hasSize(1);
		}
	}

	@Test
	void openResourceGetInputStreamOpensFreshStreamEachCall() throws IOException {
		try (final var _ = mockConstruction(SmbFile.class);
			final MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class)) {

			final var integration = new SambaIntegration(PROPERTIES);
			final var resource = integration.openResource("/ljud/intervju.mp3");

			try (final var _ = resource.getInputStream()) {
				// first open
			}
			try (final var _ = resource.getInputStream()) {
				// second open — must be a separately constructed stream
			}

			assertThat(smbInputStreamConstruction.constructed()).hasSize(2);
		}
	}

	@Test
	void openResourceLengthLookupMapsFileNotFoundTo404() {
		try (final var _ = mockConstruction(SmbFile.class,
			(mock, _) -> org.mockito.Mockito.when(mock.length()).thenThrow(new SmbException("The system cannot find the file specified")))) {

			final var integration = new SambaIntegration(PROPERTIES);
			final var resource = integration.openResource("/ljud/missing.mp3");

			final var exception = assertThrows(ThrowableProblem.class, resource::contentLength);
			assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
			assertThat(exception.getMessage()).contains("File not found at path '/ljud/missing.mp3'");
		}
	}

	@Test
	void openInputStreamReturnsStream() {
		try (final var _ = mockConstruction(SmbFile.class);
			final MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class)) {

			final var integration = new SambaIntegration(PROPERTIES);

			final var stream = integration.openInputStream("/ljud/intervju.mp3");

			assertThat(stream).isNotNull();
			assertThat(smbInputStreamConstruction.constructed()).hasSize(1);
		}
	}

	@Test
	void streamFileIOExceptionWithNullMessage() {
		final var outputStream = new ByteArrayOutputStream();

		try (final var _ = mockConstruction(SmbFile.class);
			final MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
				(mock, _) -> doThrow(new IOException((String) null))
					.when(mock).transferTo(any()))) {

			final var integration = new SambaIntegration(PROPERTIES);

			final var exception = assertThrows(ThrowableProblem.class,
				() -> integration.streamFile("/films/error.mp4", outputStream));

			assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
			assertThat(exception.getMessage()).contains("Failed to read file from SMB share");
			assertThat(smbInputStreamConstruction.constructed()).hasSize(1);
		}
	}
}
