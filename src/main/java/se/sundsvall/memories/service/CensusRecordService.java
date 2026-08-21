package se.sundsvall.memories.service;

import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.CensusRecord;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.api.model.PagedCensusRecordResponse;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.service.mapper.CensusRecordMapper;
import se.sundsvall.memories.service.util.Pageables;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CensusRecordService {

	private static final String CENSUS_RECORD_NOT_FOUND = "Census record with id '%s' not found";

	private final CensusRecordRepository censusRecordRepository;

	public CensusRecordService(final CensusRecordRepository censusRecordRepository) {
		this.censusRecordRepository = censusRecordRepository;
	}

	public PagedCensusRecordResponse search(final CensusRecordParameters parameters) {
		final var pageable = Pageables.of(parameters, "id");

		final var page = censusRecordRepository.findAllByParameters(parameters, pageable);

		return PagedCensusRecordResponse.create()
			.withCensusRecords(CensusRecordMapper.toCensusRecordList(page.getContent()))
			.withMetaData(Pageables.metaDataOf(page, "id"));
	}

	public CensusRecord getById(final Integer id) {
		return censusRecordRepository.findById(id)
			.map(CensusRecordMapper::toCensusRecord)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, CENSUS_RECORD_NOT_FOUND.formatted(id)));
	}
}
