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
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.integration.db.PublRepository;
import se.sundsvall.memories.integration.db.PublTypRepository;
import se.sundsvall.memories.integration.db.model.PublEntity;
import se.sundsvall.memories.integration.db.model.PublTypEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.PublicationService.FileVariant;

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
class PublicationServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film", "/publ/");

	@Mock
	private PublRepository publRepositoryMock;

	@Mock
	private PublTypRepository publTypRepositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	private PublicationService service;

	private static PublTypEntity publTyp(final int id, final String name) {
		return PublTypEntity.create().withId(id).withPubliktyp(name);
	}

	private static PublEntity entity() {
		return PublEntity.create()
			.withPublId(207)
			.withDoktitel("Alfwar och Skämt")
			.withPtId(4)
			.withFilLiten("PUBL.id_207_fil_liten.jpeg")
			.withFilStor("PUBL.id_207_fil_stor.jpeg")
			.withFilOriginal("PUBL.id_207_fil_original.jpeg")
			.withFilTxt("PUBL.id_207_fil_txt.xml")
			.withFilXtra("PUBL.id_207_fil_xtra.jpeg")
			.withXmltext("<text>content</text>")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.LITEN, "PUBL.id_207_fil_liten.jpeg", "fil_liten"),
			Arguments.of(FileVariant.STOR, "PUBL.id_207_fil_stor.jpeg", "fil_stor"),
			Arguments.of(FileVariant.ORIGINAL, "PUBL.id_207_fil_original.jpeg", "fil_original"),
			Arguments.of(FileVariant.TXT, "PUBL.id_207_fil_txt.xml", "fil_txt"),
			Arguments.of(FileVariant.XTRA, "PUBL.id_207_fil_xtra.jpeg", "fil_xtra"));
	}

	@BeforeEach
	void setUp() {
		service = new PublicationService(publRepositoryMock, publTypRepositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES);
	}

	@Test
	void searchWithQueryUsesFulltextRepository() {
		when(publTypRepositoryMock.findAll()).thenReturn(List.of(publTyp(4, "Broschyrer")));
		when(publRepositoryMock.searchPublished("Drunkningsolycka*")).thenReturn(List.of(entity()));

		final var result = service.search("Drunkningsolycka");

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getPubliktypName()).isEqualTo("Broschyrer");
		assertThat(result.getFirst().getXmltext()).isNull();
		verify(publRepositoryMock).searchPublished("Drunkningsolycka*");
		verifyNoMoreInteractions(publRepositoryMock);
	}

	@Test
	void searchWithNullQueryUsesFindAllPublished() {
		when(publTypRepositoryMock.findAll()).thenReturn(List.of(publTyp(4, "Broschyrer")));
		when(publRepositoryMock.findAllPublished()).thenReturn(List.of(entity()));

		final var result = service.search(null);

		assertThat(result).hasSize(1);
		verify(publRepositoryMock).findAllPublished();
		verifyNoMoreInteractions(publRepositoryMock);
	}

	@Test
	void searchWithBlankQueryUsesFindAllPublished() {
		when(publTypRepositoryMock.findAll()).thenReturn(List.of());
		when(publRepositoryMock.findAllPublished()).thenReturn(List.of());

		final var result = service.search("   ");

		assertThat(result).isEmpty();
		verify(publRepositoryMock).findAllPublished();
		verifyNoMoreInteractions(publRepositoryMock);
	}

	@Test
	void searchWithOperatorOnlyQueryFallsBackToFindAll() {
		when(publTypRepositoryMock.findAll()).thenReturn(List.of());
		when(publRepositoryMock.findAllPublished()).thenReturn(List.of());

		final var result = service.search("+-*");

		assertThat(result).isEmpty();
		verify(publRepositoryMock).findAllPublished();
		verifyNoMoreInteractions(publRepositoryMock);
	}

	@Test
	void getByIdIncludesXmltext() {
		when(publTypRepositoryMock.findAll()).thenReturn(List.of(publTyp(4, "Broschyrer")));
		when(publRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));

		final var result = service.getById(207);

		assertThat(result).isNotNull();
		assertThat(result.getPublId()).isEqualTo(207);
		assertThat(result.getXmltext()).isEqualTo("<text>content</text>");
		assertThat(result.getPubliktypName()).isEqualTo("Broschyrer");
	}

	@Test
	void getByIdNotFound() {
		when(publRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileForVariant(final FileVariant variant, final String expectedFilename, final String expectedSubfolder) throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(publRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(207, variant, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(expectedFilename));
		verify(sambaIntegrationMock).streamFile("/publ/" + expectedSubfolder + "/" + expectedFilename, outputStreamMock);
	}

	@Test
	void streamFileNotFoundPublication() {
		final var responseMock = mock(HttpServletResponse.class);

		when(publRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.LITEN, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withFilXtra("   ");

		when(publRepositoryMock.findById(207)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.XTRA, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'xtra'");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withFilTxt(null);

		when(publRepositoryMock.findById(207)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.TXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'txt'");
	}

	@Test
	void streamFileWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);

		when(publRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenThrow(new IOException("boom"));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.LITEN, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom");
	}
}
