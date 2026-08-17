package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.bornFrom;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.bornUntil;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.hasFirstName;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.hasGender;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.hasLastName;

/**
 * Repository for the {@code MANTAL} census-record table.
 *
 * <p>
 * The {@code MANTAL} table has no publish ({@code OPTIONS}) column, so no publish filter is applied — every row is
 * searchable.
 */
@CircuitBreaker(name = "censusRecordRepository")
public interface CensusRecordRepository extends JpaRepository<CensusRecordEntity, Integer>, JpaSpecificationExecutor<CensusRecordEntity> {

	/**
	 * Searches census records with all filter parameters optional (a blank or {@code null} parameter is ignored). The
	 * birth year is read from {@code FODAR}, which is dirty free text; see
	 * {@link se.sundsvall.memories.integration.db.specification.SpecificationBuilder#buildYearAtLeastFilter} for how a
	 * value without a readable year is treated.
	 */
	default Page<CensusRecordEntity> findAllByParameters(final CensusRecordParameters parameters, final Pageable pageable) {
		return findAll(hasLastName(parameters.getLastName())
			.and(hasFirstName(parameters.getFirstName()))
			.and(hasGender(parameters.getGender()))
			.and(bornFrom(parameters.getYearFrom()))
			.and(bornUntil(parameters.getYearTo())),
			pageable);
	}
}
