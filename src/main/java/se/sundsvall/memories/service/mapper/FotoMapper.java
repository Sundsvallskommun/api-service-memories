package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Foto;
import se.sundsvall.memories.integration.db.model.FotoEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class FotoMapper {

	private FotoMapper() {}

	public static Foto toFoto(final FotoEntity entity) {
		return ofNullable(entity)
			.map(e -> Foto.create()
				.withFotoId(e.getFotoId())
				.withFilnamn(e.getFilnamn())
				.withAccNr(e.getAccNr())
				.withRefKod(e.getRefKod())
				.withInventNr(e.getInventNr())
				.withTidigNr(e.getTidigNr())
				.withDoktitel(e.getDoktitel())
				.withSakord(e.getSakord())
				.withKommentFf(e.getKommentFf())
				.withTidig(e.getTidig())
				.withSenast(e.getSenast())
				.withObsdatum(e.getObsdatum())
				.withFotoOplats(e.getFotoOplats())
				.withForplats(e.getForplats())
				.withObjtyp(e.getObjtyp())
				.withSvvitfarg(e.getSvvitfarg())
				.withNegPos(e.getNegPos())
				.withGenPas(e.getGenPas())
				.withBildbar(e.getBildbar())
				.withMaterial(e.getMaterial())
				.withTeknik(e.getTeknik())
				.withFunktion(e.getFunktion())
				.withHojd(e.getHojd())
				.withBredd(e.getBredd())
				.withDiam(e.getDiam())
				.withRam(e.getRam())
				.withTillSkat(e.getTillSkat())
				.withTillstand(e.getTillstand())
				.withObsnamn(e.getObsnamn())
				.withAtgard(e.getAtgard())
				.withAtdDatum(e.getAtdDatum())
				.withSign(e.getSign())
				.withGivRattigh(e.getGivRattigh())
				.withGivForbeh(e.getGivForbeh())
				.withAnvando(e.getAnvando())
				.withKommentUpph(e.getKommentUpph())
				.withFilLiten(e.getFilLiten())
				.withFilStor(e.getFilStor())
				.withFilOriginal(e.getFilOriginal()))
			.orElse(null);
	}

	public static List<Foto> toFotoList(final List<FotoEntity> entities) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(FotoMapper::toFoto)
				.toList())
			.orElse(emptyList());
	}
}
