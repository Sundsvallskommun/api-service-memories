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
import se.sundsvall.memories.api.model.FotoParameters;
import se.sundsvall.memories.integration.db.FotoRepository;
import se.sundsvall.memories.integration.db.model.FotoEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.FotoService.FileVariant;

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
class FotoServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/");

	@Mock
	private FotoRepository fotoRepositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private TopografiLookup topografiLookupMock;

	private FotoService service;

	private static FotoEntity entity() {
		return FotoEntity.create()
			.withFotoId(1234)
			.withDoktitel("Stadsvy")
			.withFilLiten("FOTO.id_1234_fil_liten.jpg")
			.withFilStor("FOTO.id_1234_fil_stor.jpg")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.LITEN, "FOTO.id_1234_fil_liten.jpg", "fil_liten"),
			Arguments.of(FileVariant.STOR, "FOTO.id_1234_fil_stor.jpg", "fil_stor"));
	}

	@BeforeEach
	void setUp() {
		service = new FotoService(fotoRepositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES, topografiLookupMock);
	}

	@Test
	void searchWithQueryUsesFulltextRepository() {
		final var pageable = PageRequest.of(0, 100);
		when(fotoRepositoryMock.searchPublished("Sundsvall*", pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(FotoParameters.create().withQuery("Sundsvall"));

		assertThat(result.getPhotos()).hasSize(1);
		assertThat(result.getPhotos().getFirst().getDoktitel()).isEqualTo("Stadsvy");
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(fotoRepositoryMock).searchPublished("Sundsvall*", pageable);
		verifyNoMoreInteractions(fotoRepositoryMock);
	}

	@Test
	void searchWithNullQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(fotoRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(FotoParameters.create());

		assertThat(result.getPhotos()).hasSize(1);
		verify(fotoRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(fotoRepositoryMock);
	}

	@Test
	void searchWithBlankQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(fotoRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(FotoParameters.create().withQuery("   "));

		assertThat(result.getPhotos()).isEmpty();
		verify(fotoRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(fotoRepositoryMock);
	}

	@Test
	void getById() {
		when(fotoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));

		final var result = service.getById(1234);

		assertThat(result).isNotNull();
		assertThat(result.getFotoId()).isEqualTo(1234);
	}

	@Test
	void getByIdNotFound() {
		when(fotoRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Photo with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileForVariant(final FileVariant variant, final String expectedFilename, final String expectedSubfolder) throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(fotoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(1234, variant, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(expectedFilename));
		verify(sambaIntegrationMock).streamFile("/foto/" + expectedSubfolder + "/" + expectedFilename, outputStreamMock);
	}

	@Test
	void streamFileNotFoundPhoto() {
		final var responseMock = mock(HttpServletResponse.class);

		when(fotoRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.LITEN, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Photo with id '999' not found");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withFilLiten("   ");

		when(fotoRepositoryMock.findById(1234)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.LITEN, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'liten'");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withFilStor(null);

		when(fotoRepositoryMock.findById(1234)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.STOR, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'stor'");
	}

	@Test
	void streamFileWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);

		when(fotoRepositoryMock.findById(1234)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenThrow(new IOException("boom"));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1234, FileVariant.LITEN, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom");
	}
}
