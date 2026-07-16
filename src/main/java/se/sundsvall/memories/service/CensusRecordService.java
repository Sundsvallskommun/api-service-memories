package se.sundsvall.memories.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.CensusRecord;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.api.model.PagedCensusRecordResponse;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.service.mapper.CensusRecordMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CensusRecordService {

	private static final String CENSUS_RECORD_NOT_FOUND = "Census record with id '%s' not found";

	private final CensusRecordRepository censusRecordRepository;

	public CensusRecordService(final CensusRecordRepository censusRecordRepository) {
		this.censusRecordRepository = censusRecordRepository;
	}

	public PagedCensusRecordResponse search(final CensusRecordParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = censusRecordRepository.search(
			blankToNull(parameters.getLastName()),
			blankToNull(parameters.getFirstName()),
			gender(parameters.getGender()),
			parameters.getYearFrom(),
			parameters.getYearTo(),
			pageable);

		return PagedCensusRecordResponse.create()
			.withCensusRecords(CensusRecordMapper.toCensusRecordList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public CensusRecord getById(final Integer id) {
		return censusRecordRepository.findById(id)
			.map(CensusRecordMapper::toCensusRecord)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, CENSUS_RECORD_NOT_FOUND.formatted(id)));
	}

	private static String blankToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.orElse(null);
	}

	/**
	 * Normalises the gender filter: blank and the "both sources" sentinel ("båda") mean "no filter" and map to
	 * {@code null}; any other value is passed through and matched case-insensitively against the stored {@code KON}.
	 */
	private static String gender(final String value) {
		return ofNullable(blankToNull(value))
			.filter(v -> !"båda".equalsIgnoreCase(v))
			.orElse(null);
	}
}
