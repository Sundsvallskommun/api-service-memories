package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.api.model.ObjectTypeCount;
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
			.withLocationText("Sundsvall");
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
}
