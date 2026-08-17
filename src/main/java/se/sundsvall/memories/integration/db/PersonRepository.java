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
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.notPlaceholder;
import static se.sundsvall.memories.integration.db.specification.PersonSpecification.published;

/**
 * Repository for the {@code PERSON} table.
 */
@CircuitBreaker(name = "personRepository")
public interface PersonRepository extends JpaRepository<PersonEntity, Integer>, JpaSpecificationExecutor<PersonEntity> {

	/**
	 * Searches published persons with all filter parameters optional (a blank or {@code null} parameter is ignored). The
	 * birth year is read from {@code FODDAT}, which is dirty free text; see
	 * {@link se.sundsvall.memories.integration.db.specification.SpecificationBuilder#buildYearAtLeastFilter} for how a
	 * value without a readable year is treated.
	 */
	default Page<PersonEntity> findAllByParameters(final PersonParameters parameters, final Pageable pageable) {
		return findAll(published()
			.and(notPlaceholder())
			.and(hasLastName(parameters.getLastName()))
			.and(hasFirstName(parameters.getFirstName()))
			.and(hasBirthParish(parameters.getBirthParish()))
			.and(hasGender(parameters.getGender()))
			.and(bornFrom(parameters.getYearFrom()))
			.and(bornUntil(parameters.getYearTo())),
			pageable);
	}

	/**
	 * Looks up a single person by id, excluding only the placeholder row {@code P_ID = 0} ("ingen person").
	 *
	 * <p>
	 * <strong>The published bit is deliberately NOT applied here, unlike in {@link #findAllByParameters}.</strong>
	 * Unpublished persons must remain reachable by id: a planned administrative interface needs to fetch them directly.
	 * Hiding them from the search while keeping them addressable by id is the intended behaviour, not an oversight.
	 */
	default Optional<PersonEntity> findVisibleById(final Integer id) {
		return findOne(hasId(id)
			.and(notPlaceholder()));
	}
}
