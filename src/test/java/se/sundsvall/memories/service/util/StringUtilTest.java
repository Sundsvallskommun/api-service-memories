package se.sundsvall.memories.service.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.memories.service.util.StringUtil.trimToNull;

class StringUtilTest {

	@Test
	void trimToNullReturnsNullForNull() {
		assertThat(trimToNull(null)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"", " ", "   ", "\t", "\n", " \t\n "
	})
	void trimToNullReturnsNullForBlank(final String value) {
		assertThat(trimToNull(value)).isNull();
	}

	@Test
	void trimToNullTrimsSurroundingWhitespace() {
		assertThat(trimToNull("  Nordin  ")).isEqualTo("Nordin");
		assertThat(trimToNull("\tNordin\n")).isEqualTo("Nordin");
	}

	@Test
	void trimToNullKeepsInnerWhitespace() {
		assertThat(trimToNull("  Anton Nordin  ")).isEqualTo("Anton Nordin");
	}

	@Test
	void trimToNullReturnsValueUnchangedWhenAlreadyTrimmed() {
		assertThat(trimToNull("Nordin")).isEqualTo("Nordin");
	}
}
