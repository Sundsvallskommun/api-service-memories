package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.ObjectTypeCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.CategoryCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.GenderCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TopographyCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class CombinedObjectMapperTest {

	private static CombinedObjectEntity sampleEntity() {
		return CombinedObjectEntity.create()
			.withObjectKey("foto-1001")
			.withSourceId(1001)
			.withObjectType("Foto")
			.withTitle("Stadsvy")
			.withYear(1920)
			.withTopography(TopographyEntity.create().withId(1).withName("Sundsvalls kommun"))
			.withLocationText("Sundsvall")
			.withNodeId(19000);
	}

	@Test
	void toCombinedObjectResolvesLocation() {
		final var result = CombinedObjectMapper.toCombinedObject(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getObjectKey()).isEqualTo("foto-1001");
		assertThat(result.getSourceId()).isEqualTo(1001);
		assertThat(result.getObjectType()).isEqualTo("Foto");
		assertThat(result.getTitle()).isEqualTo("Stadsvy");
		assertThat(result.getYear()).isEqualTo(1920);
		assertThat(result.getTopographyId()).isEqualTo(1);
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getNodeId()).isEqualTo(19000);
	}

	/** A register row is not placed in the tree, and comes through with no node rather than a made-up one. */
	@Test
	void toCombinedObjectWithoutNode() {
		assertThat(CombinedObjectMapper.toCombinedObject(CombinedObjectEntity.create().withObjectKey("person-1")).getNodeId()).isNull();
	}

	/**
	 * The association is null both when the object has no place and when {@code TOPOGRAPHY_ID} points at a row that does
	 * not exist, so both the id and the resolved name must come out null.
	 */
	@Test
	void toCombinedObjectWithoutTopography() {
		final var result = CombinedObjectMapper.toCombinedObject(CombinedObjectEntity.create().withObjectKey("text-2"));

		assertThat(result.getTopographyId()).isNull();
		assertThat(result.getLocation()).isNull();
	}

	@Test
	void toCombinedObjectWhenNull() {
		assertThat(CombinedObjectMapper.toCombinedObject(null)).isNull();
	}

	@Test
	void toCombinedObjectList() {
		final var result = CombinedObjectMapper.toCombinedObjectList(
			List.of(sampleEntity(), CombinedObjectEntity.create().withObjectKey("text-2")
				.withTopography(TopographyEntity.create().withId(2).withName("Timrå"))));

		assertThat(result).hasSize(2)
			.extracting("objectKey", "location")
			.containsExactly(tuple("foto-1001", "Sundsvalls kommun"), tuple("text-2", "Timrå"));
	}

	@Test
	void toCombinedObjectListWhenNull() {
		assertThat(CombinedObjectMapper.toCombinedObjectList(null)).isEqualTo(emptyList());
	}

	@Test
	void toObjectTypeCountList() {
		final var result = CombinedObjectMapper.toObjectTypeCountList(List.of(new TypeCount("Foto", 12L), new TypeCount("Sjöman", 2L)));

		assertThat(result).extracting(ObjectTypeCount::getObjectType, ObjectTypeCount::getCount)
			.containsExactly(tuple("Foto", 12L), tuple("Sjöman", 2L));
	}

	@Test
	void toObjectTypeCountWhenNull() {
		assertThat(CombinedObjectMapper.toObjectTypeCount(null)).isNull();
	}

	@Test
	void toObjectTypeCountListWhenNull() {
		assertThat(CombinedObjectMapper.toObjectTypeCountList(null)).isEqualTo(emptyList());
	}

	@Test
	void toGenderCountList() {
		final var result = CombinedObjectMapper.toGenderCountList(List.of(new GenderCount("kvinna", 3L), new GenderCount("man", 12L)));

		assertThat(result).extracting(se.sundsvall.memories.api.model.GenderCount::getGender, se.sundsvall.memories.api.model.GenderCount::getCount)
			.containsExactly(tuple("kvinna", 3L), tuple("man", 12L));
	}

	@Test
	void toGenderCountWhenNull() {
		assertThat(CombinedObjectMapper.toGenderCount(null)).isNull();
	}

	@Test
	void toGenderCountListWhenNull() {
		assertThat(CombinedObjectMapper.toGenderCountList(null)).isEqualTo(emptyList());
	}

	@Test
	void toCategoryCountList() {
		final var result = CombinedObjectMapper.toCategoryCountList(List.of(new CategoryCount(2, "Aktiebolag", 3L), new CategoryCount(5, "Kommitté", 12L)));

		assertThat(result).extracting(se.sundsvall.memories.api.model.CategoryCount::getCategoryId, se.sundsvall.memories.api.model.CategoryCount::getName,
			se.sundsvall.memories.api.model.CategoryCount::getCount)
			.containsExactly(tuple(2, "Aktiebolag", 3L), tuple(5, "Kommitté", 12L));
	}

	/**
	 * The chips are ordered where the response is shaped, not by the database's collation: Å, Ä and Ö sort after Z
	 * rather than as A and O, so Föremål follows Foto and not the other way round, and categories sharing a name keep a
	 * stable order by id. The same order {@code /categories} lists them in.
	 */
	@Test
	void countListsAreOrderedInSwedish() {
		assertThat(CombinedObjectMapper.toCategoryCountList(List.of(
			new CategoryCount(2, "Övrigt", 1L),
			new CategoryCount(5, "Zonkontor", 1L),
			new CategoryCount(9, "Ångbåtsbolag", 1L),
			new CategoryCount(7, "Aktiebolag", 1L))))
			.extracting(se.sundsvall.memories.api.model.CategoryCount::getName)
			.containsExactly("Aktiebolag", "Zonkontor", "Ångbåtsbolag", "Övrigt");

		assertThat(CombinedObjectMapper.toObjectTypeCountList(List.of(
			new TypeCount("Föremål", 1L), new TypeCount("Text", 1L), new TypeCount("Foto", 1L))))
			.extracting(ObjectTypeCount::getObjectType)
			.containsExactly("Foto", "Föremål", "Text");

		assertThat(CombinedObjectMapper.toGenderCountList(List.of(
			new GenderCount("Okänt", 1L), new GenderCount("Man", 1L), new GenderCount("Kvinna", 1L))))
			.extracting(se.sundsvall.memories.api.model.GenderCount::getGender)
			.containsExactly("Kvinna", "Man", "Okänt");
	}

	@Test
	void toCategoryCountWhenNull() {
		assertThat(CombinedObjectMapper.toCategoryCount(null)).isNull();
	}

	@Test
	void toCategoryCountListWhenNull() {
		assertThat(CombinedObjectMapper.toCategoryCountList(null)).isEqualTo(emptyList());
	}

	/** The chip is labelled the way /topographies labels the place, so a place with only a parish shows that alone. */
	@Test
	void toTopographyCountBuildsTheDisplayName() {
		final var result = CombinedObjectMapper.toTopographyCount(new TopographyCount(4, "Njurunda", "Kvissleby", 12L));

		assertThat(result.getTopographyId()).isEqualTo(4);
		assertThat(result.getName()).isEqualTo("Kvissleby, Njurunda");
		assertThat(result.getCount()).isEqualTo(12L);
		assertThat(CombinedObjectMapper.toTopographyCount(new TopographyCount(1, "Sundsvall", " ", 1L)).getName()).isEqualTo("Sundsvall");
		assertThat(CombinedObjectMapper.toTopographyCount(null)).isNull();
	}

	/** Ordered by the label in Swedish order, ties by id — the order /topographies lists the same names in. */
	@Test
	void toTopographyCountListSortsByDisplayNameInSwedishOrder() {
		assertThat(CombinedObjectMapper.toTopographyCountList(List.of(
			new TopographyCount(3, "Örnsköldsvik", null, 1L),
			new TopographyCount(2, "Timrå", "Söråker", 1L),
			new TopographyCount(9, "Sundsvall", null, 1L),
			new TopographyCount(4, "Njurunda", "Kvissleby", 1L),
			new TopographyCount(1, "Sundsvall", null, 1L))))
			.extracting(se.sundsvall.memories.api.model.TopographyCount::getTopographyId, se.sundsvall.memories.api.model.TopographyCount::getName)
			.containsExactly(tuple(4, "Kvissleby, Njurunda"), tuple(1, "Sundsvall"), tuple(9, "Sundsvall"), tuple(2, "Söråker, Timrå"), tuple(3, "Örnsköldsvik"));
		assertThat(CombinedObjectMapper.toTopographyCountList(null)).isEqualTo(emptyList());
	}
}
