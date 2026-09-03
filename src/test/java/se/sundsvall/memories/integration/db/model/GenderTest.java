package se.sundsvall.memories.integration.db.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class GenderTest {

	@Test
	void labelsAndSourceValues() {
		assertThat(Gender.MAN.getLabel()).isEqualTo("Man");
		assertThat(Gender.MAN.getSourceValues()).containsExactly("man", "1");
		assertThat(Gender.KVINNA.getLabel()).isEqualTo("Kvinna");
		assertThat(Gender.KVINNA.getSourceValues()).containsExactly("kvinna", "2");
		assertThat(Gender.OKANT.getLabel()).isEqualTo("Okänt");
		assertThat(Gender.OKANT.getSourceValues()).containsExactly("okänt");
	}

	@ParameterizedTest
	@CsvSource({
		"man, MAN",
		"MAN, MAN",
		" Man , MAN",
		"1, MAN",
		"kvinna, KVINNA",
		"2, KVINNA",
		"okänt, OKANT",
		"OKÄNT, OKANT"
	})
	void fromSource(final String stored, final Gender expected) {
		assertThat(Gender.fromSource(stored)).contains(expected);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0", "3", "1830-06-12", "", "   ", "m", "female"
	})
	void fromSourceWhenTheValueNamesNoGender(final String stored) {
		assertThat(Gender.fromSource(stored)).isEmpty();
	}

	@Test
	void fromSourceWhenNull() {
		assertThat(Gender.fromSource(null)).isEmpty();
	}

	@ParameterizedTest
	@CsvSource({
		"Man, MAN",
		"man, MAN",
		" MAN , MAN",
		"Kvinna, KVINNA",
		"kvinna, KVINNA",
		"Okänt, OKANT",
		"okänt, OKANT"
	})
	void fromLabel(final String label, final Gender expected) {
		assertThat(Gender.fromLabel(label)).contains(expected);
	}

	/**
	 * The filter speaks labels only: a stored code is not a label, and neither is anything the registers do not use.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"1", "2", "", "  ", "kvinn", "unknown"
	})
	void fromLabelWhenTheValueIsNoLabel(final String label) {
		assertThat(Gender.fromLabel(label)).isEmpty();
	}

	@Test
	void fromLabelWhenNull() {
		assertThat(Gender.fromLabel(null)).isEmpty();
	}

	@Test
	void sourceValuesAreImmutable() {
		final List<String> values = Gender.MAN.getSourceValues();

		assertThat(values).isUnmodifiable();
	}
}
