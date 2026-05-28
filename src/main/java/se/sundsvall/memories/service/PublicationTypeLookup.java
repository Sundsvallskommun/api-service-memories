package se.sundsvall.memories.service;

import jakarta.annotation.PostConstruct;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.memories.integration.db.PublicationTypeRepository;

import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * Loads the small PUBL_TYP lookup table into memory at startup so that PublicationService can resolve {@code P_T_ID}
 * to a publication-type display string without joining at query time. The table is small and effectively static, so
 * a single eager load is fine.
 */
@Component
public class PublicationTypeLookup {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublicationTypeLookup.class);

	private final PublicationTypeRepository publicationTypeRepository;
	private Map<Integer, String> publicationTypeById = Map.of();

	public PublicationTypeLookup(final PublicationTypeRepository publicationTypeRepository) {
		this.publicationTypeRepository = publicationTypeRepository;
	}

	@PostConstruct
	void loadCache() {
		publicationTypeById = publicationTypeRepository.findAll().stream()
			.map(entry -> new SimpleImmutableEntry<>(entry.getId(), entry.getPublicationType()))
			.filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()) && !e.getValue().isBlank())
			.collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
		LOGGER.info("Loaded {} publication-type entries into cache", publicationTypeById.size());
	}

	/**
	 * Resolves a PUBL_TYP FK to a publication-type label (PUBLIKTYP).
	 *
	 * @param  id the PUBL_TYP id (often nullable)
	 * @return    the resolved publication type, or {@code null} if the id is missing or unknown
	 */
	public String resolve(final Integer id) {
		if (id == null) {
			return null;
		}
		if (publicationTypeById.isEmpty()) {
			loadCache();
		}
		return publicationTypeById.get(id);
	}
}
