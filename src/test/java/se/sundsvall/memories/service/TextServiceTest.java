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
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.TextMediaRepository;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.TextService.FileVariant;

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
class TextServiceTest {

	private static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/", "/text/");

	// JPEG SOI marker — Tika identifies it as image/jpeg
	private static final byte[] JPEG_BYTES = new byte[] {
		(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
	};

	@Mock
	private TextRepository textRepositoryMock;

	@Mock
	private TextMediaRepository textMediaRepositoryMock;

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private XsltTransformer xsltTransformerMock;

	private TextService service;

	private static TextEntity entity() {
		return TextEntity.create()
			.withTextId(1001)
			.withDocumentTitle("Minne från Sundsvall")
			.withTopographyId(4)
			.withThumbnailFilename("TEXT.id_1001_fil_liten.jpeg")
			.withLargeImageFilename("TEXT.id_1001_fil_stor.jpeg")
			.withOcrFilename("TEXT.id_1001_fil_txt.xml")
			.withXmltext("<text>content</text>")
			.withOptions(4);
	}

	static Stream<Arguments> binaryFileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "TEXT.id_1001_fil_liten.jpeg", "fil_liten"),
			Arguments.of(FileVariant.LARGE, "TEXT.id_1001_fil_stor.jpeg", "fil_stor"));
	}

	@BeforeEach
	void setUp() {
		service = new TextService(textRepositoryMock, textMediaRepositoryMock, sambaIntegrationMock, SAMBA_PROPERTIES,
			topographyLookupMock, xsltTransformerMock, new FileTypeDetector());
	}

	@Test
	void searchWithQueryUsesFulltextRepository() {
		final var pageable = PageRequest.of(0, 100);
		when(textRepositoryMock.searchPublished("stadshuset*", pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(TextParameters.create().withQuery("stadshuset"));

		assertThat(result.getTexts()).hasSize(1);
		assertThat(result.getTexts().getFirst().getDocumentTitle()).isEqualTo("Minne från Sundsvall");
		assertThat(result.getTexts().getFirst().getXmltext()).isNull();
		assertThat(result.getTexts().getFirst().getMediaFiles()).isNull();
		verify(textRepositoryMock).searchPublished("stadshuset*", pageable);
		verifyNoMoreInteractions(textRepositoryMock);
	}

	@Test
	void searchWithNullQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(textRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(TextParameters.create());

		assertThat(result.getTexts()).hasSize(1);
		verify(textRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(textRepositoryMock);
	}

	@Test
	void searchWithBlankQueryUsesFindAllPublished() {
		final var pageable = PageRequest.of(0, 100);
		when(textRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(TextParameters.create().withQuery("   "));

		assertThat(result.getTexts()).isEmpty();
		verify(textRepositoryMock).findAllPublished(pageable);
		verifyNoMoreInteractions(textRepositoryMock);
	}

	@Test
	void searchWithOperatorOnlyQueryFallsBackToFindAll() {
		final var pageable = PageRequest.of(0, 100);
		when(textRepositoryMock.findAllPublished(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(TextParameters.create().withQuery("+-*"));

		assertThat(result.getTexts()).isEmpty();
		verify(textRepositoryMock).findAllPublished(pageable);
	}

	@Test
	void getByIdIncludesXmltextAndMediaFiles() {
		when(textRepositoryMock.findById(1001)).thenReturn(Optional.of(entity()));
		when(textMediaRepositoryMock.findByTextIdOrderById(1001)).thenReturn(List.of(
			TextMediaEntity.create().withTextId(1001).withThumbnailFilename("extra-liten.jpg")));
		when(topographyLookupMock.resolve(4)).thenReturn("Sundsvalls kommun");

		final var result = service.getById(1001);

		assertThat(result).isNotNull();
		assertThat(result.getTextId()).isEqualTo(1001);
		assertThat(result.getXmltext()).isEqualTo("<text>content</text>");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getMediaFiles()).hasSize(1);
		assertThat(result.getMediaFiles().getFirst().getThumbnailFilename()).isEqualTo("extra-liten.jpg");
	}

	@Test
	void getByIdNotFound() {
		when(textRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Text with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("binaryFileVariants")
	void streamFileForBinaryVariant(final FileVariant variant, final String expectedFilename, final String expectedSubfolder) throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(textRepositoryMock.findById(1001)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/text/" + expectedSubfolder + "/" + expectedFilename))
			.thenReturn(new ByteArrayResource(JPEG_BYTES));

		service.streamFile(1001, variant, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "image/jpeg");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"%s\"".formatted(expectedFilename));
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileForTextVariantTransformsXmlToHtml() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(textRepositoryMock.findById(1001)).thenReturn(Optional.of(entity()));
		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/text/fil_txt/TEXT.id_1001_fil_txt.xml"))
			.thenReturn(new ByteArrayResource("<?xml version=\"1.0\"?><Document><Title>Hi</Title></Document>".getBytes()));
		doAnswer(invocation -> {
			final InputStream in = invocation.getArgument(0);
			final OutputStream out = invocation.getArgument(1);
			out.write(("<html>" + new String(in.readAllBytes()) + "</html>").getBytes());
			return null;
		}).when(xsltTransformerMock).transform(any(InputStream.class), any(OutputStream.class));

		service.streamFile(1001, FileVariant.TEXT, responseMock);

		verify(responseMock).addHeader(CONTENT_TYPE, "text/html;charset=UTF-8");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"TEXT.id_1001_fil_txt.html\"");
		verify(xsltTransformerMock).transform(any(InputStream.class), any(OutputStream.class));
		verify(sambaIntegrationMock).openResource("/text/fil_txt/TEXT.id_1001_fil_txt.xml");
	}

	@Test
	void streamFileNotFound() {
		final var responseMock = mock(HttpServletResponse.class);

		when(textRepositoryMock.findById(999)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Text with id '999' not found");
		verifyNoInteractions(sambaIntegrationMock);
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withOcrFilename("   ");

		when(textRepositoryMock.findById(1001)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1001, FileVariant.TEXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'text'");
		verifyNoInteractions(sambaIntegrationMock);
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withThumbnailFilename(null);

		when(textRepositoryMock.findById(1001)).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1001, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'thumbnail'");
	}

	@Test
	void streamFileWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);

		when(textRepositoryMock.findById(1001)).thenReturn(Optional.of(entity()));
		when(sambaIntegrationMock.openResource("/text/fil_liten/TEXT.id_1001_fil_liten.jpeg"))
			.thenReturn(new ByteArrayResource(JPEG_BYTES) {
				@Override
				public InputStream getInputStream() throws IOException {
					throw new IOException("boom");
				}
			});

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1001, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom");
	}
}
