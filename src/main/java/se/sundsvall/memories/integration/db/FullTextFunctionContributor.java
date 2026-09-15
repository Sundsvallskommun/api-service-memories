package se.sundsvall.memories.integration.db;

import java.util.stream.IntStream;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

import static java.util.stream.Collectors.joining;

/**
 * Teaches Hibernate MariaDB's {@code MATCH ... AGAINST}, which has no JPQL equivalent and so cannot be expressed
 * through the criteria API on its own.
 * <p>
 * One function is registered per column count because {@code MATCH} takes its columns as a list rather than as a
 * single argument, and a pattern function has a fixed arity. The last argument is always the search expression, so
 * {@code memories_fulltext_match_2(a, b, expression)} renders as
 * {@code match (a, b) against (expression in boolean mode)}.
 * <p>
 * Boolean mode is what the legacy searches use, and it is the only mode that supports the trailing {@code *} the
 * callers rely on for prefix matching. It also returns a relevance of 1 rather than a real score, which is why callers
 * compare against zero rather than ordering by the result.
 * <p>
 * Registered through {@code META-INF/services/org.hibernate.boot.model.FunctionContributor}.
 */
public class FullTextFunctionContributor implements FunctionContributor {

	/** Suffixed with the number of columns being matched. */
	public static final String FUNCTION_PREFIX = "memories_fulltext_match_";

	/** The widest {@code FULLTEXT} index in the schema is {@code (DOKTITEL, KOMMENT_PUBL, XMLTEXT)}. */
	public static final int MAX_MATCHED_COLUMNS = 3;

	@Override
	public void contributeFunctions(final FunctionContributions functionContributions) {
		final var registry = functionContributions.getFunctionRegistry();
		final var relevanceType = functionContributions.getTypeConfiguration()
			.getBasicTypeRegistry()
			.resolve(StandardBasicTypes.DOUBLE);

		IntStream.rangeClosed(1, MAX_MATCHED_COLUMNS)
			.forEach(columnCount -> registry.registerPattern(FUNCTION_PREFIX + columnCount, pattern(columnCount), relevanceType));
	}

	private static String pattern(final int columnCount) {
		final var columns = IntStream.rangeClosed(1, columnCount)
			.mapToObj("?%d"::formatted)
			.collect(joining(", "));
		return "match (%s) against (?%d in boolean mode)".formatted(columns, columnCount + 1);
	}
}
