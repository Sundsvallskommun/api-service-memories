package se.sundsvall.memories.service.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.service.model.StreamPayload;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Single seam for streaming archive files from the SMB share to an HTTP response. Centralises the otherwise-duplicated
 * fetch / detect / write / error-handling logic shared by every media service:
 * <ul>
 * <li>{@link #streamInline} — sniff the MIME type with Tika and write the file inline, optionally transforming XML to
 * HTML (publications, photos, texts).</li>
 * <li>{@link #streamAttachment} — write a known-MIME file as a download attachment (film/audio file downloads).</li>
 * <li>{@link #openForPlayback} — open a Range-aware {@link StreamPayload} for inline media playback.</li>
 * </ul>
 */
@Component
public class FileStreamer {

	private final SambaIntegration sambaIntegration;
	private final FileTypeDetector fileTypeDetector;
	private final XsltTransformer xsltTransformer;

	public FileStreamer(final SambaIntegration sambaIntegration, final FileTypeDetector fileTypeDetector,
		final XsltTransformer xsltTransformer) {
		this.sambaIntegration = sambaIntegration;
		this.fileTypeDetector = fileTypeDetector;
		this.xsltTransformer = xsltTransformer;
	}

	/**
	 * Builds a human-friendly download filename of the form {@code <stem>.<extension>}, keeping only the file extension
	 * of {@code sourcePath} (an internal, non-user-facing SMB filename) and discarding its stem. Handles both {@code /}
	 * and {@code \} separators. When no extension can be derived the bare {@code stem} is returned.
	 *
	 * @param  stem       the user-facing name stem (e.g. {@code "sundsvallsminnen-2757"})
	 * @param  sourcePath the internal SMB object path or filename to take the extension from (nullable)
	 * @return            the download filename, never blank
	 */
	public static String downloadFilename(final String stem, final String sourcePath) {
		return ofNullable(sourcePath)
			.filter(path -> !path.isBlank())
			.map(path -> path.replace('\\', '/'))
			.map(path -> path.substring(path.lastIndexOf('/') + 1))
			.map(FileStreamer::extension)
			.filter(extension -> !extension.isBlank())
			.map(extension -> stem + "." + extension)
			.orElse(stem);
	}

	/**
	 * Fetches the file at {@code smbPath}, detects its type and writes it inline. When {@code transformXmlToHtml} is
	 * {@code true} and the detected content is XML, the file is XSLT-transformed to HTML; otherwise it is streamed
	 * verbatim with the detected {@code Content-Type}.
	 *
	 * @param smbPath            the SMB path to the file
	 * @param filename           internal filename used only for Tika type detection (extension hint)
	 * @param downloadFilename   user-facing filename written to the {@code Content-Disposition} header (extension swapped
	 *                           to {@code .html} when the content is transformed to HTML)
	 * @param transformXmlToHtml whether XML content should be transformed to HTML
	 * @param response           the response to write to
	 * @param errorContext       human-readable context prefixed to the 500 detail on IO failure
	 */
	public void streamInline(final String smbPath, final String filename, final String downloadFilename, final boolean transformXmlToHtml,
		final HttpServletResponse response, final String errorContext) {
		try (final var input = sambaIntegration.openResource(smbPath).getInputStream()) {
			final var detected = fileTypeDetector.detect(input, filename);

			if (transformXmlToHtml && isXmlMimeType(detected.mimeType())) {
				streamTransformedXml(downloadFilename, detected, response);
			} else {
				streamBinary(downloadFilename, detected, response);
			}
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "%s: %s".formatted(errorContext, e.getMessage()));
		}
	}

	/**
	 * Streams the file at {@code smbPath} to the response as a download attachment with the given {@code mimeType}.
	 *
	 * @param smbPath      the SMB path to the file
	 * @param mimeType     the {@code Content-Type} to set
	 * @param filename     filename used for the {@code Content-Disposition} header
	 * @param response     the response to write to
	 * @param errorContext human-readable context prefixed to the 500 detail on IO failure
	 */
	public void streamAttachment(final String smbPath, final String mimeType, final String filename,
		final HttpServletResponse response, final String errorContext) {
		response.addHeader(CONTENT_TYPE, mimeType);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(filename));
		try {
			sambaIntegration.streamFile(smbPath, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "%s: %s".formatted(errorContext, e.getMessage()));
		}
	}

	/**
	 * Opens a Range-aware {@link StreamPayload} for inline media playback. The returned resource looks up its length on
	 * demand and opens a fresh SMB stream per read, as required for {@code 206 Partial Content} responses.
	 *
	 * @param  smbPath  the SMB path to the file
	 * @param  mimeType the resource MIME type
	 * @param  filename the display filename
	 * @return          the payload (resource, mime type, filename)
	 */
	public StreamPayload openForPlayback(final String smbPath, final String mimeType, final String filename) {
		return new StreamPayload(sambaIntegration.openResource(smbPath), mimeType, filename);
	}

	private void streamBinary(final String downloadFilename, final FileTypeDetector.Detected detected, final HttpServletResponse response) throws IOException {
		response.addHeader(CONTENT_TYPE, detected.mimeType());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(downloadFilename).build().toString());
		detected.writeTo(response.getOutputStream());
	}

	private void streamTransformedXml(final String downloadFilename, final FileTypeDetector.Detected detected, final HttpServletResponse response) throws IOException {
		final var htmlFilename = swapExtension(downloadFilename, "html");
		response.addHeader(CONTENT_TYPE, new MediaType("text", "html", StandardCharsets.UTF_8).toString());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(htmlFilename).build().toString());
		xsltTransformer.transform(detected.fullStream(), response.getOutputStream());
	}

	private static boolean isXmlMimeType(final String mimeType) {
		return mimeType != null && mimeType.contains("xml");
	}

	private static String swapExtension(final String filename, final String newExtension) {
		return stem(filename) + "." + newExtension;
	}

	private static String stem(final String filename) {
		final var dot = filename.lastIndexOf('.');
		if (dot > 0) {
			return filename.substring(0, dot);
		}
		return filename;
	}

	private static String extension(final String filename) {
		final var dot = filename.lastIndexOf('.');
		if (dot > 0) {
			return filename.substring(dot + 1);
		}
		return "";
	}
}
