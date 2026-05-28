package se.sundsvall.memories.integration.db.model;

import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.TextMediaEntity.TextMediaId;

import static org.assertj.core.api.Assertions.assertThat;

class TextMediaIdTest {

	@Test
	void equalsAndHashCode() {
		final var a = new TextMediaId(1001, 1);
		final var b = new TextMediaId(1001, 1);
		final var differentSeq = new TextMediaId(1001, 2);
		final var differentText = new TextMediaId(1002, 1);

		assertThat(a)
			.isEqualTo(a)
			.isEqualTo(b)
			.hasSameHashCodeAs(b)
			.isNotEqualTo(differentSeq)
			.isNotEqualTo(differentText)
			.isNotEqualTo(null)
			.isNotEqualTo("not-an-id");
	}

	@Test
	void noArgConstructorYieldsNullComponents() {
		final var id = new TextMediaId();

		assertThat(id).isEqualTo(new TextMediaId(null, null));
	}
}
