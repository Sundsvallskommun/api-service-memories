package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link LegalEntitySpecification} against a real MariaDB instance (Testcontainers), because the behaviour
 * under test — the {@code bitand} bitmask function, {@code LIKE} escaping and how a missing or dirty date compares — is
 * database behaviour, not Java behaviour.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class LegalEntitySpecificationTest {

	private static final Integer PUBLISHED = 4;

	@Autowired
	private LegalEntityRepository legalEntityRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		legalEntityRepository.deleteAll();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM KATEGORI").executeUpdate();
		legalEntityRepository.flush();
	}

	private LegalEntityEntity persist(final Integer id, final Integer options, final String name) {
		return legalEntityRepository.saveAndFlush(LegalEntityEntity.create()
			.withLegalEntityId(id)
			.withOptions(options)
			.withName(name));
	}

	private LegalEntityEntity persistActive(final Integer id, final String startDate, final String endDate) {
		return legalEntityRepository.saveAndFlush(LegalEntityEntity.create()
			.withLegalEntityId(id)
			.withOptions(PUBLISHED)
			.withName("entity " + id)
			.withStartDate(startDate)
			.withEndDate(endDate));
	}

	private List<Integer> findIds(final Specification<LegalEntityEntity> specification) {
		return legalEntityRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(LegalEntityEntity::getLegalEntityId)
			.sorted()
			.toList();
	}

	private TopographyEntity persistTopography(final int id, final String name) {
		final var topography = TopographyEntity.create().withId(id).withName(name);
		entityManager.persist(topography);
		entityManager.flush();
		return topography;
	}

	private CategoryEntity persistCategory(final int id, final String name) {
		final var category = CategoryEntity.create().withCategoryId(id).withName(name);
		entityManager.persist(category);
		entityManager.flush();
		return category;
	}

	@Test
	void publishedMatchesRowsWithBitFourSet() {
		persist(2, PUBLISHED, "published");
		persist(3, 0, "unpublished");
		persist(4, 6, "bit 2 and bit 4");

		assertThat(findIds(LegalEntitySpecification.published())).containsExactly(2, 4);
	}

	/**
	 * {@code J_ID = 1} is the sentinel other tables point at to mean "no legal entity". It is flagged as published, so
	 * the published filter alone does not hide it.
	 */
	@Test
	void notPlaceholderExcludesTheSentinelRowThePublishedFilterKeeps() {
		persist(1, 6, "ingen");
		persist(2, PUBLISHED, "Berg AB");

		assertThat(findIds(LegalEntitySpecification.published())).containsExactly(1, 2);
		assertThat(findIds(LegalEntitySpecification.notPlaceholder())).containsExactly(2);
	}

	@Test
	void hasNameMatchesTheRegisteredNameOrAnAlternativeOne() {
		legalEntityRepository.saveAndFlush(LegalEntityEntity.create().withLegalEntityId(2).withOptions(PUBLISHED)
			.withName("Nödhjälpskommittén 1888-1889"));
		legalEntityRepository.saveAndFlush(LegalEntityEntity.create().withLegalEntityId(3).withOptions(PUBLISHED)
			.withName("Berg AB").withAlternativeNames("Bergs handel"));

		assertThat(findIds(LegalEntitySpecification.hasName("nödhjälp"))).containsExactly(2);
		assertThat(findIds(LegalEntitySpecification.hasName("bergs handel"))).containsExactly(3);
		assertThat(findIds(LegalEntitySpecification.hasName("   "))).containsExactly(2, 3);
	}

	@Test
	void matchesLocationFindsThePlaceThroughTheAssociationOrTheFreeText() {
		persist(2, PUBLISHED, "a").setTopography(persistTopography(500, "Sundsvall"));
		persist(3, PUBLISHED, "b").setLocationText("Alnö");
		persist(4, PUBLISHED, "c").setTopography(persistTopography(501, "Timrå"));
		legalEntityRepository.flush();

		assertThat(findIds(LegalEntitySpecification.matchesLocation("sundsvall"))).containsExactly(2);
		assertThat(findIds(LegalEntitySpecification.matchesLocation("alnö"))).containsExactly(3);
	}

	@Test
	void hasCategoryMatchesThroughTheAssociation() {
		persist(2, PUBLISHED, "a").setCategory(persistCategory(5, "Kommitté"));
		persist(3, PUBLISHED, "b").setCategory(persistCategory(6, "Aktiebolag"));
		persist(4, PUBLISHED, "c");
		legalEntityRepository.flush();

		assertThat(findIds(LegalEntitySpecification.hasCategory(5))).containsExactly(2);
		assertThat(findIds(LegalEntitySpecification.hasCategory(null))).containsExactly(2, 3, 4);
	}

	@Test
	void activeFiltersKeepEntitiesWhosePeriodOverlapsTheRange() {
		persistActive(2, "1880", "1885");
		persistActive(3, "1888", "1895");
		persistActive(4, "1900", "1910");

		assertThat(findIds(LegalEntitySpecification.activeFrom(1890))).containsExactly(3, 4);
		assertThat(findIds(LegalEntitySpecification.activeUntil(1890))).containsExactly(2, 3);
		assertThat(findIds(Specification.allOf(LegalEntitySpecification.activeFrom(1888), LegalEntitySpecification.activeUntil(1895)))).containsExactly(3);
	}

	/**
	 * An entity without an end date has not ended, so it is still active in every later range — unlike the material
	 * types, where an unreadable date means the object has no date at all and falls outside every range. The same
	 * applies to a missing start date, which says nothing about when the entity began.
	 */
	@Test
	void activeFiltersTreatAMissingOrUnreadableDateAsAnOpenPeriod() {
		persistActive(2, "1880", null);
		persistActive(3, "1880", "");
		persistActive(4, "1880", "okänt");
		persistActive(5, null, "1885");
		persistActive(6, "1880", "1885");

		assertThat(findIds(LegalEntitySpecification.activeFrom(1990))).containsExactly(2, 3, 4);
		assertThat(findIds(LegalEntitySpecification.activeUntil(1800))).containsExactly(5);
	}

	@Test
	void findAllByParametersCombinesEveryFilter() {
		final var kommitte = persistCategory(5, "Kommitté");
		final var sundsvall = persistTopography(500, "Sundsvall");

		persist(1, 6, "Nödhjälpskommittén");
		final var match = persistActive(2, "1880", "1895");
		match.setCategory(kommitte);
		match.setTopography(sundsvall);
		match.setName("Nödhjälpskommittén");
		final var wrongCategory = persistActive(3, "1880", "1895");
		wrongCategory.setTopography(sundsvall);
		wrongCategory.setName("Nödhjälpskommittén");
		final var wrongPeriod = persistActive(4, "1900", "1910");
		wrongPeriod.setCategory(kommitte);
		wrongPeriod.setTopography(sundsvall);
		wrongPeriod.setName("Nödhjälpskommittén");
		legalEntityRepository.saveAndFlush(LegalEntityEntity.create().withLegalEntityId(5).withOptions(0)
			.withName("Nödhjälpskommittén").withCategory(kommitte).withTopography(sundsvall).withStartDate("1880").withEndDate("1895"));
		legalEntityRepository.flush();

		final var parameters = LegalEntityParameters.create()
			.withName("nödhjälp")
			.withLocation("sundsvall")
			.withCategoryId(5)
			.withYearFrom(1885)
			.withYearTo(1890);

		final var page = legalEntityRepository.findAllByParameters(parameters, Pageable.unpaged());

		// Row 1 is the placeholder, row 3 has no category, row 4 was active later, row 5 is unpublished.
		assertThat(page.getContent()).extracting(LegalEntityEntity::getLegalEntityId).containsExactly(2);
	}

	/**
	 * Get-by-id deliberately keeps unpublished entities reachable — only the placeholder row is hidden.
	 */
	@Test
	void findVisibleByIdKeepsUnpublishedButHidesThePlaceholder() {
		persist(1, 6, "ingen");
		persist(2, 0, "unpublished");

		assertThat(legalEntityRepository.findVisibleById(2)).isPresent();
		assertThat(legalEntityRepository.findVisibleById(1)).isEmpty();
	}

	@Test
	void findAllByParametersResolvesBothAssociationsAndSurvivesPaging() {
		persist(2, PUBLISHED, "a").setTopography(persistTopography(500, "Sundsvall"));
		persist(3, PUBLISHED, "b").setCategory(persistCategory(5, "Kommitté"));
		legalEntityRepository.flush();
		entityManager.clear();

		// The fetch joins are invalid in the count projection, so the specification must skip them there.
		final var page = legalEntityRepository.findAllByParameters(LegalEntityParameters.create(), Pageable.ofSize(1));

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).hasSize(1);
	}
}
