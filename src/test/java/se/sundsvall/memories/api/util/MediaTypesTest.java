package se.sundsvall.memories.api.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTypesTest {

	@Test
	void resolveJpeg() {
		assertThat(MediaTypes.resolve("photo.jpg")).hasToString("image/jpeg");
		assertThat(MediaTypes.resolve("photo.jpeg")).hasToString("image/jpeg");
	}

	@Test
	void resolvePng() {
		assertThat(MediaTypes.resolve("logo.png")).hasToString("image/png");
	}

	@Test
	void resolvePdf() {
		assertThat(MediaTypes.resolve("book.pdf")).hasToString("application/pdf");
	}

	@Test
	void resolveUnknownFallsBackToOctetStream() {
		assertThat(MediaTypes.resolve("mystery.q9z")).hasToString("application/octet-stream");
	}

	@Test
	void resolveNoExtensionFallsBackToOctetStream() {
		assertThat(MediaTypes.resolve("no-extension")).hasToString("application/octet-stream");
	}
}
