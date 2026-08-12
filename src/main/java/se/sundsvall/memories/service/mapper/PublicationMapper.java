package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PublicationMapper {

	private PublicationMapper() {}

	/** Summary mapping (no XMLTEXT) used for list responses. */
	public static Publication toPublicationSummary(final PublicationEntity entity) {
		return toBase(entity);
	}

	/** Detail mapping including XMLTEXT, used for get-by-id. */
	public static Publication toPublication(final PublicationEntity entity) {
		return ofNullable(toBase(entity))
			.map(publication -> publication.withXmltext(entity.getXmltext()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link PublicationEntity} to summary {@link Publication}s.
	 *
	 * @param  entities source entities
	 * @return          list of mapped publications (empty if entities is null)
	 */
	public static List<Publication> toPublicationList(final List<PublicationEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(PublicationMapper::toPublicationSummary)
			.toList();
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the
	 * publication has no place and when {@code P_T_ID} points at a row that does not exist.
	 */
	private static String location(final PublicationEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	private static Publication toBase(final PublicationEntity entity) {
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
				.withLocation(location(e))
				.withComment(e.getComment())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename())
				.withOcrFilename(e.getOcrFilename()))
			.orElse(null);
	}
}
