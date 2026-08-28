package se.sundsvall.memories.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.CensusRecord;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.api.model.PagedCensusRecordResponse;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.integration.db.model.CensusRecordId;
import se.sundsvall.memories.service.mapper.CensusRecordMapper;
import se.sundsvall.memories.service.util.Pageables;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CensusRecordService {

	private static final String CENSUS_RECORD_NOT_FOUND = "Census record with id '%s' not found";

	/**
	 * The API id: the census volume and the row number within it, joined with {@code -} (e.g. {@code 1845-123}). The
	 * volume may itself contain a dash, so the row number is whatever follows the last one.
	 */
	private static final Pattern VOLUME_AND_ROW = Pattern.compile("(.+)-(\\d{1,9})");

	private final CensusRecordRepository censusRecordRepository;

	public CensusRecordService(final CensusRecordRepository censusRecordRepository) {
		this.censusRecordRepository = censusRecordRepository;
	}

	public PagedCensusRecordResponse search(final CensusRecordParameters parameters) {
		final var pageable = Pageables.of(parameters, "source", "id");

		final var page = censusRecordRepository.findAllByParameters(parameters, pageable);

		return PagedCensusRecordResponse.create()
			.withCensusRecords(CensusRecordMapper.toCensusRecordList(page.getContent()))
			.withMetaData(Pageables.metaDataOf(page, "source", "id"));
	}

	public CensusRecord getById(final String id) {
		return toCensusRecordId(id)
			.flatMap(censusRecordRepository::findById)
			.map(CensusRecordMapper::toCensusRecord)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, CENSUS_RECORD_NOT_FOUND.formatted(id)));
	}

	/** An id that does not parse cannot name a record, so it reads as not found rather than as an error. */
	private static Optional<CensusRecordId> toCensusRecordId(final String id) {
		return ofNullable(id)
			.map(VOLUME_AND_ROW::matcher)
			.filter(Matcher::matches)
			.map(matcher -> new CensusRecordId(matcher.group(1), Integer.valueOf(matcher.group(2))));
	}
}
