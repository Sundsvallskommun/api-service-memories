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
import se.sundsvall.memories.integration.db.InstitutionRepository;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link InstitutionSpecification} against a real MariaDB instance (Testcontainers): which rows have a name
 * is decided in the database. The order is not — {@link se.sundsvall.memories.service.InstitutionService} applies it,
 * and its test covers it. Each test is rolled back.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class InstitutionSpecificationTest {

	@Autowired
	private InstitutionRepository institutionRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		entityManager.createNativeQuery("DELETE FROM INSTITUTION").executeUpdate();
	}

	/**
	 * A row is offered when it has a name. A code alone is not one, and neither is whitespace, whatever the column's
	 * collation makes of a trailing space — which is what keeps a blank sentinel row out without the list knowing its
	 * id.
	 */
	@Test
	void findAllSelectableOffersEveryNamedRow() {
		persist(1, "", "");
		persist(2, "Sundsvalls museum", "SVM");
		persist(3, null, "FAV");
		persist(4, "  ", "  ");
		persist(5, "Föreningsarkivet Västernorrland", null);

		assertThat(institutionRepository.findAllSelectable())
			.extracting(InstitutionEntity::getId)
			.containsExactlyInAnyOrder(2, 5);
	}

	private void persist(final int id, final String name, final String code) {
		entityManager.persist(InstitutionEntity.create().withId(id).withName(name).withCode(code));
		entityManager.flush();
	}
}
