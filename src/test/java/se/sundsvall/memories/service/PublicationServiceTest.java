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
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
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
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/");

	@Mock
	private PublicationRepository publicationRepositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	private PublicationService service;

	private static PublicationEntity entity() {
		return PublicationEntity.create()
			.withPublicationId(207)
			.withDocumentTitle("Alfwar och Skämt")
			.withTopographyId(4)
			.withPublicationType("Broschyrer")
			.withThumbnailFilename("PUBL.id_207_fil_liten.jpeg")
			.withLargeImageFilename("PUBL.id_207_fil_stor.jpeg")
			.withOcrFilename("PUBL.id_207_fil_txt.xml")
			.withXmltext("<text>content</text>")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "PUBL.id_207_fil_liten.jpeg", "fil_liten"),
			Arguments.of(FileVariant.LARGE, "PUBL.id_207_fil_stor.jpeg", "fil_stor"),
			Arguments.of(FileVariant.TEXT, "PUBL.id_207_fil_txt.xml", "fil_txt"));
	}

	@BeforeEach
	void setUp() {
		service = new PublicationService(publicationRepositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES, topographyLookupMock);
	}

	@Test
	void searchWithQueryUsesFulltextRepository() {
		final var pageable = PageRequest.of(0, 100);
		when(publicationRepositoryMock.searchPublished("Drowning*", pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));
		when(topographyLookupMock.resolve(4)).thenReturn("Sundsvall");

		final var result = service.search(PublicationParameters.create().withQuery("Drowning"));

		assertThat(result.getPublications()).hasSize(1);
		assertThat(result.getPublications().getFirst().getPublicationType()).isEqualTo("Broschyrer");
		assertThat(result.getPublications().getFirst().getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getPublications().getFirst().getXmltext()).isNull();
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(publicationRepositoryMock).searchPublished("Drowning*", pageable);
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void searchWithNullQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(publicationRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(PublicationParameters.create());

		assertThat(result.getPublications()).hasSize(1);
		verify(publicationRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void searchWithBlankQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(publicationRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(PublicationParameters.create().withQuery("   "));

		assertThat(result.getPublications()).isEmpty();
		verify(publicationRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void searchWithOperatorOnlyQueryFallsBackToFindAll() {
		final var pageable = PageRequest.of(0, 100);
		when(publicationRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(PublicationParameters.create().withQuery("+-*"));

		assertThat(result.getPublications()).isEmpty();
		verify(publicationRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void getByIdIncludesXmltextAndLocation() {
		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(topographyLookupMock.resolve(4)).thenReturn("Sundsvall");

		final var result = service.getById(207);

		assertThat(result).isNotNull();
		assertThat(result.getPublicationId()).isEqualTo(207);
		assertThat(result.getXmltext()).isEqualTo("<text>content</text>");
		assertThat(result.getPublicationType()).isEqualTo("Broschyrer");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
	}

	@Test
	void getByIdNotFound() {
		when(publicationRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileForVariant(final FileVariant variant, final String expectedFilename, final String expectedSubfolder) throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		service.streamFile(207, variant, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(expectedFilename));
		verify(sambaIntegrationMock).streamFile("/publ/" + expectedSubfolder + "/" + expectedFilename, outputStreamMock);
	}

	@Test
	void streamFileNotFoundPublication() {
		final var responseMock = mock(HttpServletResponse.class);

		when(publicationRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withOcrFilename("   ");

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.TEXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'text'");
		verifyNoInteractions(sambaIntegrationMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withThumbnailFilename(null);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'thumbnail'");
	}

	@Test
	void streamFileWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenThrow(new IOException("boom"));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom");
	}
}
