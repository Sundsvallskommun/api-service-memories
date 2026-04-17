package se.sundsvall.memories.service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.memories.integration.db.TopografiRepository;
import se.sundsvall.memories.integration.db.model.TopografiEntity;

/**
 * Loads the small TOPOGRAFI table into memory at startup so that FILM/PUBL/FOTO services can resolve their
 * topografi-FK to a place name without joining at query time. The table is small (a few hundred rows) and
 * effectively static, so a single eager load is fine.
 */
@Component
public class TopografiLookup {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopografiLookup.class);

	private final TopografiRepository topografiRepository;
	private Map<Integer, String> placeByTId = Map.of();

	public TopografiLookup(final TopografiRepository topografiRepository) {
		this.topografiRepository = topografiRepository;
	}

	@PostConstruct
	void loadCache() {
		final var entries = topografiRepository.findAll();
		final var map = new HashMap<Integer, String>(entries.size());
		for (final var entry : entries) {
			final var id = entry.getTId();
			if (id == null)
				continue;
			final var name = resolveDisplayName(entry);
			if (name == null)
				continue; // skip entries with no usable place name (Map.copyOf rejects nulls)
			map.put(id, name);
		}
		placeByTId = Map.copyOf(map);
		LOGGER.info("Loaded {} topografi entries into cache", placeByTId.size());
	}

	/**
	 * Resolves a topografi FK to a display string. Prefers TOPNAMN, falls back to PLATS, then TOPKOD.
	 *
	 * @param  tId the topografi id (often nullable, may be 0/1 for unmapped rows)
	 * @return     the resolved place name, or {@code null} if the id is missing or unknown
	 */
	public String resolve(final Integer tId) {
		if (tId == null) {
			return null;
		}
		if (placeByTId.isEmpty()) {
			loadCache();
		}
		return placeByTId.get(tId);
	}

	private static String resolveDisplayName(final TopografiEntity entry) {
		return Optional.ofNullable(entry.getTopNamn())
			.filter(s -> !s.isBlank())
			.or(() -> Optional.ofNullable(entry.getPlats()).filter(s -> !s.isBlank()))
			.or(() -> Optional.ofNullable(entry.getTopKod()).filter(s -> !s.isBlank()))
			.orElse(null);
	}
}
