package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.integration.db.NodeRepository;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.NodeAttributesEntity;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.time.Month.MARCH;
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
		entityManager.createNativeQuery("DELETE FROM TBL_NODEATTRIBUTES").executeUpdate();
		nodeRepository.deleteAll();
		nodeRepository.flush();
		entityManager.createNativeQuery("DELETE FROM INSTITUTION").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM TOPOGRAFI").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM JURPERS").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM PERSON").executeUpdate();
		entityManager.createNativeQuery("DELETE FROM KATEGORI").executeUpdate();
	}

	private <T> T persistLookup(final T lookup) {
		entityManager.persist(lookup);
		entityManager.flush();
		return lookup;
	}

	/**
	 * The attribute row shares the node's id and is written after the node. The persistence context is cleared so the
	 * node under test is read back with its attributes joined rather than served from memory.
	 */
	private void persistAttributes(final NodeAttributesEntity attributes) {
		entityManager.persist(attributes);
		entityManager.flush();
		entityManager.clear();
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

	/**
	 * Deletion sets DELETEDDATE but leaves the published bit set, so the published filter alone does not hide the row.
	 */
	@Test
	void notDeletedExcludesSoftDeletedRows() {
		final var type = persistType(1, "Arkiv");
		persist(1, "Kvar", null, type, null, null, PUBLISHED);
		persist(2, "Raderad", null, type, null, null, PUBLISHED);
		nodeRepository.findById(2).ifPresent(node -> nodeRepository.saveAndFlush(node.withDeletedDate(LocalDate.of(2024, MARCH, 1))));

		assertThat(findIds(NodeSpecification.notDeleted())).containsExactly(1);
		assertThat(findIds(NodeSpecification.published())).containsExactly(1, 2);
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

	/** The type is matched by name regardless of case, and a blank name means no filter. */
	@Test
	void hasNodeTypeNameFiltersOnTheTypesName() {
		final var archive = persistType(2, "Arkiv");
		final var series = persistType(3, "Serie");
		persist(1, "Sundsvalls stads arkiv", null, archive, null, null, PUBLISHED);
		persist(2, "Protokoll", null, series, null, null, PUBLISHED);

		assertThat(findIds(NodeSpecification.hasNodeTypeName("arkiv"))).containsExactly(1);
		assertThat(findIds(NodeSpecification.hasNodeTypeName(" Serie "))).containsExactly(2);
		assertThat(findIds(NodeSpecification.hasNodeTypeName("Volym"))).isEmpty();
		assertThat(findIds(NodeSpecification.hasNodeTypeName("  "))).containsExactly(1, 2);
		assertThat(findIds(NodeSpecification.hasNodeTypeName(null))).containsExactly(1, 2);
	}

	/** The lookup filters read the attribute row; a node without one, or without that lookup, simply does not match. */
	@Test
	void lookupFiltersReadTheAttributeRow() {
		final var type = persistType(1, "Arkiv");
		final var museum = persistLookup(InstitutionEntity.create().withId(3).withName("Sundsvalls museum"));
		final var company = persistLookup(CategoryEntity.create().withCategoryId(5).withName("Företag"));
		final var njurunda = persistLookup(TopographyEntity.create().withId(4).withName("Njurunda").withPlace("Kvissleby"));
		persist(1, "Med allt", null, type, null, null, PUBLISHED);
		persist(2, "Med inget", null, type, null, null, PUBLISHED);
		persist(3, "Utan rad", null, type, null, null, PUBLISHED);
		persistAttributes(NodeAttributesEntity.create().withNodeId(1).withInstitution(museum).withCategory(company).withTopography(njurunda));
		persistAttributes(NodeAttributesEntity.create().withNodeId(2));

		assertThat(findIds(NodeSpecification.hasInstitution(List.of(3)))).containsExactly(1);
		assertThat(findIds(NodeSpecification.hasInstitution(List.of(99)))).isEmpty();
		assertThat(findIds(NodeSpecification.hasInstitution(null))).containsExactly(1, 2, 3);
		assertThat(findIds(NodeSpecification.hasCategory(List.of(5, 7)))).containsExactly(1);
		assertThat(findIds(NodeSpecification.hasCategory(List.of()))).containsExactly(1, 2, 3);
		assertThat(findIds(NodeSpecification.hasTopography(List.of(4)))).containsExactly(1);
		assertThat(findIds(NodeSpecification.hasTopography(List.of(1)))).isEmpty();
	}

	/**
	 * The sentinel category every legal entity defaults to is not a category: naming it alone matches nothing rather
	 * than every node whose category was never set, and named with a real one it adds nothing.
	 */
	@Test
	void hasCategoryNeverMatchesTheSentinel() {
		final var type = persistType(1, "Arkiv");
		final var sentinel = persistLookup(CategoryEntity.create().withCategoryId(CategorySpecification.PLACEHOLDER_ID).withName(""));
		final var company = persistLookup(CategoryEntity.create().withCategoryId(5).withName("Företag"));
		persist(1, "Utan kategori", null, type, null, null, PUBLISHED);
		persist(2, "Företaget", null, type, null, null, PUBLISHED);
		persistAttributes(NodeAttributesEntity.create().withNodeId(1).withCategory(sentinel));
		persistAttributes(NodeAttributesEntity.create().withNodeId(2).withCategory(company));

		assertThat(findIds(NodeSpecification.hasCategory(List.of(CategorySpecification.PLACEHOLDER_ID)))).isEmpty();
		assertThat(findIds(NodeSpecification.hasCategory(List.of(CategorySpecification.PLACEHOLDER_ID, 5)))).containsExactly(2);
	}

	/** The place is matched through the topography, on either of its name columns, or through the free-text place. */
	@Test
	void matchesLocationReadsTheTopographyAndTheFreeText() {
		final var type = persistType(1, "Arkiv");
		final var njurunda = persistLookup(TopographyEntity.create().withId(4).withName("Njurunda").withPlace("Kvissleby"));
		persist(1, "Med topografi", null, type, null, null, PUBLISHED);
		persist(2, "Med fritext", null, type, null, null, PUBLISHED);
		persist(3, "Utan plats", null, type, null, null, PUBLISHED);
		persistAttributes(NodeAttributesEntity.create().withNodeId(1).withTopography(njurunda));
		persistAttributes(NodeAttributesEntity.create().withNodeId(2).withLocationText("Obestämd by i Njurunda"));

		assertThat(findIds(NodeSpecification.matchesLocation("njurunda"))).containsExactly(1, 2);
		assertThat(findIds(NodeSpecification.matchesLocation("Kvissle"))).containsExactly(1);
		assertThat(findIds(NodeSpecification.matchesLocation("obestämd"))).containsExactly(2);
		assertThat(findIds(NodeSpecification.matchesLocation("  "))).containsExactly(1, 2, 3);
	}

	/**
	 * An archive named after its arkivbildare has an empty name of its own, so the free text has to reach the legal
	 * entity and the person the attribute row names — but not the sentinel rows, whose name would otherwise match every
	 * node pointed at them.
	 */
	@Test
	void matchesReachesTheCreatorButNotTheSentinels() {
		final var type = persistType(1, "Arkiv");
		final var company = persistLookup(LegalEntityEntity.create().withLegalEntityId(10).withName("Galtströms Bruk").withAlternativeNames("Galtström").withOptions(PUBLISHED));
		final var sentinel = persistLookup(LegalEntityEntity.create().withLegalEntityId(LegalEntitySpecification.PLACEHOLDER_ID).withName("Ingen").withOptions(PUBLISHED));
		final var person = persistLookup(PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin").withOptions(PUBLISHED));
		persist(1, "", null, type, null, null, PUBLISHED);
		persist(2, "", null, type, null, null, PUBLISHED);
		persist(3, "Ingen arkivbildare", null, type, null, null, PUBLISHED);
		persistAttributes(NodeAttributesEntity.create().withNodeId(1).withLegalEntity(company));
		persistAttributes(NodeAttributesEntity.create().withNodeId(2).withPerson(person).withLegalEntity(sentinel));

		assertThat(findIds(NodeSpecification.matches("bruk"))).containsExactly(1);
		assertThat(findIds(NodeSpecification.matches("galtström"))).containsExactly(1);
		assertThat(findIds(NodeSpecification.matches("Anton Nordin"))).containsExactly(2);
		assertThat(findIds(NodeSpecification.matches("ingen"))).containsExactly(3);
	}

	/**
	 * The attribute row and its lookups arrive with the node, and the fetch is a left one: a node without a row, or
	 * with a row whose foreign key points at nothing, still shows up.
	 */
	@Test
	void fetchAttributesKeepsNodesWithoutARowAndLoadsTheLookups() {
		final var type = persistType(1, "Arkiv");
		final var museum = persistLookup(InstitutionEntity.create().withId(3).withName("Sundsvalls museum"));
		persist(1, "Med rad", null, type, null, null, PUBLISHED);
		persist(2, "Utan rad", null, type, null, null, PUBLISHED);
		persistAttributes(NodeAttributesEntity.create().withNodeId(1).withInstitution(museum));
		entityManager.createNativeQuery("INSERT INTO TBL_NODEATTRIBUTES (NODEID, FIELD3, SUBITEMS) VALUES (2, 999, 0)").executeUpdate();
		entityManager.flush();
		entityManager.clear();

		final var nodes = nodeRepository.findAll(NodeSpecification.fetchAttributes(), Pageable.unpaged()).getContent();

		assertThat(nodes).extracting(NodeEntity::getId).containsExactlyInAnyOrder(1, 2);
		assertThat(nodes.stream().filter(node -> node.getId() == 1).findFirst().orElseThrow().getAttributes().getInstitution().getName())
			.isEqualTo("Sundsvalls museum");
		assertThat(nodes.stream().filter(node -> node.getId() == 2).findFirst().orElseThrow().getAttributes().getInstitution()).isNull();
	}

	/**
	 * What the location sort key is translated into has to resolve as a path, and Spring Data has to join it with a
	 * left join, so a node without a place is kept — and put last, in either direction, rather than where the database
	 * would put an empty value.
	 */
	@Test
	void locationSortPathsResolveAndKeepNodesWithoutAPlace() {
		final var type = persistType(1, "Arkiv");
		final var timra = persistLookup(TopographyEntity.create().withId(2).withName("Timrå").withPlace("Söråker"));
		final var njurunda = persistLookup(TopographyEntity.create().withId(4).withName("Njurunda").withPlace("Kvissleby"));
		persist(1, "Timrå-arkivet", null, type, null, null, PUBLISHED);
		persist(2, "Njurunda-arkivet", null, type, null, null, PUBLISHED);
		persist(3, "Platslöst", null, type, null, null, PUBLISHED);
		persistAttributes(NodeAttributesEntity.create().withNodeId(1).withTopography(timra));
		persistAttributes(NodeAttributesEntity.create().withNodeId(2).withTopography(njurunda));

		assertThat(orderedBy(Sort.Direction.ASC)).containsExactly(2, 1, 3);
		assertThat(orderedBy(Sort.Direction.DESC)).containsExactly(1, 2, 3);
	}

	private List<Integer> orderedBy(final Sort.Direction direction) {
		final var sort = Sort.by(
			new Sort.Order(direction, "attributes.topography.place", Sort.NullHandling.NULLS_LAST),
			new Sort.Order(direction, "attributes.topography.name", Sort.NullHandling.NULLS_LAST)).and(Sort.by("id"));
		return nodeRepository.findAll(NodeSpecification.fetchAttributes(), PageRequest.of(0, 10, sort)).getContent().stream()
			.map(NodeEntity::getId)
			.toList();
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
