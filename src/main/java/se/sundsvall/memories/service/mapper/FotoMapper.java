package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.function.Function;
import se.sundsvall.memories.api.model.Foto;
import se.sundsvall.memories.integration.db.model.FotoEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class FotoMapper {

	private FotoMapper() {}

	/**
	 * Map a single FotoEntity to a Foto API model with the resolved place name {@code plats}.
	 *
	 * @param  entity the source entity
	 * @param  plats  the topografi-resolved place name (nullable)
	 * @return        the mapped {@link Foto}, or {@code null} if {@code entity} is null
	 */
	public static Foto toFoto(final FotoEntity entity, final String plats) {
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
				.withPlats(plats)
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
				.withFilStor(e.getFilStor()))
			.orElse(null);
	}

	/**
	 * Map a list of FotoEntities, resolving each entity's plats via the provided lookup.
	 *
	 * @param  entities    source entities
	 * @param  platsLookup function from fotoTId → resolved plats string (nullable)
	 * @return             list of mapped {@link Foto}, empty if entities is null
	 */
	public static List<Foto> toFotoList(final List<FotoEntity> entities, final Function<Integer, String> platsLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toFoto(e, platsLookup.apply(e.getFotoTId())))
				.toList())
			.orElse(emptyList());
	}
}
