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

	public CombinedObjectService(final CombinedObjectRepository combinedObjectRepository) {
		this.combinedObjectRepository = combinedObjectRepository;
	}

	@Transactional(readOnly = true)
	public PagedCombinedObjectResponse search(final CombinedObjectParameters parameters) {
		// This search orders itself, so unlike the others it gets an unordered page request: relevance is computed per
		// request and is not a column, and Spring Data would replace the specification's order with the page request's
		// the moment it had one. The id tiebreaker the other searches get from Pageables is appended by the
		// specification instead.
		final var pageable = Pageables.unordered(parameters);

		final var page = combinedObjectRepository.findAllByParameters(parameters, pageable);

		final var typeCounts = combinedObjectRepository.countByType(parameters);

		return PagedCombinedObjectResponse.create()
			.withObjects(CombinedObjectMapper.toCombinedObjectList(page.getContent()))
			.withTypeCounts(CombinedObjectMapper.toObjectTypeCountList(typeCounts))
			.withMetaData(Pageables.metaDataOf(page, parameters));
	}
}
