package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Photo;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PhotoMapper {

	private PhotoMapper() {}

	/** Summary mapping (no relatedPhotoIds, no subjects) used for list responses. */
	public static Photo toPhotoSummary(final PhotoEntity entity, final String location) {
		return toBase(entity, location);
	}

	/** Detail mapping including FOTO_FOTO relations and FOTO_OCM subjects, used for get-by-id. */
	public static Photo toPhoto(final PhotoEntity entity, final String location, final List<Integer> relatedPhotoIds, final List<Subject> subjects) {
		return ofNullable(toBase(entity, location))
			.map(photo -> photo.withRelatedPhotoIds(ofNullable(relatedPhotoIds).orElse(emptyList()))
				.withSubjects(ofNullable(subjects).orElse(emptyList())))
			.orElse(null);
	}

	/**
	 * Map a list of PhotoEntities to summary {@link Photo}s, resolving each entity's location via the provided lookup.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup resolver from topographyId → location string (nullable)
	 * @return                list of mapped {@link Photo}, empty if entities is null
	 */
	public static List<Photo> toPhotoList(final List<PhotoEntity> entities, final ReferenceResolver locationLookup) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(e -> toPhotoSummary(e, locationLookup.resolve(e.getTopographyId())))
			.toList();
	}

	private static Photo toBase(final PhotoEntity entity, final String location) {
		return ofNullable(entity)
			.map(e -> Photo.create()
				.withPhotoId(e.getPhotoId())
				.withFilename(e.getFilename())
				.withAccessionNumber(e.getAccessionNumber())
				.withReferenceCode(e.getReferenceCode())
				.withInventoryNumber(e.getInventoryNumber())
				.withEarlierReference(e.getEarlierReference())
				.withDocumentTitle(e.getDocumentTitle())
				.withSubjectKeyword(e.getSubjectKeyword())
				.withComment(e.getComment())
				.withEarliest(e.getEarliest())
				.withLatest(e.getLatest())
				.withObservationDate(e.getObservationDate())
				.withLocationText(e.getLocationText())
				.withLocation(location)
				.withStorageLocation(e.getStorageLocation())
				.withObjectType(e.getObjectType())
				.withColorMode(e.getColorMode())
				.withNegativePositive(e.getNegativePositive())
				.withTransmissiveReflective(e.getTransmissiveReflective())
				.withImageCarrier(e.getImageCarrier())
				.withMaterial(e.getMaterial())
				.withTechnique(e.getTechnique())
				.withFunction(e.getFunction())
				.withHeight(e.getHeight())
				.withWidth(e.getWidth())
				.withDiameter(e.getDiameter())
				.withFramed(e.getFramed())
				.withConditionCategory(e.getConditionCategory())
				.withConditionAssessment(e.getConditionAssessment())
				.withObserverName(e.getObserverName())
				.withTreatment(e.getTreatment())
				.withTreatmentDate(e.getTreatmentDate())
				.withSignature(e.getSignature())
				.withRights(e.getRights())
				.withRestricted(e.getRestricted())
				.withRestrictionNote(e.getRestrictionNote())
				.withProvenance(e.getProvenance())
				.withThumbnailFilename(e.getThumbnailFilename())
				.withLargeImageFilename(e.getLargeImageFilename()))
			.orElse(null);
	}
}
