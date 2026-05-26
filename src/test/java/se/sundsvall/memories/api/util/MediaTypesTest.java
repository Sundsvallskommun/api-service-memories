package se.sundsvall.memories.api.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTypesTest {

	@Test
	void resolveJpeg() {
		assertThat(MediaTypes.resolve("photo.jpg").toString()).isEqualTo("image/jpeg");
		assertThat(MediaTypes.resolve("photo.jpeg").toString()).isEqualTo("image/jpeg");
	}

	@Test
	void resolvePng() {
		assertThat(MediaTypes.resolve("logo.png").toString()).isEqualTo("image/png");
	}

	@Test
	void resolvePdf() {
		assertThat(MediaTypes.resolve("book.pdf").toString()).isEqualTo("application/pdf");
	}

	@Test
	void resolveUnknownFallsBackToOctetStream() {
		assertThat(MediaTypes.resolve("mystery.q9z").toString()).isEqualTo("application/octet-stream");
	}

	@Test
	void resolveNoExtensionFallsBackToOctetStream() {
		assertThat(MediaTypes.resolve("no-extension").toString()).isEqualTo("application/octet-stream");
	}
}
