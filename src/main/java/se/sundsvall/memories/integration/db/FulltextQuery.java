package se.sundsvall.memories.integration.db;

import java.util.regex.Pattern;

/**
 * Helper for turning a user-supplied query string into a safe MariaDB fulltext
 * {@code IN BOOLEAN MODE} expression.
 * <p>
 * Fulltext operator characters ({@code + - " ( ) ~ < > * @ :}) are stripped so
 * the user cannot unintentionally (or intentionally) alter the query semantics
 * via injection. A trailing {@code *} wildcard is appended to each remaining
 * token so prefix matches work the same way as the archive's own search UI.
 */
public final class FulltextQuery {

	private static final Pattern OPERATOR_CHARS = Pattern.compile("[+\\-\"()~<>*@:]");
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");

	private FulltextQuery() {}

	/**
	 * Sanitize a free-text query for use with {@code MATCH(...) AGAINST(? IN BOOLEAN MODE)}.
	 * Returns {@code null} when the input produces no searchable tokens so callers can fall
	 * back to the "list all" path.
	 */
	public static String sanitize(final String query) {
		if (query == null) {
			return null;
		}

		final var stripped = OPERATOR_CHARS.matcher(query).replaceAll(" ").trim();
		if (stripped.isEmpty()) {
			return null;
		}

		final var tokens = WHITESPACE.split(stripped);
		final var builder = new StringBuilder();
		for (final var token : tokens) {
			if (token.isEmpty()) {
				continue;
			}
			if (!builder.isEmpty()) {
				builder.append(' ');
			}
			builder.append(token).append('*');
		}

		return builder.isEmpty() ? null : builder.toString();
	}
}
