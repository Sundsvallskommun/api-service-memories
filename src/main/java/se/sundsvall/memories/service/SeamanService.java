package se.sundsvall.memories.service;

import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedSeamanResponse;
import se.sundsvall.memories.api.model.Seaman;
import se.sundsvall.memories.api.model.SeamanParameters;
import se.sundsvall.memories.integration.db.SeamanRepository;
import se.sundsvall.memories.service.mapper.SeamanMapper;
import se.sundsvall.memories.service.util.Pageables;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class SeamanService {

	private static final String SEAMAN_NOT_FOUND = "Seaman with id '%s' not found";

	private final SeamanRepository seamanRepository;

	public SeamanService(final SeamanRepository seamanRepository) {
		this.seamanRepository = seamanRepository;
	}

	public PagedSeamanResponse search(final SeamanParameters parameters) {
		final var pageable = Pageables.of(parameters, "id");

		final var page = seamanRepository.findAllByParameters(parameters, pageable);

		return PagedSeamanResponse.create()
			.withSeamen(SeamanMapper.toSeamanList(page.getContent()))
			.withMetaData(Pageables.metaDataOf(page, "id"));
	}

	public Seaman getById(final Integer id) {
		return seamanRepository.findById(id)
			.map(SeamanMapper::toSeaman)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, SEAMAN_NOT_FOUND.formatted(id)));
	}
}
