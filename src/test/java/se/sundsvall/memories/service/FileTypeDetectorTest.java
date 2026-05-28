package se.sundsvall.memories.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class FileTypeDetectorTest {

	private static final byte[] JPEG_BYTES = new byte[] {
		(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
	};
	private static final byte[] PDF_BYTES = "%PDF-1.4\n%abc\n".getBytes();
	private static final byte[] XML_BYTES = "<?xml version=\"1.0\"?><root/>".getBytes();

	private final FileTypeDetector detector = new FileTypeDetector();

	@Test
	void detectIdentifiesJpegFromMagicBytes() {
		final var detected = detector.detect(new ByteArrayInputStream(JPEG_BYTES), "anything.bin");
		assertThat(detected.mimeType()).isEqualTo("image/jpeg");
	}

	@Test
	void detectIdentifiesPdfFromMagicBytesEvenWhenFilenameLies() {
		final var detected = detector.detect(new ByteArrayInputStream(PDF_BYTES), "PUBL.id_15381_fil_txt.xml");
		assertThat(detected.mimeType()).isEqualTo("application/pdf");
	}

	@Test
	void detectIdentifiesXmlFromContent() {
		final var detected = detector.detect(new ByteArrayInputStream(XML_BYTES), "file.xml");
		assertThat(detected.mimeType()).contains("xml");
	}

	@Test
	void detectFallsBackToOctetStreamForUnknownBytes() {
		final var detected = detector.detect(new ByteArrayInputStream(new byte[] {
			0x00, 0x00, 0x00, 0x00
		}), "mystery.q9z");
		assertThat(detected.mimeType()).isEqualTo("application/octet-stream");
	}

	@Test
	void writeToEmitsSniffPlusRemainder() throws IOException {
		final var content = "Hello, world!".getBytes();
		final var detected = detector.detect(new ByteArrayInputStream(content), "greeting.txt");
		final var out = new ByteArrayOutputStream();

		detected.writeTo(out);

		assertThat(out.toByteArray()).containsExactly(content);
	}

	@Test
	void fullStreamYieldsCompleteContent() throws IOException {
		final var content = "Hello, world!".getBytes();
		final var detected = detector.detect(new ByteArrayInputStream(content), "greeting.txt");

		try (final var full = detected.fullStream()) {
			assertThat(full.readAllBytes()).containsExactly(content);
		}
	}

	@Test
	void writeToWrapsIOExceptionAsProblem() {
		final var detected = detector.detect(new ByteArrayInputStream(JPEG_BYTES), "image.jpg");

		assertThatThrownBy(() -> detected.writeTo(new java.io.OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				throw new IOException("disk-full");
			}
		}))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", INTERNAL_SERVER_ERROR);
	}

	@Test
	void detectedEqualsHashCodeAndToString() {
		final var remainder = new ByteArrayInputStream(new byte[0]);
		final var a = new FileTypeDetector.Detected("application/pdf", new byte[] {
			1, 2, 3
		}, remainder);
		final var b = new FileTypeDetector.Detected("application/pdf", new byte[] {
			1, 2, 3
		}, remainder);
		final var differentMime = new FileTypeDetector.Detected("image/png", new byte[] {
			1, 2, 3
		}, remainder);
		final var differentSniff = new FileTypeDetector.Detected("application/pdf", new byte[] {
			9
		}, remainder);

		assertThat(a)
			.isEqualTo(a)
			.isEqualTo(b)
			.hasSameHashCodeAs(b)
			.isNotEqualTo(differentMime)
			.isNotEqualTo(differentSniff)
			.isNotEqualTo(null)
			.isNotEqualTo("not-a-detected");
		assertThat(a).hasToString("Detected{mimeType='application/pdf', sniff=[1, 2, 3], remainder=" + remainder + "}");
	}

	@Test
	void detectHandlesIoExceptionGracefully() {
		final var failingStream = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("read fail");
			}
		};

		final var detected = detector.detect(failingStream, "anything.bin");

		assertThat(detected.mimeType()).isEqualTo("application/octet-stream");
		assertThat(detected.sniff()).isEmpty();
	}
}
