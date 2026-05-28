package se.sundsvall.memories.service.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Objects;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * Sniffs the leading bytes of an input stream to detect its MIME type via Apache Tika.
 *
 * Tika reads magic bytes from the head of the stream and uses the filename as a tiebreaker. This is more robust than
 * filename-extension lookup alone — files with misleading or missing extensions still get a correct
 * {@code Content-Type}.
 *
 * The sniff is destructive on the underlying stream, so the detector buffers the sniffed bytes and returns them
 * alongside the remainder as a {@link Detected} record. Callers either write the sniffed bytes followed by the
 * remainder, or use
 * {@link Detected#fullStream()} to get a single stream over both.
 */
@Component
public class FileTypeDetector {

	static final int SNIFF_BYTES = 8192;

	// Tika is thread-safe and stateless, so a single shared instance is safe and avoids
	// per-request setup cost.
	private static final Tika TIKA = new Tika();

	public Detected detect(final InputStream stream, final String filename) {
		try {
			final var sniff = stream.readNBytes(SNIFF_BYTES);
			final var mimeType = TIKA.detect(new ByteArrayInputStream(sniff), filename);
			return new Detected(mimeType, sniff, stream);
		} catch (final IOException _) {
			return new Detected(APPLICATION_OCTET_STREAM_VALUE, new byte[0], stream);
		}
	}

	public record Detected(String mimeType, byte[] sniff, InputStream remainder) {

		public InputStream fullStream() {
			return new SequenceInputStream(new ByteArrayInputStream(sniff), remainder);
		}

		public void writeTo(final OutputStream out) {
			try {
				out.write(sniff);
				remainder.transferTo(out);
			} catch (final IOException e) {
				throw Problem.valueOf(INTERNAL_SERVER_ERROR,
					"IOException while writing stream: %s".formatted(e.getMessage()));
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null || getClass() != o.getClass())
				return false;
			final Detected detected = (Detected) o;
			return Objects.deepEquals(sniff, detected.sniff) && Objects.equals(mimeType, detected.mimeType) && Objects.equals(remainder, detected.remainder);
		}

		@Override
		public int hashCode() {
			return Objects.hash(mimeType, Arrays.hashCode(sniff), remainder);
		}

		@Override
		public String toString() {
			return "Detected{" +
				"mimeType='" + mimeType + '\'' +
				", sniff=" + Arrays.toString(sniff) +
				", remainder=" + remainder +
				'}';
		}
	}
}
