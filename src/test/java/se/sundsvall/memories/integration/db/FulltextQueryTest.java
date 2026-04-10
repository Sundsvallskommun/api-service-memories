package se.sundsvall.memories.integration.db;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FulltextQueryTest {

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {
		"   ", "+-\"()~<>*", "  + - * "
	})
	void sanitizeReturnsNullForEmptyOrOperatorOnlyInput(final String input) {
		assertThat(FulltextQuery.sanitize(input)).isNull();
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
		"Drunkningsolycka | Drunkningsolycka*",
		"popul              | popul*",
		"  trimmed          | trimmed*",
		"hello world        | hello* world*",
		"+foo -bar          | foo* bar*",
		"\"quoted phrase\"    | quoted* phrase*",
		"midsommar 1985     | midsommar* 1985*"
	})
	void sanitizeAppendsWildcardAndStripsOperators(final String input, final String expected) {
		assertThat(FulltextQuery.sanitize(input)).isEqualTo(expected);
	}
}
