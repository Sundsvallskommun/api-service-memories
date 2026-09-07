package se.sundsvall.memories.integration.db.model;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.function.Predicate.not;

/**
 * The canonical gender labels, and the spellings the delivered registers store for each. PERSON writes the words;
 * MANTAL mixes the words with ISO 5218 codes ({@code 1} = Man, {@code 2} = Kvinna, confirmed by the source owner); and
 * both carry stray values (blank, {@code 0}, {@code 3}, birth dates from a shifted column) that name no gender at all.
 * <p>
 * {@link #OKANT} is where everything else lands: a stray, blank or missing value is a gender the archive does not
 * know, which is what the label says, so no register row is left without one. Only the types that record no gender at
 * all — every object type, and the seamen — have none.
 * <p>
 * The mapping mirrors the {@code CASE} in {@code VW_MEMORY_OBJECTS} (V2_3), so the per-type register endpoints and the
 * combined search label a row the same way and accept the same filter values.
 */
public enum Gender {

	MAN("Man", "man", "1"),
	KVINNA("Kvinna", "kvinna", "2"),
	OKANT("Okänt", "okänt");

	private final String label;
	private final List<String> sourceValues;

	Gender(final String label, final String... sourceValues) {
		this.label = label;
		this.sourceValues = List.of(sourceValues);
	}

	/**
	 * @return the label the API emits and the gender filters accept
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the lower-cased spellings the registers store for this gender
	 */
	public List<String> getSourceValues() {
		return sourceValues;
	}

	/**
	 * The spellings that name some gender other than this one. A row carrying none of them names no gender of its own,
	 * which is what {@link #OKANT} covers — stray, blank and missing values included — so this is the set the unknown
	 * gender is filtered against.
	 *
	 * @return the lower-cased spellings the registers store for every other gender
	 */
	public List<String> getOtherSourceValues() {
		return Arrays.stream(values())
			.filter(not(this::equals))
			.flatMap(gender -> gender.sourceValues.stream())
			.toList();
	}

	/**
	 * Labels a stored value the way {@code VW_MEMORY_OBJECTS} does: the spellings that name a gender resolve to it,
	 * and everything else falls to {@link #OKANT}. Every register row therefore has a gender, which is what lets a
	 * missing one mean "this type records none".
	 *
	 * @param  value the stored value
	 * @return       the gender the value names, never {@code null}
	 */
	public static Gender ofSource(final String value) {
		return fromSource(value).orElse(OKANT);
	}

	/**
	 * Resolves a value as a register stores it, whatever its casing. Prefer {@link #ofSource(String)} where a stray
	 * value should read as unknown rather than as nothing.
	 *
	 * @param  value the stored value
	 * @return       the gender it names, or empty for a blank or stray value
	 */
	public static Optional<Gender> fromSource(final String value) {
		return normalize(value)
			.flatMap(normalized -> Arrays.stream(values())
				.filter(gender -> gender.sourceValues.contains(normalized))
				.findFirst());
	}

	/**
	 * Resolves a label as a client spells it in a gender filter, case-insensitively.
	 *
	 * @param  label the filter value
	 * @return       the gender it names, or empty for a blank or unknown label
	 */
	public static Optional<Gender> fromLabel(final String label) {
		return normalize(label)
			.flatMap(normalized -> Arrays.stream(values())
				.filter(gender -> gender.label.toLowerCase(Locale.ROOT).equals(normalized))
				.findFirst());
	}

	private static Optional<String> normalize(final String value) {
		return Optional.ofNullable(value)
			.map(String::trim)
			.filter(not(String::isEmpty))
			.map(trimmed -> trimmed.toLowerCase(Locale.ROOT));
	}
}
