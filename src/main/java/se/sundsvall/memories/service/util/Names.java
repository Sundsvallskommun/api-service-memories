package se.sundsvall.memories.service.util;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Comparator.comparing;
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
	 * Swedish alphabetical order, missing names last. Compares precomputed {@link CollationKey}s rather than collating
	 * on every comparison: a list of n names is collated n times instead of the n log n a sort would otherwise ask for,
	 * which is what the place list, at a couple of thousand entries, actually feels.
	 *
	 * @return a comparator, built fresh on every call — neither the {@link Collator} nor the key cache is thread-safe,
	 *         so each belongs to the one sort it was built for
	 */
	public static Comparator<String> swedishOrder() {
		final var collator = Collator.getInstance(SWEDISH);
		final Map<String, CollationKey> keys = new HashMap<>();
		return nullsLast(comparing((final String name) -> keys.computeIfAbsent(name, collator::getCollationKey)));
	}
}
