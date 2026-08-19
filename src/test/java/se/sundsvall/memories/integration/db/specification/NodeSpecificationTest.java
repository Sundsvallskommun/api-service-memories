package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
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
import se.sundsvall.memories.integration.db.NodeRepository;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises {@link NodeSpecification} against a real MariaDB instance (Testcontainers), because the behaviour under
 * test — {@code LIKE} escaping, the collation-driven case insensitivity, the {@code bitand} published check and how an
 * open period compares — is database behaviour, not Java behaviour.
 *
 * <p>
 * Each test runs in a transaction that is rolled back, so the rows inserted here do not leak between tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class NodeSpecificationTest {

	private static final int PUBLISHED = 6;

	private static final int UNPUBLISHED = 1;

	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	void clearTable() {
		nodeRepository.deleteAll();
		nodeRepository.flush();
	}

	private NodeTypeEntity persistType(final Integer id, final String name) {
		final var nodeType = NodeTypeEntity.create().withId(id).withName(name);
		entityManager.persist(nodeType);
		entityManager.flush();
		return nodeType;
	}

	private void persist(final Integer id, final String name, final String description, final NodeTypeEntity nodeType, final Integer startYear, final Integer stopYear,
		final Integer options) {
		nodeRepository.saveAndFlush(NodeEntity.create()
			.withId(id)
			.withName(name)
			.withDescription(description)
			.withNodeType(nodeType)
			.withStartYear(startYear)
			.withStopYear(stopYear)
			.withOptions(options)
			// SORT, SUBITEMS and SUBITEMS_4 are NOT NULL in the schema; the application only ever reads nodes, so the
			// defaults matter to this test alone.
			.withSortOrder(0)
			.withSubItemCount(0)
			.withPublishedSubItemCount(0));
	}

	private List<Integer> findIds(final Specification<NodeEntity> specification) {
		return nodeRepository.findAll(specification, Pageable.unpaged()).getContent().stream()
			.map(NodeEntity::getId)
			.sorted()
			.toList();
	}

	@Test
	void matchesNameAndDescriptionRegardlessOfCase() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Sundsvalls stads arkiv", "Handlingar från stadsfullmäktige", type, 1862, 1951, PUBLISHED);
		persist(2, "Fotosamlingen", "Glasplåtar", type, 1900, null, PUBLISHED);

		assertThat(findIds(NodeSpecification.matches("stads"))).containsExactly(1);
		assertThat(findIds(NodeSpecification.matches("GLASPLÅTAR"))).containsExactly(2);
	}

	@Test
	void matchesTrimsAndTreatsBlankAsNoFilter() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Sundsvalls stads arkiv", null, type, 1862, 1951, PUBLISHED);
		persist(2, "Fotosamlingen", null, type, 1900, null, PUBLISHED);

		assertThat(findIds(NodeSpecification.matches("  Fotosamlingen  "))).containsExactly(2);
		assertThat(findIds(NodeSpecification.matches("   "))).containsExactly(1, 2);
		assertThat(findIds(NodeSpecification.matches(null))).containsExactly(1, 2);
	}

	/**
	 * A name containing {@code %} must be matched literally, otherwise a user searching for it gets every row.
	 */
	@Test
	void matchesEscapesWildcards() {
		final var type = persistType(1, "Arkiv");
		persist(1, "100% komplett", null, type, null, null, PUBLISHED);
		persist(2, "Fotosamlingen", null, type, null, null, PUBLISHED);

		assertThat(findIds(NodeSpecification.matches("100%"))).containsExactly(1);
		assertThat(findIds(NodeSpecification.matches("%"))).containsExactly(1);
	}

	@Test
	void hasNodeTypeFiltersOnTheAssociation() {
		final var archive = persistType(1, "Arkiv");
		final var series = persistType(2, "Serie");
		persist(1, "Sundsvalls stads arkiv", null, archive, null, null, PUBLISHED);
		persist(2, "Protokoll", null, series, null, null, PUBLISHED);

		assertThat(findIds(NodeSpecification.hasNodeType(2))).containsExactly(2);
		assertThat(findIds(NodeSpecification.hasNodeType(null))).containsExactly(1, 2);
	}

	@Test
	void publishedKeepsOnlyRowsWithBitFourSet() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Publicerad", null, type, null, null, PUBLISHED);
		persist(2, "Dold", null, type, null, null, UNPUBLISHED);

		assertThat(findIds(NodeSpecification.published())).containsExactly(1);
	}

	/**
	 * A node whose period overlaps the requested range is kept. The legacy schema writes an unknown year as both
	 * {@code NULL} and {@code 0}, and neither ends a period: a series without a stop year is still running.
	 */
	@Test
	void activeFromKeepsOverlappingAndOpenPeriods() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Slutade före", null, type, 1800, 1850, PUBLISHED);
		persist(2, "Slutade efter", null, type, 1800, 1900, PUBLISHED);
		persist(3, "Pågår, null", null, type, 1800, null, PUBLISHED);
		persist(4, "Pågår, noll", null, type, 1800, 0, PUBLISHED);

		assertThat(findIds(NodeSpecification.activeFrom(1860))).containsExactly(2, 3, 4);
		assertThat(findIds(NodeSpecification.activeFrom(null))).containsExactly(1, 2, 3, 4);
	}

	@Test
	void activeUntilKeepsOverlappingAndOpenPeriods() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Startade efter", null, type, 1950, 1990, PUBLISHED);
		persist(2, "Startade före", null, type, 1800, 1900, PUBLISHED);
		persist(3, "Okänd start, null", null, type, null, 1900, PUBLISHED);
		persist(4, "Okänd start, noll", null, type, 0, 1900, PUBLISHED);

		assertThat(findIds(NodeSpecification.activeUntil(1900))).containsExactly(2, 3, 4);
		assertThat(findIds(NodeSpecification.activeUntil(null))).containsExactly(1, 2, 3, 4);
	}

	/**
	 * The fetch must not turn into an inner join: {@code NODETYPEID} carries no foreign key, so a node can point at a
	 * type row that does not exist, and such a node still has to show up in the result.
	 */
	@Test
	void fetchNodeTypeKeepsNodesWhoseTypeIsMissing() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Med typ", null, type, null, null, PUBLISHED);
		// NODETYPEID is NOT NULL but carries no foreign key, so the only way to have no type is to point at a row that
		// does not exist. That has to be inserted past the mapping.
		entityManager.createNativeQuery("INSERT INTO TBL_NODES (ID, NAME, NODETYPEID, `OPTIONS`, SORT, SUBITEMS, SUBITEMS_4) VALUES (2, 'Utan typ', 999, 6, 0, 0, 0)")
			.executeUpdate();
		entityManager.flush();
		entityManager.clear();

		assertThat(findIds(NodeSpecification.fetchNodeType())).containsExactly(1, 2);
	}
}
