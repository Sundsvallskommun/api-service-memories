package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.integration.db.model.PublEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PublicationMapper {

	private PublicationMapper() {}

	public static Publication toPublicationSummary(final PublEntity entity) {
		return toBase(entity);
	}

	public static Publication toPublication(final PublEntity entity) {
		return ofNullable(toBase(entity))
			.map(publication -> publication.withXmltext(entity.getXmltext()))
			.orElse(null);
	}

	public static List<Publication> toPublicationList(final List<PublEntity> entities) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(PublicationMapper::toPublicationSummary)
				.toList())
			.orElse(emptyList());
	}

	private static Publication toBase(final PublEntity entity) {
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
				.withKommentPubl(e.getKommentPubl())
				.withFilLiten(e.getFilLiten())
				.withFilStor(e.getFilStor())
				.withFilOriginal(e.getFilOriginal())
				.withFilTxt(e.getFilTxt())
				.withFilXtra(e.getFilXtra()))
			.orElse(null);
	}
}
