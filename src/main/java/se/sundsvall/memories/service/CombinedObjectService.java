package se.sundsvall.memories.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.api.model.PagedCombinedObjectResponse;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.service.mapper.CombinedObjectMapper;
import se.sundsvall.memories.service.util.Pageables;

@Service
public class CombinedObjectService {

	private final CombinedObjectRepository combinedObjectRepository;
	private final CombinedObjectFacetService combinedObjectFacetService;

	public CombinedObjectService(final CombinedObjectRepository combinedObjectRepository, final CombinedObjectFacetService combinedObjectFacetService) {
		this.combinedObjectRepository = combinedObjectRepository;
		this.combinedObjectFacetService = combinedObjectFacetService;
	}

	@Transactional(readOnly = true)
	public PagedCombinedObjectResponse search(final CombinedObjectParameters parameters) {
		// Unordered on purpose: this search orders itself from its specification, tiebreak included.
		final var pageable = Pageables.unordered(parameters);

		final var page = combinedObjectRepository.findAllByParameters(parameters, pageable);

		final var facets = combinedObjectFacetService.getFacets(parameters);

		return PagedCombinedObjectResponse.create()
			.withObjects(CombinedObjectMapper.toCombinedObjectList(page.getContent()))
			.withTypeCounts(facets.typeCounts())
			.withGenderCounts(facets.genderCounts())
			.withCategoryCounts(facets.categoryCounts())
			.withTopographyCounts(facets.topographyCounts())
			.withMetaData(Pageables.metaDataOf(page, parameters));
	}
}
