package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.service.model.FileVariant;
import se.sundsvall.memories.service.util.FileStreamer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.integration.samba.SambaTestProperties.SAMBA_PROPERTIES;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

	private static final String STREAM_ERROR_CONTEXT = "IOException occurred when streaming file for photo with id '1234'";

	@Mock
	private PhotoRepository photoRepositoryMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private PhotoService service;

	private static PhotoEntity entity() {
		return PhotoEntity.create()
			.withId(1234)
			.withDocumentTitle("Stadsvy")
			.withThumbnailFilename("FOTO.id_1234_fil_liten.jpg")
			.withLargeImageFilename("FOTO.id_1234_fil_stor.jpg")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "/foto/fil_liten/FOTO.id_1234_fil_liten.jpg", "FOTO.id_1234_fil_liten.jpg", "sundsvallsminnen-foto-1234.jpg"),
			Arguments.of(FileVariant.LARGE, "/foto/fil_stor/FOTO.id_1234_fil_stor.jpg", "FOTO.id_1234_fil_stor.jpg", "sundsvallsminnen-foto-1234.jpg"));
	}

	@BeforeEach
	void setUp() {
		service = new PhotoService(photoRepositoryMock, SAMBA_PROPERTIES, fileStreamerMock);
	}

	// Which rows the filters select is verified against a real database in PhotoSpecificationTest. These tests cover
	// what the service itself does: build the pageable, forward the parameters, and map the resulting page.

	@Test
	void searchDelegatesToRepositoryAndMapsThePage() {
		final var pageable = PageRequest.of(0, 100, Sort.by("id"));
		when(photoRepositoryMock.findAllByParameters(any(PhotoParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(PhotoParameters.create().withQuery("Sundsvall").withObjectType("Foto"));

		assertThat(result.getPhotos()).hasSize(1);
		assertThat(result.getPhotos().getFirst().getDocumentTitle()).isEqualTo("Stadsvy");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(photoRepositoryMock).findAllByParameters(any(PhotoParameters.class), eq(pageable));
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100, Sort.by("id"));
		final var parameters = PhotoParameters.create();
		when(photoRepositoryMock.findAllByParameters(any(PhotoParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getPhotos()).isEmpty();
		// Which filters the parameters turn into is the repository's job, verified against a real database in
		// PhotoSpecificationTest. The service only has to hand them over untouched.
		final var parametersCaptor = ArgumentCaptor.forClass(PhotoParameters.class);
		verify(photoRepositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25, Sort.by("id"));
		when(photoRepositoryMock.findAllByParameters(any(PhotoParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 51));

		final var result = service.search(PhotoParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
		verify(photoRepositoryMock).findAllByParameters(any(PhotoParameters.class), eq(pageable));
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void getByIdReturnsDetailWithRelatedPhotosAndSubjects() {
		final var entity = entity().withSubjects(new LinkedHashSet<>(List.of(
			OcmEntity.create().withId(10).withCode("ALM").withText("Allmänt"),
			OcmEntity.create().withId(20).withCode("MUS").withText("Musik"))));
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity));
		when(photoRepositoryMock.findRelatedPhotoIds(1234)).thenReturn(List.of(2001, 2002));

		final var result = service.getById(1234);

		assertThat(result).isNotNull();
		assertThat(result.getPhotoId()).isEqualTo(1234);
		assertThat(result.getRelatedPhotoIds()).containsExactly(2001, 2002);
		assertThat(result.getSubjects())
			.extracting(Subject::getCode, Subject::getText)
			.containsExactly(tuple("ALM", "Allmänt"), tuple("MUS", "Musik"));
	}

	@Test
	void getByIdWithoutSubjectsReturnsAnEmptyList() {
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity()));
		when(photoRepositoryMock.findRelatedPhotoIds(1234)).thenReturn(List.of());

		assertThat(service.getById(1234).getSubjects()).isEmpty();
	}

	@Test
	void getByIdNotFound() {
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Photo with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileDelegatesToFileStreamer(final FileVariant variant, final String expectedPath, final String expectedFilename, final String expectedDownloadFilename) {
		final var responseMock = mock(HttpServletResponse.class);
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity()));

		service.streamFile(1234, variant, responseMock);

		// Photos never transform to HTML, so the flag is always false.
		verify(fileStreamerMock).streamInline(expectedPath, expectedFilename, expectedDownloadFilename, false, responseMock, STREAM_ERROR_CONTEXT);
	}

	@Test
	void streamFileNotFoundPhoto() {
		final var responseMock = mock(HttpServletResponse.class);
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Photo with id '999' not found");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withThumbnailFilename("   ");
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'thumbnail'");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withLargeImageFilename(null);
		when(photoRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.LARGE, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'large'");
		verifyNoInteractions(fileStreamerMock);
	}
}
