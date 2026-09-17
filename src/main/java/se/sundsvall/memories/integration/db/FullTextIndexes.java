package se.sundsvall.memories.integration.db;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Which {@code FULLTEXT} indexes the database actually has, read once at startup.
 * <p>
 * {@code MATCH} accepts only the exact column list of an existing index: a list that names a column too few, or a
 * table that never got the index, is answered with error 1191 rather than with a slower scan. The migrations in this
 * repository build the Testcontainers database, not the deployed one — the legacy tables there were created by the
 * museum system years ago, and this PR is its own evidence that the lists drift, since {@code TBL_NODES} needed a new
 * index because the existing one covered {@code DESCRIPTION} alone. Without this check a search whose index differs in
 * production would fail outright instead of running slowly.
 * <p>
 * A search whose index is missing falls back to {@code LIKE}, and so does every search if the probe itself fails: slow
 * and correct beats fast and 500. The result is held statically because the specifications are interfaces with a
 * static builder rather than Spring beans.
 */
@Component
public class FullTextIndexes {

	private static final Logger LOGGER = LoggerFactory.getLogger(FullTextIndexes.class);

	/**
	 * Every fulltext index in the current schema as {@code table:col,col}, lower-cased, columns sorted — {@code MATCH}
	 * pairs with an index by column set, not by the order they were declared in.
	 */
	private static final AtomicReference<Set<String>> AVAILABLE = new AtomicReference<>(Set.of());

	private static final String QUERY = """
		SELECT LOWER(TABLE_NAME), LOWER(COLUMN_NAME), INDEX_NAME
		FROM information_schema.STATISTICS
		WHERE TABLE_SCHEMA = DATABASE() AND INDEX_TYPE = 'FULLTEXT'
		""";

	private final EntityManager entityManager;

	public FullTextIndexes(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/** Whether {@code MATCH} over exactly these columns of this table will resolve to an index. */
	public static boolean covers(final String table, final List<String> columns) {
		return AVAILABLE.get().contains(key(table, columns));
	}

	static void set(final Set<String> available) {
		AVAILABLE.set(available);
	}

	private static String key(final String table, final List<String> columns) {
		return table.toLowerCase(Locale.ROOT) + ":" + columns.stream()
			.map(column -> column.toLowerCase(Locale.ROOT))
			.sorted()
			.reduce((left, right) -> left + "," + right)
			.orElse("");
	}

	@PostConstruct
	@Transactional(readOnly = true)
	void read() {
		try {
			@SuppressWarnings("unchecked")
			final List<Object[]> rows = entityManager.createNativeQuery(QUERY).getResultList();

			final var byIndex = new java.util.HashMap<String, java.util.List<String>>();
			rows.forEach(row -> byIndex
				.computeIfAbsent(row[0] + "/" + row[2], _ -> new java.util.ArrayList<>())
				.add((String) row[1]));

			final var available = byIndex.entrySet().stream()
				.map(entry -> key(entry.getKey().substring(0, entry.getKey().indexOf('/')), entry.getValue()))
				.collect(java.util.stream.Collectors.toUnmodifiableSet());

			set(available);
			LOGGER.info("Fulltext indexes available for MATCH: {}", available);
		} catch (final Exception e) {
			// Leaving the set empty makes every search use LIKE, which is what this service did before the switch.
			set(Set.of());
			LOGGER.warn("Could not read the fulltext indexes; every search will use LIKE instead of MATCH", e);
		}
	}
}
