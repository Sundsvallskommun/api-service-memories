package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.PersonParameters;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static se.sundsvall.memories.integration.db.specification.PersonSpecification.bornFrom;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.bornUntil;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.hasBirthParish;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.hasFirstName;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.hasGender;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.hasLastName;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.notDeleted;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.notPlaceholder;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.published;

@CircuitBreaker(name = "personRepository")
public interface PersonRepository extends JpaRepository<PersonEntity, Integer>, JpaSpecificationExecutor<PersonEntity> {

	default Page<PersonEntity> findAllByParameters(final PersonParameters parameters, final Pageable pageable) {
		return findAll(published()
			.and(notDeleted())
			.and(notPlaceholder())
			.and(hasLastName(parameters.getLastName()))
			.and(hasFirstName(parameters.getFirstName()))
			.and(hasBirthParish(parameters.getBirthParish()))
			.and(hasGender(parameters.getGender()))
			.and(bornFrom(parameters.getYearFrom()))
			.and(bornUntil(parameters.getYearTo())),
			pageable);
	}

	// Unpublished person records stay reachable by id, the way the object searches keep unpublished objects;
	// a deleted one does not, and is not named as an originator either.
	default Optional<PersonEntity> findVisibleById(final Integer id) {
		return findOne(hasId(id)
			.and(notDeleted())
			.and(notPlaceholder()));
	}
}
