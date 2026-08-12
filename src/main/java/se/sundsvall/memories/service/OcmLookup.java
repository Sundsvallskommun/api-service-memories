package se.sundsvall.memories.service;

import jakarta.annotation.PostConstruct;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.OcmRepository;
import se.sundsvall.memories.integration.db.model.OcmEntity;

import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * Loads the small OCM subject table into memory at startup so that the photo service can resolve the OCM ids behind
 * FOTO_OCM without joining at query time. The table is small and effectively static, so a single eager load is fine.
 */
@Component
public class OcmLookup {

	private static final Logger LOGGER = LoggerFactory.getLogger(OcmLookup.class);

	private final OcmRepository ocmRepository;
	private Map<Integer, Subject> subjectById = Map.of();

	public OcmLookup(final OcmRepository ocmRepository) {
		this.ocmRepository = ocmRepository;
	}

	@PostConstruct
	void loadCache() {
		subjectById = ocmRepository.findAll().stream()
			.map(entry -> new SimpleImmutableEntry<>(entry.getId(), toSubject(entry)))
			.filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()))
			.collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
		LOGGER.info("Loaded {} OCM entries into cache", subjectById.size());
	}

	/**
	 * Resolves an OCM FK to a full {@link Subject} (code + text + description).
	 *
	 * @param  oId the OCM id
	 * @return     the resolved Subject, or {@code null} if the id is missing or unknown
	 */
	public Subject resolveSubject(final Integer oId) {
		if (oId == null) {
			return null;
		}
		if (subjectById.isEmpty()) {
			loadCache();
		}
		return subjectById.get(oId);
	}

	private static Subject toSubject(final OcmEntity entry) {
		return Subject.create()
			.withCode(entry.getCode())
			.withText(entry.getText())
			.withDescription(entry.getDescription());
	}
}
