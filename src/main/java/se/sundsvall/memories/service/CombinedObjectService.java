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
		// Unordered on purpose: this search orders itself from its specification, tiebreak included.
		final var pageable = Pageables.unordered(parameters);

		final var page = combinedObjectRepository.findAllByParameters(parameters, pageable);

		final var typeCounts = combinedObjectRepository.countByType(parameters);

		return PagedCombinedObjectResponse.create()
			.withObjects(CombinedObjectMapper.toCombinedObjectList(page.getContent()))
			.withTypeCounts(CombinedObjectMapper.toObjectTypeCountList(typeCounts))
			.withMetaData(Pageables.metaDataOf(page, parameters));
	}
}
