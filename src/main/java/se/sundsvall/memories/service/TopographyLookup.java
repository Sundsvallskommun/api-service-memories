package se.sundsvall.memories.service;

import jakarta.annotation.PostConstruct;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * Loads the small TOPOGRAFI table into memory at startup so that film/publication/photo services can resolve their
 * topography FK to a place name without joining at query time. The table is small (a few hundred rows) and
 * effectively static, so a single eager load is fine.
 */
@Component
public class TopographyLookup {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopographyLookup.class);

	private final TopographyRepository topographyRepository;
	private Map<Integer, String> locationByTopographyId = Map.of();

	public TopographyLookup(final TopographyRepository topographyRepository) {
		this.topographyRepository = topographyRepository;
	}

	@PostConstruct
	void loadCache() {
		locationByTopographyId = topographyRepository.findAll().stream()
			.map(entry -> new SimpleImmutableEntry<>(entry.getTId(), resolveDisplayName(entry)))
			.filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()))
			.collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
		LOGGER.info("Loaded {} topography entries into cache", locationByTopographyId.size());
	}

	/**
	 * Resolves a topography FK to a display string. Prefers TOPNAMN, falls back to PLATS, then TOPKOD.
	 *
	 * @param  tId the topography id (often nullable, may be 0/1 for unmapped rows)
	 * @return     the resolved place name, or {@code null} if the id is missing or unknown
	 */
	public String resolve(final Integer tId) {
		if (tId == null) {
			return null;
		}
		if (locationByTopographyId.isEmpty()) {
			loadCache();
		}
		return locationByTopographyId.get(tId);
	}

	private static String resolveDisplayName(final TopographyEntity entry) {
		return Optional.ofNullable(entry.getName())
			.filter(s -> !s.isBlank())
			.or(() -> Optional.ofNullable(entry.getPlace()).filter(s -> !s.isBlank()))
			.or(() -> Optional.ofNullable(entry.getCode()).filter(s -> !s.isBlank()))
			.orElse(null);
	}
}
