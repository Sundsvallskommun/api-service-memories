package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PublicationMapper {

	private PublicationMapper() {}

	/** Summary mapping (no XMLTEXT) used for list responses. */
	public static Publication toPublicationSummary(final PublicationEntity entity, final String location) {
		return toBase(entity, location);
	}

	/** Detail mapping including XMLTEXT, used for get-by-id. */
	public static Publication toPublication(final PublicationEntity entity, final String location) {
		return ofNullable(toBase(entity, location))
			.map(publication -> publication.withXmltext(entity.getXmltext()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link PublicationEntity} to summary {@link Publication}s, resolving each entity's location via the
	 * provided lookup.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup resolver from topographyId → location string (nullable)
	 * @return                list of mapped publications (empty if entities is null)
	 */
	public static List<Publication> toPublicationList(final List<PublicationEntity> entities, final ReferenceResolver locationLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toPublicationSummary(e, locationLookup.resolve(e.getTopographyId())))
			.toList();
	}

	private static Publication toBase(final PublicationEntity entity, final String location) {
		return ofNullable(entity)
			.map(e -> Publication.create()
				.withPublicationId(e.getPublicationId())
				.withFilename(e.getFilename())
				.withPublicationType(e.getPublicationType())
				.withDate(e.getDate())
				.withPeriodicalTitle(e.getPeriodicalTitle())
				.withIssueNumber(e.getIssueNumber())
				.withPageNumber(e.getPageNumber())
				.withPublisherLocation(e.getPublisherLocation())
				.withDocumentTitle(e.getDocumentTitle())
				.withLocationText(e.getLocationText())
				.withLocation(location)
				.withComment(e.getComment())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOcrFilename(e.getOcrFilename()))
			.orElse(null);
	}
}
