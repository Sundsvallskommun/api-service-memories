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
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;
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
class FilmServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/");

	@Mock
	private FilmRepository repositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	private FilmService service;

	@BeforeEach
	void setUp() {
		service = new FilmService(repositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES);
	}

	@Test
	void searchWithQuery() {
		final var query = "sundsvall";
		final var entity = FilmEntity.create().withFilmId(1).withDoktitel("Sundsvall film");

		when(repositoryMock.searchPublished("sundsvall*")).thenReturn(List.of(entity));

		final var result = service.search(query);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getDoktitel()).isEqualTo("Sundsvall film");
		verify(repositoryMock).searchPublished("sundsvall*");
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchSanitizesOperatorsInQuery() {
		final var entity = FilmEntity.create().withFilmId(1).withDoktitel("Midsommar");

		when(repositoryMock.searchPublished("midsommar* 1985*")).thenReturn(List.of(entity));

		final var result = service.search("+midsommar -1985");

		assertThat(result).hasSize(1);
		verify(repositoryMock).searchPublished("midsommar* 1985*");
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithNullQuery() {
		final var entity = FilmEntity.create().withFilmId(1);

		when(repositoryMock.findAllPublished()).thenReturn(List.of(entity));

		final var result = service.search(null);

		assertThat(result).hasSize(1);
		verify(repositoryMock).findAllPublished();
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithBlankQuery() {
		final var entity = FilmEntity.create().withFilmId(1);

		when(repositoryMock.findAllPublished()).thenReturn(List.of(entity));

		final var result = service.search("   ");

		assertThat(result).hasSize(1);
		verify(repositoryMock).findAllPublished();
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithOperatorOnlyQueryFallsBackToFindAllPublished() {
		when(repositoryMock.findAllPublished()).thenReturn(List.of());

		final var result = service.search("+-*");

		assertThat(result).isEmpty();
		verify(repositoryMock).findAllPublished();
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void getById() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withDoktitel("Test");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		final var result = service.getById(id);

		assertThat(result).isNotNull();
		assertThat(result.getFilmId()).isEqualTo(id);
		assertThat(result.getDoktitel()).isEqualTo("Test");
		verify(repositoryMock).findById(id);
	}

	@Test
	void getByIdNotFound() {
		final var id = 999;

		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verify(repositoryMock).findById(id);
	}

	@Test
	void streamFile() throws IOException {
		final var id = 1;
		final var filePath = "/films/test.mp4";
		final var entity = FilmEntity.create()
			.withFilmId(id)
			.withFilmObjFil(filePath)
			.withFilmMimeType("video/mp4");
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(id, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "video/mp4");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"test.mp4\"");
		verify(repositoryMock).findById(id);
		verify(sambaIntegrationMock).streamFile("/film/" + filePath, outputStreamMock);
	}

	@Test
	void streamFileDerivesFilenameFromBackslashPath() throws IOException {
		final var id = 2;
		final var filePath = "\\\\server\\share\\films\\midsommar1985.avi";
		final var entity = FilmEntity.create()
			.withFilmId(id)
			.withFilmObjFil(filePath)
			.withFilmMimeType("video/avi");
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(id, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "video/avi");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"midsommar1985.avi\"");
		verify(sambaIntegrationMock).streamFile("/film/" + filePath, outputStreamMock);
	}

	@Test
	void streamFileFallsBackWhenFilmObjFilIsBlank() throws IOException {
		final var id = 3;
		final var entity = FilmEntity.create()
			.withFilmId(id)
			.withFilmObjFil("   ");
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(id, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"film-3\"");
		verify(sambaIntegrationMock).streamFile("/film/" + "   ", outputStreamMock);
	}

	@Test
	void streamFileNotFound() {
		final var id = 999;
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.streamFile(id, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verify(repositoryMock).findById(id);
		verifyNoInteractions(sambaIntegrationMock);
	}
}
