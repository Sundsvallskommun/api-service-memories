package se.sundsvall.memories.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.api.model.PagedLegalEntityResponse;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.service.mapper.LegalEntityMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class LegalEntityService {

	private static final String LEGAL_ENTITY_NOT_FOUND = "Legal entity with id '%s' not found";

	private final LegalEntityRepository legalEntityRepository;
	private final TopographyLookup topographyLookup;
	private final CategoryLookup categoryLookup;

	public LegalEntityService(final LegalEntityRepository legalEntityRepository, final TopographyLookup topographyLookup, final CategoryLookup categoryLookup) {
		this.legalEntityRepository = legalEntityRepository;
		this.topographyLookup = topographyLookup;
		this.categoryLookup = categoryLookup;
	}

	public PagedLegalEntityResponse search(final LegalEntityParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = legalEntityRepository.search(
			blankToNull(parameters.getName()),
			blankToNull(parameters.getLocation()),
			parameters.getCategoryId(),
			parameters.getYearFrom(),
			parameters.getYearTo(),
			pageable);

		return PagedLegalEntityResponse.create()
			.withLegalEntities(LegalEntityMapper.toLegalEntityList(page.getContent(), topographyLookup::resolve, categoryLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public LegalEntity getById(final Integer id) {
		return legalEntityRepository.findById(id)
			.map(entity -> LegalEntityMapper.toLegalEntity(entity, topographyLookup.resolve(entity.getTopographyId()), categoryLookup.resolve(entity.getCategoryId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, LEGAL_ENTITY_NOT_FOUND.formatted(id)));
	}

	private static String blankToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.orElse(null);
	}
}
