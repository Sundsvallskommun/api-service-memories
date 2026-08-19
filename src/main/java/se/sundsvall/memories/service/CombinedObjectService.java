package se.sundsvall.memories.service;

import java.util.LinkedHashMap;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.api.model.PagedCombinedObjectResponse;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository.TypeCount;
import se.sundsvall.memories.service.mapper.CombinedObjectMapper;

import static java.util.stream.Collectors.toMap;
import static se.sundsvall.memories.service.util.StringUtil.trimToNull;

@Service
public class CombinedObjectService {

	private final CombinedObjectRepository combinedObjectRepository;

	public CombinedObjectService(final CombinedObjectRepository combinedObjectRepository) {
		this.combinedObjectRepository = combinedObjectRepository;
	}

	@Transactional(readOnly = true)
	public PagedCombinedObjectResponse search(final CombinedObjectParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = combinedObjectRepository.findAllByParameters(parameters, pageable);

		// The search trims through its specifications; the counters are a native query, so they have to be handed
		// trimmed values to filter on the same rows.
		final var typeCounts = combinedObjectRepository.countByType(
			trimToNull(parameters.getQuery()),
			parameters.getYearFrom(),
			parameters.getYearTo(),
			trimToNull(parameters.getLocation())).stream()
			.collect(toMap(TypeCount::getObjectType, TypeCount::getTotal, (first, _) -> first, LinkedHashMap::new));

		return PagedCombinedObjectResponse.create()
			.withObjects(CombinedObjectMapper.toCombinedObjectList(page.getContent()))
			.withTypeCounts(typeCounts)
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}
}
