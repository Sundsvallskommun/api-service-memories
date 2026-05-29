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
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
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

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/", "/text/");

	@Mock
	private FilmRepository repositoryMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private FilmService service;

	@BeforeEach
	void setUp() {
		service = new FilmService(repositoryMock, SAMBA_PROPERTIES, topographyLookupMock, fileStreamerMock);
	}

	@Test
	void searchWithQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = FilmEntity.create().withFilmId(1).withDocumentTitle("Sundsvall film");

		when(repositoryMock.searchPublished("+sundsvall*", pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(FilmParameters.create().withQuery("sundsvall"));

		assertThat(result.getFilms()).hasSize(1);
		assertThat(result.getFilms().getFirst().getDocumentTitle()).isEqualTo("Sundsvall film");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).searchPublished("+sundsvall*", pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchSanitizesOperatorsInQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = FilmEntity.create().withFilmId(1).withDocumentTitle("Midsommar");

		when(repositoryMock.searchPublished("+midsommar* +1985*", pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(FilmParameters.create().withQuery("+midsommar -1985"));

		assertThat(result.getFilms()).hasSize(1);
		verify(repositoryMock).searchPublished("+midsommar* +1985*", pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithNullQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = FilmEntity.create().withFilmId(1);

		when(repositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(FilmParameters.create());

		assertThat(result.getFilms()).hasSize(1);
		verify(repositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithBlankQuery() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = FilmEntity.create().withFilmId(1);

		when(repositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(FilmParameters.create().withQuery("   "));

		assertThat(result.getFilms()).hasSize(1);
		verify(repositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithOperatorOnlyQueryFallsBackToFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);

		when(repositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(FilmParameters.create().withQuery("+-*"));

		assertThat(result.getFilms()).isEmpty();
		verify(repositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void getById() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withDocumentTitle("Test");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		final var result = service.getById(id);

		assertThat(result).isNotNull();
		assertThat(result.getFilmId()).isEqualTo(id);
		assertThat(result.getDocumentTitle()).isEqualTo("Test");
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
	void streamFileDelegatesToFileStreamer() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("/films/test.mp4").withFilmMimeType("video/mp4");
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		service.streamFile(id, responseMock);

		verify(fileStreamerMock).streamAttachment("/film//films/test.mp4", "video/mp4", "test.mp4", responseMock,
			"IOException occurred when streaming file for film with id '1'");
	}

	@Test
	void streamFileFallsBackToOctetStreamAndDerivedNameWhenObjectPathBlank() {
		final var id = 3;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("   ");
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		service.streamFile(id, responseMock);

		verify(fileStreamerMock).streamAttachment("/film/   ", "application/octet-stream", "film-3", responseMock,
			"IOException occurred when streaming file for film with id '3'");
	}

	@Test
	void openForPlaybackReturnsPayloadFromStreamer() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("/a/midsommar.mp4").withFilmMimeType("video/mp4");
		final var expected = new StreamPayload(mock(Resource.class), "video/mp4", "midsommar.mp4");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(fileStreamerMock.openForPlayback("/film//a/midsommar.mp4", "video/mp4", "midsommar.mp4")).thenReturn(expected);

		assertThat(service.openForPlayback(id)).isSameAs(expected);
	}

	@Test
	void openForPlaybackFallsBackToOctetStreamWhenMimeMissing() {
		final var id = 2;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("   ");
		final var expected = new StreamPayload(mock(Resource.class), "application/octet-stream", "film-2");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));
		when(fileStreamerMock.openForPlayback("/film/   ", "application/octet-stream", "film-2")).thenReturn(expected);

		assertThat(service.openForPlayback(id)).isSameAs(expected);
	}

	@Test
	void openForPlaybackNotFound() {
		final var id = 999;
		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.openForPlayback(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verifyNoInteractions(fileStreamerMock);
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
		verifyNoInteractions(fileStreamerMock);
	}
}
