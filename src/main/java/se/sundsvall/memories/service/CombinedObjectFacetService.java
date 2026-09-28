package se.sundsvall.memories.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.api.model.CategoryCount;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.api.model.GenderCount;
import se.sundsvall.memories.api.model.ObjectTypeCount;
import se.sundsvall.memories.api.model.TopographyCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.service.mapper.CombinedObjectMapper;

import static se.sundsvall.memories.configuration.CacheConfiguration.OBJECT_FACETS_CACHE;

/**
 * The four facet counts {@code /objects} returns next to a page. Each is a full pass over the combined view, so they
 * are cached: a page turn, a change of sort or a class of pupils sending the same search reuse them. The key holds
 * only the filters — paging and sorting do not change a count. An edit to the archive shows up in the counts once the
 * entry expires (see {@code spring.cache} in the application configuration).
 */
@Service
public class CombinedObjectFacetService {

	private final CombinedObjectRepository combinedObjectRepository;

	public CombinedObjectFacetService(final CombinedObjectRepository combinedObjectRepository) {
		this.combinedObjectRepository = combinedObjectRepository;
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = OBJECT_FACETS_CACHE, key = "T(se.sundsvall.memories.service.CombinedObjectFacetService).key(#parameters)")
	public Facets getFacets(final CombinedObjectParameters parameters) {
		return new Facets(
			CombinedObjectMapper.toObjectTypeCountList(combinedObjectRepository.countByType(parameters)),
			CombinedObjectMapper.toGenderCountList(combinedObjectRepository.countByGender(parameters)),
			CombinedObjectMapper.toCategoryCountList(combinedObjectRepository.countByCategory(parameters)),
			CombinedObjectMapper.toTopographyCountList(combinedObjectRepository.countByTopography(parameters)));
	}

	/**
	 * The filters the counts depend on, and nothing else.
	 *
	 * @param  parameters the search parameters
	 * @return            a key that is equal for two searches with the same filters
	 */
	public static List<Object> key(final CombinedObjectParameters parameters) {
		return Arrays.asList(parameters.getQuery(), parameters.getYearFrom(), parameters.getYearTo(), parameters.getLocation(),
			parameters.getTopographyId(), parameters.getObjectType(), parameters.getGender(), parameters.getCreator(),
			parameters.getCreatorPersonId(), parameters.getCreatorLegalEntityId(), parameters.getCategoryId(), parameters.getNodeId());
	}

	public record Facets(List<ObjectTypeCount> typeCounts, List<GenderCount> genderCounts, List<CategoryCount> categoryCounts,
		List<TopographyCount> topographyCounts) {
	}
}
