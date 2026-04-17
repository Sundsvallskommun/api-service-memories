package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.function.Function;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.integration.db.model.PublEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PublicationMapper {

	private PublicationMapper() {}

	/** Summary mapping (no XMLTEXT) used for list responses. */
	public static Publication toPublicationSummary(final PublEntity entity, final String plats) {
		return toBase(entity, plats);
	}

	/** Detail mapping including XMLTEXT, used for get-by-id. */
	public static Publication toPublication(final PublEntity entity, final String plats) {
		return ofNullable(toBase(entity, plats))
			.map(publication -> publication.withXmltext(entity.getXmltext()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link PublEntity} to summary {@link Publication}s, resolving each entity's plats via the lookup.
	 *
	 * @param  entities    source entities
	 * @param  platsLookup function from ptId → resolved plats string (nullable)
	 * @return             list of mapped publications (empty if entities is null)
	 */
	public static List<Publication> toPublicationList(final List<PublEntity> entities, final Function<Integer, String> platsLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toPublicationSummary(e, platsLookup.apply(e.getPtId())))
				.toList())
			.orElse(emptyList());
	}

	private static Publication toBase(final PublEntity entity, final String plats) {
		return ofNullable(entity)
			.map(e -> Publication.create()
				.withPublId(e.getPublId())
				.withFilnamn(e.getFilnamn())
				.withPubliktyp(e.getPubliktyp())
				.withDatum(e.getDatum())
				.withTidtitel(e.getTidtitel())
				.withTidnr(e.getTidnr())
				.withTidsida(e.getTidsida())
				.withForlagOplats(e.getForlagOplats())
				.withDoktitel(e.getDoktitel())
				.withPubOplats(e.getPubOplats())
				.withPlats(plats)
				.withKommentPubl(e.getKommentPubl())
				.withFilLiten(e.getFilLiten())
				.withFilStor(e.getFilStor())
				.withFilTxt(e.getFilTxt()))
			.orElse(null);
	}
}
