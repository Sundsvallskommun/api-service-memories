package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.service.model.StreamPayload;
import se.sundsvall.memories.service.util.FileStreamer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.integration.samba.SambaTestProperties.SAMBA_PROPERTIES;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

	@Mock
	private FilmRepository repositoryMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private FilmService service;

	@BeforeEach
	void setUp() {
		service = new FilmService(repositoryMock, SAMBA_PROPERTIES, fileStreamerMock);
	}

	// Which rows a specification selects is verified against a real database in FilmSpecificationsTest. These tests only
	// cover what the service itself does: build the pageable, hand a specification to the repository, and map the page.

	@Test
	void searchDelegatesToRepositoryAndMapsThePage() {
		final var pageable = PageRequest.of(0, 100);
		final var entity = FilmEntity.create().withFilmId(1).withDocumentTitle("Sundsvall film");

		when(repositoryMock.findAll(ArgumentMatchers.<Specification<FilmEntity>>any(), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(FilmParameters.create().withQuery("sundsvall"));

		assertThat(result.getFilms()).hasSize(1);
		assertThat(result.getFilms().getFirst().getDocumentTitle()).isEqualTo("Sundsvall film");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).findAll(ArgumentMatchers.<Specification<FilmEntity>>any(), eq(pageable));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithoutFiltersStillPassesASpecification() {
		final var pageable = PageRequest.of(0, 100);

		when(repositoryMock.findAll(ArgumentMatchers.<Specification<FilmEntity>>any(), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(FilmParameters.create());

		assertThat(result.getFilms()).isEmpty();
		// Specification.allOf rejects null elements, so an unfiltered search must still yield a usable specification.
		final var specificationCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(repositoryMock).findAll(specificationCaptor.capture(), eq(pageable));
		assertThat(specificationCaptor.getValue()).isNotNull();
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25);
		final var entity = FilmEntity.create().withFilmId(1);

		when(repositoryMock.findAll(ArgumentMatchers.<Specification<FilmEntity>>any(), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 51));

		final var result = service.search(FilmParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
		verify(repositoryMock).findAll(ArgumentMatchers.<Specification<FilmEntity>>any(), eq(pageable));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void getById() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withDocumentTitle("Test");

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.of(entity));

		final var result = service.getById(id);

		assertThat(result).isNotNull();
		assertThat(result.getFilmId()).isEqualTo(id);
		assertThat(result.getDocumentTitle()).isEqualTo("Test");
		verify(repositoryMock).findOne(ArgumentMatchers.<Specification<FilmEntity>>any());
	}

	@Test
	void getByIdNotFound() {
		final var id = 999;

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verify(repositoryMock).findOne(ArgumentMatchers.<Specification<FilmEntity>>any());
	}

	@Test
	void streamFileDelegatesToFileStreamer() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("/films/test.mp4").withFilmMimeType("video/mp4");
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.of(entity));

		service.streamFile(id, responseMock);

		verify(fileStreamerMock).streamAttachment("/film//films/test.mp4", "video/mp4", "test.mp4", responseMock,
			"IOException occurred when streaming file for film with id '1'");
	}

	@Test
	void streamFileFallsBackToOctetStreamAndDerivedNameWhenObjectPathBlank() {
		final var id = 3;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("   ");
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.of(entity));

		service.streamFile(id, responseMock);

		verify(fileStreamerMock).streamAttachment("/film/   ", "application/octet-stream", "film-3", responseMock,
			"IOException occurred when streaming file for film with id '3'");
	}

	@Test
	void openForPlaybackReturnsPayloadFromStreamer() {
		final var id = 1;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("/a/midsommar.mp4").withFilmMimeType("video/mp4");
		final var expected = new StreamPayload(mock(Resource.class), "video/mp4", "midsommar.mp4");

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.of(entity));
		when(fileStreamerMock.openForPlayback("/film//a/midsommar.mp4", "video/mp4", "midsommar.mp4")).thenReturn(expected);

		assertThat(service.openForPlayback(id)).isSameAs(expected);
	}

	@Test
	void openForPlaybackFallsBackToOctetStreamWhenMimeMissing() {
		final var id = 2;
		final var entity = FilmEntity.create().withFilmId(id).withObjectFilePath("   ");
		final var expected = new StreamPayload(mock(Resource.class), "application/octet-stream", "film-2");

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.of(entity));
		when(fileStreamerMock.openForPlayback("/film/   ", "application/octet-stream", "film-2")).thenReturn(expected);

		assertThat(service.openForPlayback(id)).isSameAs(expected);
	}

	@Test
	void openForPlaybackNotFound() {
		final var id = 999;
		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.openForPlayback(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileNotFound() {
		final var id = 999;
		final var responseMock = mock(HttpServletResponse.class);

		when(repositoryMock.findOne(ArgumentMatchers.<Specification<FilmEntity>>any())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.streamFile(id, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verify(repositoryMock).findOne(ArgumentMatchers.<Specification<FilmEntity>>any());
		verifyNoInteractions(fileStreamerMock);
	}
}
