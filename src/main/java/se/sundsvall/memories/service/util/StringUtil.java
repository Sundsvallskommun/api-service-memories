package se.sundsvall.memories.service.util;

import static java.util.Optional.ofNullable;

/**
 * Small string helpers shared by the search services.
 */
public final class StringUtil {

	private StringUtil() {}

	/**
	 * Trims the value and converts blank input to {@code null}, so that an empty request parameter means "no filter"
	 * rather than an empty-string match.
	 *
	 * @param  value the value to normalise (nullable)
	 * @return       the trimmed value, or {@code null} when it is null or blank
	 */
	public static String trimToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(trimmed -> !trimmed.isEmpty())
			.orElse(null);
	}
}
