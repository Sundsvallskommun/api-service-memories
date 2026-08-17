package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.activeFrom;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.activeUntil;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.fetchCategory;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.fetchTopography;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.hasCategory;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.hasId;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.hasName;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.matchesLocation;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.notPlaceholder;
import static se.sundsvall.memories.integration.db.specification.LegalEntitySpecification.published;

/**
 * Repository for the {@code JURPERS} legal-entity table.
 */
@CircuitBreaker(name = "legalEntityRepository")
public interface LegalEntityRepository extends JpaRepository<LegalEntityEntity, Integer>, JpaSpecificationExecutor<LegalEntityEntity> {

	/**
	 * Searches published legal entities with all filter parameters optional (a blank or {@code null} parameter is
	 * ignored). The name filter matches the registered name or any alternative one; the location filter matches the
	 * place name reached through the topography association or the free-text place. The year filters treat
	 * {@code STARTDATUM}/{@code SLUTDATUM} as an activity period and keep the entities whose period overlaps the
	 * requested range, with a missing bound read as an open period rather than as no period.
	 */
	default Page<LegalEntityEntity> findAllByParameters(final LegalEntityParameters parameters, final Pageable pageable) {
		return findAll(fetchTopography()
			.and(fetchCategory())
			.and(published())
			.and(notPlaceholder())
			.and(hasName(parameters.getName()))
			.and(matchesLocation(parameters.getLocation()))
			.and(hasCategory(parameters.getCategoryId()))
			.and(activeFrom(parameters.getYearFrom()))
			.and(activeUntil(parameters.getYearTo())),
			pageable);
	}

	/**
	 * Looks up a single legal entity by id, excluding the placeholder row {@code J_ID = 1} ("ingen").
	 *
	 * <p>
	 * <strong>The published bit is deliberately NOT applied here.</strong> Unlike
	 * {@link #findAllByParameters(LegalEntityParameters, Pageable)}, this lookup returns unpublished records as well.
	 * That is an intentional decision, not an oversight: a planned administrative interface needs to reach unpublished
	 * records by id. Do not add a published filter here.
	 */
	default Optional<LegalEntityEntity> findVisibleById(final Integer id) {
		return findOne(fetchTopography()
			.and(fetchCategory())
			.and(hasId(id))
			.and(notPlaceholder()));
	}
}
