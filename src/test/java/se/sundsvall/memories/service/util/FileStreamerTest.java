package se.sundsvall.memories.service.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.core.io.Resource;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.service.util.FileStreamer.MaterialType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.AUDIO;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.FILM;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.PHOTO;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.PUBLICATION;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.TEXT;

@ExtendWith(MockitoExtension.class)
class FileStreamerTest {

	// JPEG SOI marker — Tika identifies it as image/jpeg
	private static final byte[] JPEG_BYTES = new byte[] {
		(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
	};

	// %PDF-1.4 — Tika identifies it as application/pdf
	private static final byte[] PDF_BYTES = "%PDF-1.4\n%abc\n".getBytes();

	private static final String XML = "<?xml version=\"1.0\"?><Document><Title>Hi</Title></Document>";

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@Mock
	private XsltTransformer xsltTransformerMock;

	private FileStreamer fileStreamer;

	@BeforeEach
	void setUp() {
		// Real FileTypeDetector so the Tika detection branches are genuinely exercised.
		fileStreamer = new FileStreamer(sambaIntegrationMock, new FileTypeDetector(), xsltTransformerMock);
	}

	@Test
	void streamInlineWritesBinaryWithDetectedType() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/fil_liten/img.jpeg")).thenReturn(new ByteArrayResource(JPEG_BYTES));

		fileStreamer.streamInline("/publ/fil_liten/img.jpeg", "img.jpeg", "sundsvallsminnen-publikation-42.jpeg", false, responseMock, "ctx");

		verify(responseMock).addHeader(CONTENT_TYPE, "image/jpeg");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"sundsvallsminnen-publikation-42.jpeg\"");
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamInlineFallsBackToOctetStreamForUnknownBytes() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/foto/fil_liten/odd.q9z")).thenReturn(new ByteArrayResource(new byte[] {
			0x00, 0x00, 0x00, 0x00
		}));

		fileStreamer.streamInline("/foto/fil_liten/odd.q9z", "odd.q9z", "sundsvallsminnen-foto-7.q9z", false, responseMock, "ctx");

		verify(responseMock).addHeader(CONTENT_TYPE, "application/octet-stream");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"sundsvallsminnen-foto-7.q9z\"");
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamInlineTransformsXmlToHtmlWhenRequested() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/fil_txt/doc.xml")).thenReturn(new ByteArrayResource(XML.getBytes()));
		doAnswer(invocation -> {
			final InputStream in = invocation.getArgument(0);
			final OutputStream out = invocation.getArgument(1);
			out.write(("<html>" + new String(in.readAllBytes()) + "</html>").getBytes());
			return null;
		}).when(xsltTransformerMock).transform(any(InputStream.class), any(OutputStream.class));

		fileStreamer.streamInline("/publ/fil_txt/doc.xml", "doc.xml", "sundsvallsminnen-publikation-9.xml", true, responseMock, "ctx");

		verify(responseMock).addHeader(CONTENT_TYPE, "text/html;charset=UTF-8");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"sundsvallsminnen-publikation-9.html\"");
		verify(xsltTransformerMock).transform(any(InputStream.class), any(OutputStream.class));
	}

	@Test
	void streamInlineStreamsBinaryWhenTransformRequestedButContentNotXml() throws IOException {
		// Filename says .xml but the bytes are a PDF — Tika wins, so we stream binary instead of transforming.
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/publ/fil_txt/doc.xml")).thenReturn(new ByteArrayResource(PDF_BYTES));

		fileStreamer.streamInline("/publ/fil_txt/doc.xml", "doc.xml", "sundsvallsminnen-publikation-11.xml", true, responseMock, "ctx");

		verify(responseMock).addHeader(CONTENT_TYPE, "application/pdf");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"sundsvallsminnen-publikation-11.xml\"");
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamInlineDoesNotTransformXmlWhenNotRequested() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		when(sambaIntegrationMock.openResource("/foto/fil_liten/doc.xml")).thenReturn(new ByteArrayResource(XML.getBytes()));

		fileStreamer.streamInline("/foto/fil_liten/doc.xml", "doc.xml", "sundsvallsminnen-foto-13.xml", false, responseMock, "ctx");

		verify(responseMock).addHeader(CONTENT_TYPE, "application/xml");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "inline; filename=\"sundsvallsminnen-foto-13.xml\"");
		verifyNoInteractions(xsltTransformerMock);
	}

	@Test
	void streamInlineWrapsIOException() {
		final var responseMock = mock(HttpServletResponse.class);

		when(sambaIntegrationMock.openResource("/publ/fil_liten/img.jpeg"))
			.thenReturn(new ByteArrayResource(JPEG_BYTES) {
				@Override
				public InputStream getInputStream() throws IOException {
					throw new IOException("smb-down");
				}
			});

		final var exception = assertThrows(ThrowableProblem.class,
			() -> fileStreamer.streamInline("/publ/fil_liten/img.jpeg", "img.jpeg", "sundsvallsminnen-publikation-42.jpeg", false, responseMock, "boom-context"));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom-context").contains("smb-down");
	}

	@Test
	void streamAttachmentWritesAttachmentHeadersAndStreams() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

		fileStreamer.streamAttachment("/film/movie.mp4", "video/mp4", "movie.mp4", responseMock, "ctx");

		verify(responseMock).addHeader(CONTENT_TYPE, "video/mp4");
		verify(responseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"movie.mp4\"");
		verify(sambaIntegrationMock).streamFile("/film/movie.mp4", outputStreamMock);
	}

	@Test
	void streamAttachmentWrapsIOException() throws IOException {
		final var responseMock = mock(HttpServletResponse.class);
		final var outputStreamMock = mock(ServletOutputStream.class);

		when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
		doAnswer(invocation -> {
			throw new IOException("smb-down");
		}).when(sambaIntegrationMock).streamFile("/film/movie.mp4", outputStreamMock);

		final var exception = assertThrows(ThrowableProblem.class,
			() -> fileStreamer.streamAttachment("/film/movie.mp4", "video/mp4", "movie.mp4", responseMock, "boom-context"));

		assertThat(exception.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).contains("boom-context").contains("smb-down");
	}

	@Test
	void openForPlaybackReturnsPayload() {
		final var resourceMock = mock(Resource.class);
		when(sambaIntegrationMock.openResource("/film/movie.mp4")).thenReturn(resourceMock);

		final var payload = fileStreamer.openForPlayback("/film/movie.mp4", "video/mp4", "movie.mp4");

		assertThat(payload.resource()).isSameAs(resourceMock);
		assertThat(payload.mimeType()).isEqualTo("video/mp4");
		assertThat(payload.filename()).isEqualTo("movie.mp4");
	}

	@Test
	void downloadFilenameKeepsExtensionFromForwardSlashPath() {
		assertThat(FileStreamer.downloadFilename(FILM, 1, "/media/film/movie.mp4")).isEqualTo("sundsvallsminnen-film-1.mp4");
	}

	@Test
	void downloadFilenameKeepsExtensionFromBackslashPath() {
		assertThat(FileStreamer.downloadFilename(AUDIO, 2, "\\\\server\\share\\clip.avi")).isEqualTo("sundsvallsminnen-ljud-2.avi");
	}

	@Test
	void downloadFilenameHandlesBareFilename() {
		assertThat(FileStreamer.downloadFilename(PHOTO, 3, "movie.mp4")).isEqualTo("sundsvallsminnen-foto-3.mp4");
	}

	@Test
	void downloadFilenameWithoutExtensionReturnsBareStem() {
		assertThat(FileStreamer.downloadFilename(PUBLICATION, 4, "/media/film/movie")).isEqualTo("sundsvallsminnen-publikation-4");
	}

	@Test
	void downloadFilenameBlankOrNullSourceReturnsBareStem() {
		assertThat(FileStreamer.downloadFilename(TEXT, 5, "   ")).isEqualTo("sundsvallsminnen-text-5");
		assertThat(FileStreamer.downloadFilename(TEXT, 6, (String) null)).isEqualTo("sundsvallsminnen-text-6");
	}

	@Test
	void downloadFilenameTreatsLeadingDotAsNoExtension() {
		// dot at index 0 (".hidden") is not an extension separator — bare stem is returned
		assertThat(FileStreamer.downloadFilename(PHOTO, 7, "/media/.hidden")).isEqualTo("sundsvallsminnen-foto-7");
	}

	@ParameterizedTest
	@MethodSource("materialTypeSegments")
	void downloadFilenameUsesMaterialTypeSegment(final MaterialType materialType, final String expected) {
		assertThat(FileStreamer.downloadFilename(materialType, 207, "/archive/FILE.id_207.jpeg")).isEqualTo(expected);
	}

	static Stream<Arguments> materialTypeSegments() {
		return Stream.of(
			Arguments.of(PHOTO, "sundsvallsminnen-foto-207.jpeg"),
			Arguments.of(FILM, "sundsvallsminnen-film-207.jpeg"),
			Arguments.of(AUDIO, "sundsvallsminnen-ljud-207.jpeg"),
			Arguments.of(TEXT, "sundsvallsminnen-text-207.jpeg"),
			Arguments.of(PUBLICATION, "sundsvallsminnen-publikation-207.jpeg"));
	}

	@Test
	void downloadFilenameForMediaFileIncludesBothIds() {
		assertThat(FileStreamer.downloadFilename(TEXT, 1001, 1, "/text_multi/fil_stor/TEXT.id_1001.multi_1.fil_stor.jpeg"))
			.isEqualTo("sundsvallsminnen-text-1001-1.jpeg");
	}

	@Test
	void downloadFilenameForMediaFileWithoutExtensionReturnsBareStem() {
		assertThat(FileStreamer.downloadFilename(TEXT, 1001, 2, null)).isEqualTo("sundsvallsminnen-text-1001-2");
	}
}
