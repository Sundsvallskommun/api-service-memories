package se.sundsvall.memories.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.memories.integration.db.model.FotoEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class FotoMapperTest {

	private static FotoEntity sampleEntity() {
		return FotoEntity.create()
			.withFotoId(1234)
			.withDoktitel("Stadsvy från Norra berget")
			.withTidig("1920")
			.withSenast("1925")
			.withFotoOplats("Sundsvall")
			.withFilLiten("FOTO.id_1234_fil_liten.jpg")
			.withFilStor("FOTO.id_1234_fil_stor.jpg")
			.withFilOriginal("FOTO.id_1234_fil_original.jpg")
			.withGivRattigh("Free use")
			.withGivForbeh("Nej")
			.withOptions(4);
	}

	@Test
	void toFoto() {
		final var result = FotoMapper.toFoto(sampleEntity());

		assertThat(result).isNotNull();
		assertThat(result.getFotoId()).isEqualTo(1234);
		assertThat(result.getDoktitel()).isEqualTo("Stadsvy från Norra berget");
		assertThat(result.getFotoOplats()).isEqualTo("Sundsvall");
		assertThat(result.getFilStor()).isEqualTo("FOTO.id_1234_fil_stor.jpg");
		assertThat(result.getGivRattigh()).isEqualTo("Free use");
	}

	@Test
	void toFotoWithNullEntityReturnsNull() {
		assertThat(FotoMapper.toFoto(null)).isNull();
	}

	@Test
	void toFotoListMapsAllEntities() {
		final var entities = List.of(
			FotoEntity.create().withFotoId(1).withDoktitel("A"),
			FotoEntity.create().withFotoId(2).withDoktitel("B"));

		final var result = FotoMapper.toFotoList(entities);

		assertThat(result)
			.extracting("fotoId", "doktitel")
			.containsExactly(tuple(1, "A"), tuple(2, "B"));
	}

	@Test
	void toFotoListWithNullReturnsEmpty() {
		assertThat(FotoMapper.toFotoList(null)).isEmpty();
	}
}
