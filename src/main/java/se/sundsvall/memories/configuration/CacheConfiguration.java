package se.sundsvall.memories.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Caching of the expensive, repeatable reads. The caches (see {@link CacheNames}) and their Caffeine spec (size and
 * time to live) are set under {@code spring.cache} in the application configuration.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
}
