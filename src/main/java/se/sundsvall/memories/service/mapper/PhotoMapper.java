package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.function.Function;
import se.sundsvall.memories.api.model.Photo;
import se.sundsvall.memories.integration.db.model.PhotoEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PhotoMapper {

	private PhotoMapper() {}

	/**
	 * Map a single PhotoEntity to a Photo API model with the resolved place name {@code location}.
	 *
	 * @param  entity   the source entity
	 * @param  location the topography-resolved place name (nullable)
	 * @return          the mapped {@link Photo}, or {@code null} if {@code entity} is null
	 */
	public static Photo toPhoto(final PhotoEntity entity, final String location) {
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

	/**
	 * Map a list of PhotoEntities, resolving each entity's location via the provided lookup.
	 *
	 * @param  entities       source entities
	 * @param  locationLookup function from topographyId → resolved location string (nullable)
	 * @return                list of mapped {@link Photo}, empty if entities is null
	 */
	@SuppressWarnings("java:S4276") // IntFunction<String> would require unboxing topographyId; the field is a nullable Integer.
	public static List<Photo> toPhotoList(final List<PhotoEntity> entities, final Function<Integer, String> locationLookup) {
		return ofNullable(entities)
			.map(list -> list.stream()
				.map(e -> toPhoto(e, locationLookup.apply(e.getTopographyId())))
				.toList())
			.orElse(emptyList());
	}
}
