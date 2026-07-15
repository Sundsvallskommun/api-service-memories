package se.sundsvall.memories.service;

import java.util.LinkedHashMap;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.api.model.PagedCombinedObjectResponse;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository.TypeCount;
import se.sundsvall.memories.service.mapper.CombinedObjectMapper;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Service
public class CombinedObjectService {

	private final CombinedObjectRepository combinedObjectRepository;
	private final TopographyLookup topographyLookup;

	public CombinedObjectService(final CombinedObjectRepository combinedObjectRepository, final TopographyLookup topographyLookup) {
		this.combinedObjectRepository = combinedObjectRepository;
		this.topographyLookup = topographyLookup;
	}

	public PagedCombinedObjectResponse search(final CombinedObjectParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var query = blankToNull(parameters.getQuery());
		final var location = blankToNull(parameters.getLocation());

		final var page = combinedObjectRepository.search(query, parameters.getYearFrom(), parameters.getYearTo(), location, pageable);
		final var typeCounts = combinedObjectRepository.countByType(query, parameters.getYearFrom(), parameters.getYearTo(), location).stream()
			.collect(toMap(TypeCount::getObjectType, TypeCount::getTotal, (a, b) -> a, LinkedHashMap::new));

		return PagedCombinedObjectResponse.create()
			.withObjects(CombinedObjectMapper.toCombinedObjectList(page.getContent(), topographyLookup::resolve))
			.withTypeCounts(typeCounts)
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	private static String blankToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.orElse(null);
	}
}
