package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * Exercises {@link TopographySpecification} against a real MariaDB instance (Testcontainers): which rows have a display
 * name is decided in the database. The order is not — {@link se.sundsvall.memories.service.TopographyService} applies
 * it,
 * and its test covers it. Each test is rolled back.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class TopographySpecificationTest {

	@Autowired
	private TopographyRepository topographyRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
	}

	/**
	 * A row is offered when any of name, place or code is non-blank, resolving to the same fallback
	 * {@link TopographyEntity#getDisplayName()} shows it under. A row blank in all three, which is what the sentinel the
	 * object tables default to looks like, is left out without the list knowing its id — space-padded included,
	 * whatever the column's collation makes of a trailing space.
	 */
	@Test
	void findAllSelectableOffersEveryRowWithADisplayName() {
		persist(1, "", "", "", "");
		persist(2, "Timrå", "TIM", "Timrå kommun", "Sverige");
		persist(3, "", "ALK", "Alnö kommun", "Sverige");
		persist(4, null, "SUN", null, "Sverige");
		persist(5, "Alnö", "ALN", "Sundsvalls kommun", "Sverige");
		persist(6, "  ", "  ", "  ", "Sverige");

		assertThat(topographyRepository.findAllSelectable())
			.extracting(TopographyEntity::getId, TopographyEntity::getDisplayName)
			.containsExactlyInAnyOrder(tuple(5, "Alnö"), tuple(3, "Alnö kommun"), tuple(4, "SUN"), tuple(2, "Timrå"));
	}

	private void persist(final int id, final String name, final String code, final String place, final String country) {
		entityManager.persist(TopographyEntity.create().withId(id).withName(name).withCode(code).withPlace(place).withCountry(country));
		entityManager.flush();
	}
}
