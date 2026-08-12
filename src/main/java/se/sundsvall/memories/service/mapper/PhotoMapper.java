package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Photo;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

public final class PhotoMapper {

	private PhotoMapper() {}

	/** Summary mapping (no relatedPhotoIds, no subjects) used for list responses. */
	public static Photo toPhotoSummary(final PhotoEntity entity) {
		return toBase(entity);
	}

	/** Detail mapping including FOTO_FOTO relations and FOTO_OCM subjects, used for get-by-id. */
	public static Photo toPhoto(final PhotoEntity entity, final List<Integer> relatedPhotoIds) {
		return ofNullable(toBase(entity))
			.map(photo -> photo.withRelatedPhotoIds(ofNullable(relatedPhotoIds).orElse(emptyList()))
				.withSubjects(subjects(entity)))
			.orElse(null);
	}

	/**
	 * Maps the subjects reached through the {@code FOTO_OCM} association. A photo with no subjects yields an empty
	 * list, which is what the API has always returned.
	 */
	private static List<Subject> subjects(final PhotoEntity entity) {
		return ofNullable(entity.getSubjects()).orElse(emptySet()).stream()
			.map(subject -> Subject.create()
				.withCode(subject.getCode())
				.withText(subject.getText())
				.withDescription(subject.getDescription()))
			.toList();
	}

	/**
	 * Map a list of PhotoEntities to summary {@link Photo}s.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Photo}, empty if entities is null
	 */
	public static List<Photo> toPhotoList(final List<PhotoEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(PhotoMapper::toPhotoSummary)
			.toList();
	}

	/**
	 * Resolves the place name through the topography association. The association is {@code null} both when the photo
	 * has no place and when {@code F_T_ID} points at a row that does not exist.
	 */
	private static String location(final PhotoEntity entity) {
		return ofNullable(entity.getTopography())
			.map(TopographyEntity::getDisplayName)
			.orElse(null);
	}

	private static Photo toBase(final PhotoEntity entity) {
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
				.withLocation(location(e))
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
