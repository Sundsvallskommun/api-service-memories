package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.integration.db.model.PublicationEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PublicationMapper {

	private PublicationMapper() {}

	/** Summary mapping (no XMLTEXT) used for list responses. */
	public static Publication toPublicationSummary(final PublicationEntity entity, final String location, final String publicationType) {
		return toBase(entity, location, publicationType);
	}

	/** Detail mapping including XMLTEXT, used for get-by-id. */
	public static Publication toPublication(final PublicationEntity entity, final String location, final String publicationType) {
		return ofNullable(toBase(entity, location, publicationType))
			.map(publication -> publication.withXmltext(entity.getXmltext()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link PublicationEntity} to summary {@link Publication}s, resolving each entity's location (via
	 * publisher topography) and publication type (via PUBL_TYP) using the provided lookups.
	 *
	 * @param  entities              source entities
	 * @param  locationLookup        resolver from topographyId → location string (nullable)
	 * @param  publicationTypeLookup resolver from publicationTypeId → publication-type string (nullable)
	 * @return                       list of mapped publications (empty if entities is null)
	 */
	public static List<Publication> toPublicationList(final List<PublicationEntity> entities,
		final ReferenceResolver locationLookup, final ReferenceResolver publicationTypeLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toPublicationSummary(e,
					locationLookup.resolve(e.getPublisherTopographyId()),
					publicationTypeLookup.resolve(e.getPublicationTypeId())))
				.toList())
			.orElse(emptyList());
	}

	private static Publication toBase(final PublicationEntity entity, final String location, final String publicationType) {
		return ofNullable(entity)
			.map(e -> Publication.create()
				.withPublicationId(e.getPublicationId())
				.withFilename(e.getFilename())
				.withPublicationType(resolvePublicationType(e, publicationType))
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

	private static String resolvePublicationType(final PublicationEntity entity, final String resolved) {
		return ofNullable(resolved)
			.filter(s -> !s.isBlank())
			.orElseGet(entity::getPublicationType);
	}
}
