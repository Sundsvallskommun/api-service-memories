package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.FotoEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class FotoMapperTest {

	private static final Function<Integer, String> NULL_LOOKUP = id -> null;

	private static FotoEntity sampleEntity() {
		return FotoEntity.create()
			.withFotoId(1234)
			.withFotoTId(42)
			.withDoktitel("Stadsvy från Norra berget")
			.withTidig("1920")
			.withSenast("1925")
			.withFotoOplats("Sundsvall")
			.withFilLiten("FOTO.id_1234_fil_liten.jpg")
			.withFilStor("FOTO.id_1234_fil_stor.jpg")
			.withGivRattigh("Free use")
			.withGivForbeh("Nej")
			.withOptions(4);
	}

	@Test
	void toFoto() {
		final var result = FotoMapper.toFoto(sampleEntity(), "Sundsvall");

		assertThat(result).isNotNull();
		assertThat(result.getFotoId()).isEqualTo(1234);
		assertThat(result.getDoktitel()).isEqualTo("Stadsvy från Norra berget");
		assertThat(result.getFotoOplats()).isEqualTo("Sundsvall");
		assertThat(result.getPlats()).isEqualTo("Sundsvall");
		assertThat(result.getFilStor()).isEqualTo("FOTO.id_1234_fil_stor.jpg");
		assertThat(result.getGivRattigh()).isEqualTo("Free use");
	}

	@Test
	void toFotoWithNullPlatsLeavesFieldNull() {
		final var result = FotoMapper.toFoto(sampleEntity(), null);

		assertThat(result).isNotNull();
		assertThat(result.getPlats()).isNull();
	}

	@Test
	void toFotoWithNullEntityReturnsNull() {
		assertThat(FotoMapper.toFoto(null, "ignored")).isNull();
	}

	@Test
	void toFotoListMapsAllEntitiesWithResolvedPlats() {
		final var entities = List.of(
			FotoEntity.create().withFotoId(1).withFotoTId(10).withDoktitel("A"),
			FotoEntity.create().withFotoId(2).withFotoTId(20).withDoktitel("B"));
		final Function<Integer, String> lookup = id -> id == 10 ? "Sundsvall" : "Timrå";

		final var result = FotoMapper.toFotoList(entities, lookup);

		assertThat(result)
			.extracting("fotoId", "doktitel", "plats")
			.containsExactly(tuple(1, "A", "Sundsvall"), tuple(2, "B", "Timrå"));
	}

	@Test
	void toFotoListWithNullReturnsEmpty() {
		assertThat(FotoMapper.toFotoList(null, NULL_LOOKUP)).isEmpty();
	}
}
