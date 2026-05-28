package se.sundsvall.memories.service.mapper;

/**
 * Resolves a foreign-key id (e.g. a TOPOGRAFI or OCM reference) to its display string.
 *
 * <p>
 * A dedicated functional interface is used instead of {@link java.util.function.Function Function&lt;Integer,
 * String&gt;} because the FK ids are nullable {@link Integer}s: a primitive-specialised {@code IntFunction} would force
 * unboxing and NPE on the (common) {@code null} id. Lookup components expose a matching {@code resolve(Integer)}
 * method,
 * so {@code someLookup::resolve} binds to this interface directly.
 */
@FunctionalInterface
public interface ReferenceResolver {

	/**
	 * @param  id the foreign-key id to resolve (nullable)
	 * @return    the resolved display string, or {@code null} if the id is missing or unknown
	 */
	String resolve(Integer id);
}
