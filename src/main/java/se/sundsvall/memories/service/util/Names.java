package se.sundsvall.memories.service.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import static java.util.Comparator.nullsLast;

/**
 * Ordering for the names a dropdown is shown under. The database's own collation is deliberately not relied on: the
 * legacy schema declares none, so every column inherits whatever the server happens to default to, and under a
 * {@code general_ci} collation {@code Ä} sorts as {@code A} — before {@code B} rather than after {@code Z}, which is
 * wrong for a Swedish list. The dropdown endpoints return their whole table, so they can order it here instead and be
 * right whatever the database is set to. A paged search cannot, and has to keep ordering in SQL.
 */
public final class Names {

	private static final Locale SWEDISH = Locale.of("sv", "SE");

	private Names() {}

	/**
	 * Swedish alphabetical order, missing names last.
	 *
	 * @return a comparator, built fresh on every call because {@link Collator} is not thread-safe
	 */
	public static Comparator<String> swedishOrder() {
		return nullsLast(Collator.getInstance(SWEDISH)::compare);
	}
}
