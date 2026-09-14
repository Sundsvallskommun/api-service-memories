package se.sundsvall.memories.service.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.NodeAttributesEntity;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class NodeMapperTest {

	private static NodeEntity sampleEntity() {
		return NodeEntity.create()
			.withId(100)
			.withParentId(10)
			.withName("Sundsvalls stads arkiv")
			.withNodeType(NodeTypeEntity.create().withId(1).withName("Arkiv"))
			.withStartYear(1862)
			.withStopYear(1951)
			.withDescription("Handlingar från stadsfullmäktige")
			.withSortOrder(10)
			.withSubItemCount(42)
			.withPublishedSubItemCount(40)
			.withOptions(6);
	}

	private static NodeAttributesEntity sampleAttributes() {
		return NodeAttributesEntity.create()
			.withNodeId(100)
			.withLegalEntity(LegalEntityEntity.create().withLegalEntityId(10).withName("Galtströms Bruk").withStartDate("1673").withEndDate("1916"))
			.withInstitution(InstitutionEntity.create().withId(3).withName("Sundsvalls museum").withCode("SVM"))
			.withCategory(CategoryEntity.create().withCategoryId(5).withName("Företag"))
			.withTopography(TopographyEntity.create().withId(4).withName("Njurunda").withPlace("Kvissleby"))
			.withLocationText("Okänd by")
			.withSubject(OcmEntity.create().withId(20).withCode("MUS").withText("Musik").withDescription("Musikinspelning"))
			.withSeriesSignum("A1")
			.withOldSeriesSignum("A I")
			.withVolumeNumber("001")
			.withVolumeCount(3)
			.withShelfMeters(new BigDecimal("12.50"))
			.withVolumePlacement("Hylla 3")
			.withAccessionNumber("ACC-1862")
			.withHoldingsCode("B1")
			.withHistoryFilename("arkiv_100_historik.xml");
	}

	@Test
	void toNode() {
		final var result = NodeMapper.toNode(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(100);
		assertThat(result.getParentId()).isEqualTo(10);
		assertThat(result.getName()).isEqualTo("Sundsvalls stads arkiv");
		assertThat(result.getNodeTypeId()).isEqualTo(1);
		assertThat(result.getNodeType()).isEqualTo("Arkiv");
		assertThat(result.getStartYear()).isEqualTo(1862);
		assertThat(result.getStopYear()).isEqualTo(1951);
		assertThat(result.getDescription()).isEqualTo("Handlingar från stadsfullmäktige");
		assertThat(result.getSortOrder()).isEqualTo(10);
		assertThat(result.getSubItemCount()).isEqualTo(42);
		assertThat(result.getPublishedSubItemCount()).isEqualTo(40);
		assertThat(result.getOptions()).isEqualTo(6);
		// nothing recorded beyond the tree: every attribute field is absent rather than defaulted
		assertThat(result).hasAllNullFieldsOrPropertiesExcept("id", "parentId", "name", "nodeTypeId", "nodeType", "startYear", "stopYear", "description",
			"sortOrder", "subItemCount", "publishedSubItemCount", "options");
	}

	@Test
	void toNodeMapsTheAttributeRow() {
		final var result = NodeMapper.toNode(sampleEntity().withAttributes(sampleAttributes()));

		assertThat(result.getName()).isEqualTo("Sundsvalls stads arkiv");
		assertThat(result.getCreator().getLegalEntityId()).isEqualTo(10);
		assertThat(result.getCreator().getLegalEntity()).isEqualTo("Galtströms Bruk");
		assertThat(result.getCreator().getPersonId()).isNull();
		assertThat(result.getActivityStartDate()).isEqualTo("1673");
		assertThat(result.getActivityEndDate()).isEqualTo("1916");
		assertThat(result.getInstitutionId()).isEqualTo(3);
		assertThat(result.getInstitution()).isEqualTo("Sundsvalls museum");
		assertThat(result.getInstitutionCode()).isEqualTo("SVM");
		assertThat(result.getCategoryId()).isEqualTo(5);
		assertThat(result.getCategory()).isEqualTo("Företag");
		assertThat(result.getTopographyId()).isEqualTo(4);
		assertThat(result.getLocation()).isEqualTo("Kvissleby, Njurunda");
		assertThat(result.getLocationText()).isEqualTo("Okänd by");
		assertThat(result.getSubject().getCode()).isEqualTo("MUS");
		assertThat(result.getSubject().getText()).isEqualTo("Musik");
		assertThat(result.getSubject().getDescription()).isEqualTo("Musikinspelning");
		assertThat(result.getSeriesSignum()).isEqualTo("A1");
		assertThat(result.getOldSeriesSignum()).isEqualTo("A I");
		assertThat(result.getVolumeNumber()).isEqualTo("001");
		assertThat(result.getVolumeCount()).isEqualTo(3);
		assertThat(result.getShelfMeters()).isEqualByComparingTo("12.50");
		assertThat(result.getVolumePlacement()).isEqualTo("Hylla 3");
		assertThat(result.getAccessionNumber()).isEqualTo("ACC-1862");
		assertThat(result.getHoldingsCode()).isEqualTo("B1");
		assertThat(result.getHistoryFilename()).isEqualTo("arkiv_100_historik.xml");
	}

	/**
	 * The lookups the attribute row names may be missing — a dangling foreign key arrives as a null association — and
	 * the fields read from them are then absent, not the row's other values.
	 */
	@Test
	void toNodeWithAttributesButNoLookups() {
		final var result = NodeMapper.toNode(sampleEntity().withAttributes(NodeAttributesEntity.create().withNodeId(100).withSeriesSignum("A1")));

		assertThat(result.getSeriesSignum()).isEqualTo("A1");
		assertThat(result.getCreator()).isNull();
		assertThat(result.getActivityStartDate()).isNull();
		assertThat(result.getInstitutionId()).isNull();
		assertThat(result.getCategory()).isNull();
		assertThat(result.getLocation()).isNull();
		assertThat(result.getSubject()).isNull();
	}

	/**
	 * An archive named after its arkivbildare has an empty name of its own. The archive's view falls back to the legal
	 * entity, then to the person as "Efternamn, Förnamn", and so does the mapper — a blank name counts as none, since
	 * the legacy data uses empty strings rather than NULL, and a person with one name gets that name alone.
	 */
	@Test
	void toNodeNamesANamelessArchiveAfterItsCreator() {
		final var legalEntity = LegalEntityEntity.create().withLegalEntityId(10).withName("Galtströms Bruk");
		final var person = PersonEntity.create().withPersonId(1).withFirstName("Anton").withLastName("Nordin");

		assertThat(NodeMapper.toNode(sampleEntity().withName("").withAttributes(NodeAttributesEntity.create().withLegalEntity(legalEntity).withPerson(person))).getName())
			.isEqualTo("Galtströms Bruk");
		assertThat(NodeMapper.toNode(sampleEntity().withName(" ").withAttributes(NodeAttributesEntity.create().withPerson(person))).getName())
			.isEqualTo("Nordin, Anton");
		assertThat(NodeMapper.toNode(sampleEntity().withName("").withAttributes(NodeAttributesEntity.create().withPerson(PersonEntity.create().withPersonId(2).withLastName("Berg ")))).getName())
			.isEqualTo("Berg");
		assertThat(NodeMapper.toNode(sampleEntity().withName(null).withAttributes(NodeAttributesEntity.create())).getName()).isNull();
		assertThat(NodeMapper.toNode(sampleEntity().withName(null)).getName()).isNull();
	}

	/**
	 * The sentinel rows the legacy foreign keys default to, and a soft-deleted arkivbildare, lend the node neither a
	 * name nor an activity period — the same rule the creator of an object follows.
	 */
	@Test
	void toNodeSkipsASentinelOrDeletedCreator() {
		final var sentinel = LegalEntityEntity.create().withLegalEntityId(1).withName("Ingen").withStartDate("1900");
		final var deleted = LegalEntityEntity.create().withLegalEntityId(10).withName("Raderad").withStartDate("1900").withDeletedDate(LocalDate.of(2024, 3, 1));
		final var sentinelPerson = PersonEntity.create().withPersonId(0).withLastName("Ingen");

		for (final var legalEntity : List.of(sentinel, deleted)) {
			final var result = NodeMapper.toNode(sampleEntity().withName("").withAttributes(NodeAttributesEntity.create().withLegalEntity(legalEntity).withPerson(sentinelPerson)));

			assertThat(result.getName()).isNull();
			assertThat(result.getCreator()).isNull();
			assertThat(result.getActivityStartDate()).isNull();
		}
	}

	/**
	 * NODETYPEID is declared NOT NULL, but it can still point at a type row that no longer exists — the schema carries
	 * no foreign key. Both the id and the name are then read as absent rather than blowing up.
	 */
	@Test
	void toNodeWithoutNodeType() {
		final var result = NodeMapper.toNode(NodeEntity.create().withId(100).withName("Lös nod"));

		assertThat(result).isNotNull();
		assertThat(result.getNodeTypeId()).isNull();
		assertThat(result.getNodeType()).isNull();
	}

	@Test
	void toNodeWhenNull() {
		assertThat(NodeMapper.toNode(null)).isNull();
	}

	@Test
	void toNodeList() {
		final var result = NodeMapper.toNodeList(List.of(sampleEntity(), NodeEntity.create().withId(200).withName("Fotosamlingen")));

		assertThat(result).hasSize(2)
			.extracting("id", "name")
			.containsExactly(tuple(100, "Sundsvalls stads arkiv"), tuple(200, "Fotosamlingen"));
	}

	@Test
	void toNodeListWhenNull() {
		assertThat(NodeMapper.toNodeList(null)).isEqualTo(emptyList());
	}

	@Test
	void toNodeDetail() {
		final var ancestors = List.of(
			NodeEntity.create().withId(100).withName("Sundsvalls stads arkiv"),
			NodeEntity.create().withId(110).withName("Protokoll"));

		final var result = NodeMapper.toNodeDetail(sampleEntity(), ancestors);

		assertThat(result).isNotNull();
		assertThat(result.getNode().getId()).isEqualTo(100);
		assertThat(result.getPath())
			.extracting("id", "name")
			.containsExactly(tuple(100, "Sundsvalls stads arkiv"), tuple(110, "Protokoll"));
	}

	/**
	 * A root node has nothing above it, which is an empty path rather than a missing one.
	 */
	@Test
	void toNodeDetailWithoutAncestors() {
		final var result = NodeMapper.toNodeDetail(sampleEntity(), null);

		assertThat(result).isNotNull();
		assertThat(result.getPath()).isEqualTo(emptyList());
	}

	@Test
	void toNodeDetailWhenNull() {
		assertThat(NodeMapper.toNodeDetail(null, List.of())).isNull();
	}
}
