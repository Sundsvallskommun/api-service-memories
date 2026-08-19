package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.SeamanParameters;
import se.sundsvall.memories.integration.db.model.SeamanEntity;

import static se.sundsvall.memories.integration.db.specification.SeamanSpecification.bornFrom;
import static se.sundsvall.memories.integration.db.specification.SeamanSpecification.bornUntil;
import static se.sundsvall.memories.integration.db.specification.SeamanSpecification.hasBirthParish;
import static se.sundsvall.memories.integration.db.specification.SeamanSpecification.hasFirstName;
import static se.sundsvall.memories.integration.db.specification.SeamanSpecification.hasLastName;

@CircuitBreaker(name = "seamanRepository")
public interface SeamanRepository extends JpaRepository<SeamanEntity, Integer>, JpaSpecificationExecutor<SeamanEntity> {

	default Page<SeamanEntity> findAllByParameters(final SeamanParameters parameters, final Pageable pageable) {
		return findAll(hasLastName(parameters.getLastName())
			.and(hasFirstName(parameters.getFirstName()))
			.and(hasBirthParish(parameters.getBirthParish()))
			.and(bornFrom(parameters.getYearFrom()))
			.and(bornUntil(parameters.getYearTo())),
			pageable);
	}
}
