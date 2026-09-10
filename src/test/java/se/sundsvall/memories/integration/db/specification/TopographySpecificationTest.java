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
	 * A row is offered when it has something to build a label out of — the parish, the place inside it, or both. The
	 * code is not a name, so a row carrying only that one is left out, and so is a row blank in every column, which is
	 * what the sentinel the object tables default to looks like: the list never has to know its id. Space-padded counts
	 * as blank whatever the column's collation makes of a trailing space.
	 */
	@Test
	void findAllSelectableOffersEveryRowWithADisplayName() {
		persist(1, "", "", "", "");
		persist(2, "Anundsjö", "228471", "Bredbyn", "Örnsköldsvik");
		persist(3, "Anundsjö", "228471", null, "Örnsköldsvik");
		persist(4, null, "228471", "Björnsjö", "Örnsköldsvik");
		persist(5, "", "SUN", "", "Sundsvalls kommun");
		persist(6, "  ", "  ", "  ", "Örnsköldsvik");

		assertThat(topographyRepository.findAllSelectable())
			.extracting(TopographyEntity::getId, TopographyEntity::getDisplayName)
			.containsExactlyInAnyOrder(tuple(2, "Bredbyn, Anundsjö"), tuple(3, "Anundsjö"), tuple(4, "Björnsjö"));
	}

	private void persist(final int id, final String name, final String code, final String place, final String municipality) {
		entityManager.persist(TopographyEntity.create().withId(id).withName(name).withCode(code).withPlace(place).withMunicipality(municipality));
		entityManager.flush();
	}
}
