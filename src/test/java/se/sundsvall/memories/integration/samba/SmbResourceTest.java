package se.sundsvall.memories.integration.samba;

import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.samba.SmbResource.LengthSupplier;
import se.sundsvall.memories.integration.samba.SmbResource.StreamOpener;

import static org.assertj.core.api.Assertions.assertThat;

class SmbResourceTest {

	private static final LengthSupplier FIXED_LENGTH = _ -> 1234L;
	private static final StreamOpener EMPTY_OPENER = _ -> new ByteArrayInputStream(new byte[0]);

	@Test
	void contentLengthDelegatesToSupplier() {
		final var resource = new SmbResource("/a.mp3", EMPTY_OPENER, FIXED_LENGTH);

		assertThat(resource.contentLength()).isEqualTo(1234L);
	}

	@Test
	void filenameIsBasename() {
		final var forwardSlash = new SmbResource("/folder/a.mp3", EMPTY_OPENER, FIXED_LENGTH);
		final var backslash = new SmbResource("\\\\server\\share\\folder\\b.mp3", EMPTY_OPENER, FIXED_LENGTH);
		final var noSeparator = new SmbResource("c.mp3", EMPTY_OPENER, FIXED_LENGTH);

		assertThat(forwardSlash.getFilename()).isEqualTo("a.mp3");
		assertThat(backslash.getFilename()).isEqualTo("b.mp3");
		assertThat(noSeparator.getFilename()).isEqualTo("c.mp3");
	}

	@Test
	void isReadableAndNotOpen() {
		final var resource = new SmbResource("/a.mp3", EMPTY_OPENER, FIXED_LENGTH);

		assertThat(resource.exists()).isTrue();
		assertThat(resource.isReadable()).isTrue();
		assertThat(resource.isOpen()).isFalse();
		assertThat(resource.getDescription()).contains("/a.mp3");
	}

	@Test
	void equalsAndHashCodeKeyedOnFilePath() {
		final var a = new SmbResource("/a.mp3", EMPTY_OPENER, FIXED_LENGTH);
		final var sameA = new SmbResource("/a.mp3", _ -> new ByteArrayInputStream(new byte[] {
			1
		}), _ -> 9L);
		final var b = new SmbResource("/b.mp3", EMPTY_OPENER, FIXED_LENGTH);

		assertThat(a)
			.isEqualTo(a)
			.isEqualTo(sameA)
			.isNotEqualTo(b)
			.isNotEqualTo("/a.mp3")
			.isNotEqualTo(null)
			.hasSameHashCodeAs(sameA);
		assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
	}

	@Test
	void getInputStreamDelegatesToOpener() throws Exception {
		final var calls = new AtomicInteger();
		final StreamOpener opener = path -> {
			calls.incrementAndGet();
			return new ByteArrayInputStream(new byte[0]);
		};
		final var resource = new SmbResource("/a.mp3", opener, FIXED_LENGTH);

		try (final var _ = resource.getInputStream()) {
			// first
		}
		try (final var _ = resource.getInputStream()) {
			// second — each call goes to opener, no caching
		}

		assertThat(calls.get()).isEqualTo(2);
	}
}
