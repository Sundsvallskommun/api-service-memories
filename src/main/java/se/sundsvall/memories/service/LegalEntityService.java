package se.sundsvall.memories.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.api.model.PagedLegalEntityResponse;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.service.mapper.LegalEntityMapper;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class LegalEntityService {

	private static final String LEGAL_ENTITY_NOT_FOUND = "Legal entity with id '%s' not found";

	private final LegalEntityRepository legalEntityRepository;

	public LegalEntityService(final LegalEntityRepository legalEntityRepository) {
		this.legalEntityRepository = legalEntityRepository;
	}

	@Transactional(readOnly = true)
	public PagedLegalEntityResponse search(final LegalEntityParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = legalEntityRepository.findAllByParameters(parameters, pageable);

		return PagedLegalEntityResponse.create()
			.withLegalEntities(LegalEntityMapper.toLegalEntityList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	@Transactional(readOnly = true)
	public LegalEntity getById(final Integer id) {
		return legalEntityRepository.findVisibleById(id)
			.map(LegalEntityMapper::toLegalEntity)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, LEGAL_ENTITY_NOT_FOUND.formatted(id)));
	}
}
