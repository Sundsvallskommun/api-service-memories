package se.sundsvall.memories.service.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

	@Test
	void trimToNullWithNull() {
		assertThat(StringUtil.trimToNull(null)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"", " ", "   ", "\t", "\n"
	})
	void trimToNullWithBlank(final String value) {
		assertThat(StringUtil.trimToNull(value)).isNull();
	}

	@Test
	void trimToNullWithPaddedValue() {
		assertThat(StringUtil.trimToNull("  Nordin  ")).isEqualTo("Nordin");
	}

	@Test
	void trimToNullWithValue() {
		assertThat(StringUtil.trimToNull("Nordin")).isEqualTo("Nordin");
	}
}
