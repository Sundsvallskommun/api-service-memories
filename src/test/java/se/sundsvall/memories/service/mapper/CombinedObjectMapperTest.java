package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class CombinedObjectMapperTest {

	private static final ReferenceResolver NULL_LOOKUP = id -> null;

	private static CombinedObjectEntity sampleEntity() {
		return CombinedObjectEntity.create()
			.withObjectKey("foto-1001")
			.withSourceId(1001)
			.withObjectType("Foto")
			.withTitle("Stadsvy")
			.withYear(1920)
			.withTopographyId(1)
			.withLocationText("Sundsvall");
	}

	@Test
	void toCombinedObjectResolvesLocation() {
		final var result = CombinedObjectMapper.toCombinedObject(sampleEntity(), "Sundsvalls kommun");

		assertThat(result).isNotNull();
		assertThat(result.getObjectKey()).isEqualTo("foto-1001");
		assertThat(result.getSourceId()).isEqualTo(1001);
		assertThat(result.getObjectType()).isEqualTo("Foto");
		assertThat(result.getTitle()).isEqualTo("Stadsvy");
		assertThat(result.getYear()).isEqualTo(1920);
		assertThat(result.getLocationText()).isEqualTo("Sundsvall");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
	}

	@Test
	void toCombinedObjectWhenNull() {
		assertThat(CombinedObjectMapper.toCombinedObject(null, "x")).isNull();
	}

	@Test
	void toCombinedObjectListResolvesViaLookup() {
		final var result = CombinedObjectMapper.toCombinedObjectList(
			List.of(sampleEntity(), CombinedObjectEntity.create().withObjectKey("text-2").withTopographyId(2)),
			id -> id == null ? null : "Loc-" + id);

		assertThat(result).hasSize(2)
			.extracting("objectKey", "location")
			.containsExactly(tuple("foto-1001", "Loc-1"), tuple("text-2", "Loc-2"));
	}

	@Test
	void toCombinedObjectListWhenNull() {
		assertThat(CombinedObjectMapper.toCombinedObjectList(null, NULL_LOOKUP)).isEqualTo(emptyList());
	}
}
