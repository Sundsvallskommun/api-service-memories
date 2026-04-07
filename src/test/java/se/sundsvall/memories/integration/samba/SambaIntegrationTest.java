package se.sundsvall.memories.integration.samba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
		"localhost", 445, "WORKGROUP", "user", "password", "/share/");

	@Test
	void streamFile() throws IOException {
		final var fileContent = "test-file-content".getBytes();
		final var outputStream = new ByteArrayOutputStream();

		try (MockedConstruction<SmbFile> ignored = mockConstruction(SmbFile.class);
			MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
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

		try (MockedConstruction<SmbFile> ignored = mockConstruction(SmbFile.class);
			MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
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

		try (MockedConstruction<SmbFile> ignored = mockConstruction(SmbFile.class);
			MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
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
	void streamFileIOExceptionWithNullMessage() {
		final var outputStream = new ByteArrayOutputStream();

		try (MockedConstruction<SmbFile> ignored = mockConstruction(SmbFile.class);
			MockedConstruction<SmbFileInputStream> smbInputStreamConstruction = mockConstruction(SmbFileInputStream.class,
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
