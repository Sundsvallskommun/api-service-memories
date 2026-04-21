package se.sundsvall.memories.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.PhotoService.FileVariant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/");

	@Mock
	private PhotoRepository photoRepositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	private PhotoService service;

	private static PhotoEntity entity() {
		return PhotoEntity.create()
			.withPhotoId(1234)
			.withDocumentTitle("Stadsvy")
			.withThumbnailFilename("FOTO.id_1234_fil_liten.jpg")
			.withLargeImageFilename("FOTO.id_1234_fil_stor.jpg")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "FOTO.id_1234_fil_liten.jpg", "fil_liten"),
			Arguments.of(FileVariant.LARGE, "FOTO.id_1234_fil_stor.jpg", "fil_stor"));
	}

	@BeforeEach
	void setUp() {
		service = new PhotoService(photoRepositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES, topographyLookupMock);
	}

	@Test
	void searchWithQueryUsesFulltextRepository() {
		final var pageable = PageRequest.of(0, 100);
		when(photoRepositoryMock.searchPublished("Sundsvall*", pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(PhotoParameters.create().withQuery("Sundsvall"));

		assertThat(result.getPhotos()).hasSize(1);
		assertThat(result.getPhotos().getFirst().getDocumentTitle()).isEqualTo("Stadsvy");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(photoRepositoryMock).searchPublished("Sundsvall*", pageable);
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void searchWithNullQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(photoRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(PhotoParameters.create());

		assertThat(result.getPhotos()).hasSize(1);
		verify(photoRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void searchWithBlankQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(photoRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(PhotoParameters.create().withQuery("   "));

		assertThat(result.getPhotos()).isEmpty();
		verify(photoRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void getById() {
		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));

		final var result = service.getById(1234);

		assertThat(result).isNotNull();
		assertThat(result.getPhotoId()).isEqualTo(1234);
	}

	@Test
	void getByIdNotFound() {
		when(photoRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Photo with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileForVariant(final FileVariant variant, final String expectedFilename, final String expectedSubfolder) throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(1234, variant, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(expectedFilename));
		verify(sambaIntegrationMock).streamFile("/foto/" + expectedSubfolder + "/" + expectedFilename, outputStreamMock);
	}

	@Test
	void streamFileNotFoundPhoto() {
		final var responseMock = mock(HttpServletResponse.class);

		when(photoRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Photo with id '999' not found");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withThumbnailFilename("   ");

		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'thumbnail'");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withLargeImageFilename(null);

		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.LARGE, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'large'");
	}

	@Test
	void streamFileWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);

		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenThrow(new IOException("boom"));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom");
	}
}
