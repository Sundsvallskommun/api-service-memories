package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.FotoOcmRepository;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.FotoOcmEntity;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.service.PhotoService.FileVariant;
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
class PhotoServiceTest {

	private static final String STREAM_ERROR_CONTEXT = "IOException occurred when streaming file for photo with id '1234'";

	@Mock
	private PhotoRepository photoRepositoryMock;

	@Mock
	private FotoOcmRepository fotoOcmRepositoryMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private OcmLookup ocmLookupMock;

	@Mock
	private FileStreamer fileStreamerMock;

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
			Arguments.of(FileVariant.THUMBNAIL, "/foto/fil_liten/FOTO.id_1234_fil_liten.jpg", "FOTO.id_1234_fil_liten.jpg"),
			Arguments.of(FileVariant.LARGE, "/foto/fil_stor/FOTO.id_1234_fil_stor.jpg", "FOTO.id_1234_fil_stor.jpg"));
	}

	@BeforeEach
	void setUp() {
		service = new PhotoService(photoRepositoryMock, fotoOcmRepositoryMock, SAMBA_PROPERTIES, topographyLookupMock, ocmLookupMock, fileStreamerMock);
	}

	// Which rows a specification selects is verified against a real database in PhotoSpecificationsTest. These tests
	// only cover what the service itself does: build the pageable, hand a specification to the repository, and map the
	// resulting page.

	@Test
	void searchDelegatesToRepositoryAndMapsThePage() {
		final var pageable = PageRequest.of(0, 100);
		when(photoRepositoryMock.findAll(ArgumentMatchers.<Specification<PhotoEntity>>any(), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(PhotoParameters.create().withQuery("Sundsvall").withObjectType("Foto"));

		assertThat(result.getPhotos()).hasSize(1);
		assertThat(result.getPhotos().getFirst().getDocumentTitle()).isEqualTo("Stadsvy");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(photoRepositoryMock).findAll(ArgumentMatchers.<Specification<PhotoEntity>>any(), eq(pageable));
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void searchWithoutFiltersStillPassesASpecification() {
		final var pageable = PageRequest.of(0, 100);
		when(photoRepositoryMock.findAll(ArgumentMatchers.<Specification<PhotoEntity>>any(), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(PhotoParameters.create());

		assertThat(result.getPhotos()).isEmpty();
		// Specification.allOf rejects null elements, so an unfiltered search must still yield a usable specification.
		final var specificationCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(photoRepositoryMock).findAll(specificationCaptor.capture(), eq(pageable));
		assertThat(specificationCaptor.getValue()).isNotNull();
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25);
		when(photoRepositoryMock.findAll(ArgumentMatchers.<Specification<PhotoEntity>>any(), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 51));

		final var result = service.search(PhotoParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
		verify(photoRepositoryMock).findAll(ArgumentMatchers.<Specification<PhotoEntity>>any(), eq(pageable));
		verifyNoMoreInteractions(photoRepositoryMock);
	}

	@Test
	void getByIdReturnsDetailWithRelatedPhotosAndSubjects() {
		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));
		when(photoRepositoryMock.findRelatedPhotoIds(1234)).thenReturn(List.of(2001, 2002));
		when(fotoOcmRepositoryMock.findByPhotoIdOrderById(1234)).thenReturn(List.of(
			FotoOcmEntity.create().withPhotoId(1234).withOcmId(10),
			FotoOcmEntity.create().withPhotoId(1234).withOcmId(20)));
		when(ocmLookupMock.resolveSubject(10)).thenReturn(Subject.create().withCode("ALM").withText("Allmänt"));
		when(ocmLookupMock.resolveSubject(20)).thenReturn(null);

		final var result = service.getById(1234);

		assertThat(result).isNotNull();
		assertThat(result.getPhotoId()).isEqualTo(1234);
		assertThat(result.getRelatedPhotoIds()).containsExactly(2001, 2002);
		assertThat(result.getSubjects()).hasSize(1);
		assertThat(result.getSubjects().getFirst().getCode()).isEqualTo("ALM");
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
	void streamFileDelegatesToFileStreamer(final FileVariant variant, final String expectedPath, final String expectedFilename) {
		final var responseMock = mock(HttpServletResponse.class);
		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));

		service.streamFile(1234, variant, responseMock);

		// Photos never transform to HTML, so the flag is always false.
		verify(fileStreamerMock).streamInline(expectedPath, expectedFilename, false, responseMock, STREAM_ERROR_CONTEXT);
	}

	@Test
	void streamFileNotFoundPhoto() {
		final var responseMock = mock(HttpServletResponse.class);
		when(photoRepositoryMock.findById(999)).thenReturn(Optional.empty());

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
		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entityMissingFile));

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
		when(photoRepositoryMock.findById(1234)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.LARGE, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'large'");
		verifyNoInteractions(fileStreamerMock);
	}
}
