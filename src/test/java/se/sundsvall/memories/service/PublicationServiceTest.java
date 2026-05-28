package se.sundsvall.memories.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.core.io.ByteArrayResource;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/", "/text/");

	// JPEG SOI marker — Tika identifies it as image/jpeg
	private static final byte[] JPEG_BYTES = new byte[] {
		(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
	};

	// %PDF-1.4 — Tika identifies it as application/pdf
	private static final byte[] PDF_BYTES = "%PDF-1.4\n%abc\n".getBytes();

	@Mock
	private PublicationRepository publicationRepositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private PublicationTypeLookup publicationTypeLookupMock;

	@Mock
	private XsltTransformer xsltTransformerMock;

	private PublicationService service;

	private static PublicationEntity entity() {
		return PublicationEntity.create()
			.withPublicationId(207)
			.withDocumentTitle("Alfwar och Skämt")
			.withPublisherTopographyId(1)
			.withPublicationTypeId(4)
			.withPublicationType("Broschyrer")
			.withThumbnailFilename("PUBL.id_207_fil_liten.jpeg")
			.withLargeImageFilename("PUBL.id_207_fil_stor.jpeg")
			.withOcrFilename("PUBL.id_207_fil_txt.xml")
			.withXmltext("<text>content</text>")
			.withOptions(4);
	}

	static Stream<Arguments> binaryFileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "PUBL.id_207_fil_liten.jpeg", "fil_liten"),
			Arguments.of(FileVariant.LARGE, "PUBL.id_207_fil_stor.jpeg", "fil_stor"));
	}

	@BeforeEach
	void setUp() {
		service = new PublicationService(publicationRepositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES,
			topographyLookupMock, publicationTypeLookupMock, xsltTransformerMock, new FileTypeDetector());
	}

	@Test
	void searchWithQueryUsesFulltextRepository() {
		final var pageable = PageRequest.of(0, 100);
		when(publicationRepositoryMock.searchPublished("Drowning*", pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));
		when(topographyLookupMock.resolve(1)).thenReturn("Sundsvall");
		when(publicationTypeLookupMock.resolve(4)).thenReturn("Tidning");

		final var result = service.search(PublicationParameters.create().withQuery("Drowning"));

		assertThat(result.getPublications()).hasSize(1);
		assertThat(result.getPublications().getFirst().getPublicationType()).isEqualTo("Tidning");
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
	void getByIdIncludesXmltextAndResolvedFields() {
		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(topographyLookupMock.resolve(1)).thenReturn("Sundsvall");
		when(publicationTypeLookupMock.resolve(4)).thenReturn("Tidning");

		final var result = service.getById(207);

		assertThat(result).isNotNull();
		assertThat(result.getPublicationId()).isEqualTo(207);
		assertThat(result.getXmltext()).isEqualTo("<text>content</text>");
		assertThat(result.getPublicationType()).isEqualTo("Tidning");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
	}

	@Test
	void getByIdFallsBackToFritextPublicationTypeWhenLookupMisses() {
		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(publicationTypeLookupMock.resolve(4)).thenReturn(null);

		final var result = service.getById(207);

		assertThat(result.getPublicationType()).isEqualTo("Broschyrer");
	}

	@Test
	void getByIdNotFound() {
		when(publicationRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("binaryFileVariants")
	void streamFileForBinaryVariant(final FileVariant variant, final String expectedFilename, final String expectedSubfolder) throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/" + expectedSubfolder + "/" + expectedFilename))
			.thenReturn(new ByteArrayResource(JPEG_BYTES));

		service.streamFile(207, variant, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "image/jpeg");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"%s\"".formatted(expectedFilename));
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileForTextVariantTransformsXmlToHtml() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/fil_txt/PUBL.id_207_fil_txt.xml"))
			.thenReturn(new ByteArrayResource("<?xml version=\"1.0\"?><Document><Title>Hi</Title></Document>".getBytes()));
		doAnswer(invocation -> {
			final InputStream in = invocation.getArgument(0);
			final OutputStream out = invocation.getArgument(1);
			out.write(("<html>" + new String(in.readAllBytes()) + "</html>").getBytes());
			return null;
		}).when(xsltTransformerMock).transform(any(InputStream.class), any(OutputStream.class));

		service.streamFile(207, FileVariant.TEXT, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "text/html;charset=UTF-8");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"PUBL.id_207_fil_txt.html\"");
		verify(xsltTransformerMock).transform(any(InputStream.class), any(OutputStream.class));
		verify(sambaIntegrationMock).openResource("/publ/fil_txt/PUBL.id_207_fil_txt.xml");
	}

	@Test
	void streamFileForTextVariantWithPdfStreamsBinary() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);
		final var entityPdfText = entity().withOcrFilename("PUBL.id_207_fil_txt.pdf");

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entityPdfText));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/fil_txt/PUBL.id_207_fil_txt.pdf"))
			.thenReturn(new ByteArrayResource(PDF_BYTES));

		service.streamFile(207, FileVariant.TEXT, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/pdf");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"PUBL.id_207_fil_txt.pdf\"");
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileForTextVariantWithMislabelledPdfStreamsBinary() throws IOException {
		// Filename says .xml but bytes are PDF — Tika detects PDF and we stream binary
		// instead of trying (and failing) to transform with XSLT.
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/fil_txt/PUBL.id_207_fil_txt.xml"))
			.thenReturn(new ByteArrayResource(PDF_BYTES));

		service.streamFile(207, FileVariant.TEXT, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "application/pdf");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"PUBL.id_207_fil_txt.xml\"");
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileForTextVariantWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entity()));
		when(sambaIntegrationMock.openResource("/publ/fil_txt/PUBL.id_207_fil_txt.xml"))
			.thenReturn(new ByteArrayResource("<x/>".getBytes()) {
				@Override
				public InputStream getInputStream() throws IOException {
					throw new IOException("smb-down");
				}
			});

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.TEXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("smb-down");
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
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileWhenTextVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withOcrFilename("   ");

		when(publicationRepositoryMock.findById(207)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.TEXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'text'");
		verifyNoInteractions(sambaIntegrationMock);
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileWhenThumbnailVariantIsNull() {
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
		when(sambaIntegrationMock.openResource("/publ/fil_liten/PUBL.id_207_fil_liten.jpeg"))
			.thenReturn(new ByteArrayResource(JPEG_BYTES) {
				@Override
				public InputStream getInputStream() throws IOException {
					throw new IOException("boom");
				}
			});

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom");
	}
}
