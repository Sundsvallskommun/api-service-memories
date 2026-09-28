package se.sundsvall.memories.configuration;

/** The names of the caches {@link CacheConfiguration} enables, as {@code spring.cache.cache-names} lists them. */
public final class CacheNames {

	public static final String OBJECT_FACETS_CACHE = "objectFacets";
	public static final String TOPOGRAPHIES_CACHE = "topographies";

	private CacheNames() {}
}
