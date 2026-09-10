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
import se.sundsvall.memories.integration.db.CategoryRepository;
import se.sundsvall.memories.integration.db.model.CategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * Exercises {@link CategorySpecification} against a real MariaDB instance (Testcontainers): which rows count as
 * blank, and how the names collate, is database behaviour. Each test is rolled back.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class CategorySpecificationTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	void clearTables() {
		entityManager.createNativeQuery("DELETE FROM KATEGORI").executeUpdate();
	}

	/**
	 * The sentinel row and the nameless ones are left out, whether the name is missing or blank, and the rest come in
	 * name order regardless of id or case.
	 */
	@Test
	void findAllSelectableOffersTheNamedCategoriesButNotTheSentinelByName() {
		persist(1, "", "");
		persist(5, "KOM", "Kommitté");
		persist(2, "AB", "aktiebolag");
		persist(7, "BY", "By");
		persist(9, "NUL", null);
		persist(10, "BLK", "   ");

		assertThat(categoryRepository.findAllSelectable())
			.extracting(CategoryEntity::getCategoryId, CategoryEntity::getName)
			.containsExactly(tuple(2, "aktiebolag"), tuple(7, "By"), tuple(5, "Kommitté"));
	}

	/** A named sentinel is still the sentinel: it is the id that makes it one, not the blank name. */
	@Test
	void findAllSelectableLeavesOutANamedSentinel() {
		persist(1, "ING", "Ingen");
		persist(5, "KOM", "Kommitté");

		assertThat(categoryRepository.findAllSelectable())
			.extracting(CategoryEntity::getCategoryId)
			.containsExactly(5);
	}

	private void persist(final int id, final String code, final String name) {
		entityManager.persist(CategoryEntity.create().withCategoryId(id).withCode(code).withName(name));
		entityManager.flush();
	}
}
