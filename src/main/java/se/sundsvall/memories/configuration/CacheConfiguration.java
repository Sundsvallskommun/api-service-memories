package se.sundsvall.memories.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Caching of the expensive, repeatable reads. The caches and their Caffeine spec (size and time to live) are set under
 * {@code spring.cache} in the application configuration.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	public static final String OBJECT_FACETS_CACHE = "objectFacets";
	public static final String TOPOGRAPHIES_CACHE = "topographies";
}
