package se.sundsvall.memories.service;

import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;

import static se.sundsvall.memories.configuration.CacheNames.ARCHIVE_CREATORS_CACHE;

/**
 * The legal entities that are the arkivbildare of at least one object, for {@code /legal-entities?hasObjects=true}.
 * Finding them is a full pass over the combined view, about a second against the real data, and the answer only
 * changes when the archive is edited, so it is cached like the facet counts (see {@code spring.cache}).
 * Loading is synchronized, so searches that arrive while the entry is missing wait for
 * the one pass instead of each running it.
 */
@Service
public class ArchiveCreatorService {

	private final CombinedObjectRepository combinedObjectRepository;

	public ArchiveCreatorService(final CombinedObjectRepository combinedObjectRepository) {
		this.combinedObjectRepository = combinedObjectRepository;
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = ARCHIVE_CREATORS_CACHE, sync = true)
	public Set<Integer> getLegalEntityIdsWithObjects() {
		return Set.copyOf(combinedObjectRepository.findArchiveCreatorLegalEntityIds());
	}
}
