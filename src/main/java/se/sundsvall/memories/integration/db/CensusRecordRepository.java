package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;
import se.sundsvall.memories.integration.db.model.CensusRecordId;

import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.bornFrom;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.bornUntil;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.hasFirstName;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.hasGender;
import static se.sundsvall.memories.integration.db.specification.CensusRecordSpecification.hasLastName;

@CircuitBreaker(name = "censusRecordRepository")
public interface CensusRecordRepository extends JpaRepository<CensusRecordEntity, CensusRecordId>, JpaSpecificationExecutor<CensusRecordEntity> {

	default Page<CensusRecordEntity> findAllByParameters(final CensusRecordParameters parameters, final Pageable pageable) {
		return findAll(hasLastName(parameters.getLastName())
			.and(hasFirstName(parameters.getFirstName()))
			.and(hasGender(parameters.getGender()))
			.and(bornFrom(parameters.getYearFrom()))
			.and(bornUntil(parameters.getYearTo())),
			pageable);
	}
}
