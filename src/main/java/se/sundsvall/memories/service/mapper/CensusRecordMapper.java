package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.CensusRecord;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class CensusRecordMapper {

	private CensusRecordMapper() {}

	/**
	 * Map a single {@link CensusRecordEntity} to a {@link CensusRecord} API model.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link CensusRecord}, or {@code null} if {@code entity} is null
	 */
	public static CensusRecord toCensusRecord(final CensusRecordEntity entity) {
		return ofNullable(entity)
			.map(e -> CensusRecord.create()
				.withId(e.getId())
				.withObjectNumber(e.getObjectNumber())
				.withSource(e.getSource())
				.withPropertyNumber1(e.getPropertyNumber1())
				.withPropertyPart1(e.getPropertyPart1())
				.withPropertyNumber2(e.getPropertyNumber2())
				.withPropertyPart2(e.getPropertyPart2())
				.withPropertyNumber3(e.getPropertyNumber3())
				.withPropertyPart3(e.getPropertyPart3())
				.withSerialNumber(e.getSerialNumber())
				.withHouseholdNumber(e.getHouseholdNumber())
				.withOrderNumber(e.getOrderNumber())
				.withFarmNumber(e.getFarmNumber())
				.withOccupationRelation(e.getOccupationRelation())
				.withRelationCode(e.getRelationCode())
				.withFirstName(e.getFirstName())
				.withLastName(e.getLastName())
				.withGender(e.getGender())
				.withBirthYear(e.getBirthYear())
				.withNote(e.getNote()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link CensusRecordEntity} objects to {@link CensusRecord} API models.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link CensusRecord} objects (empty if {@code entities} is null)
	 */
	public static List<CensusRecord> toCensusRecordList(final List<CensusRecordEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(CensusRecordMapper::toCensusRecord)
			.toList();
	}
}
