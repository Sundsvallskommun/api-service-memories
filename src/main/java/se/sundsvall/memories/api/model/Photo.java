package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "Photo model")
public class Photo {

	@Schema(description = "Photo ID", examples = "1234")
	private Integer photoId;

	@Schema(description = "Original filename from archive")
	private String filename;

	@Schema(description = "Accession number")
	private String accessionNumber;

	@Schema(description = "Reference code (negative number)")
	private String referenceCode;

	@Schema(description = "Inventory number (RAÄ-nr)")
	private String inventoryNumber;

	@Schema(description = "Earlier reference number")
	private String earlierReference;

	@Schema(description = "Document title / description")
	private String documentTitle;

	@Schema(description = "Subject keyword")
	private String subjectKeyword;

	@Schema(description = "Comment / description")
	private String comment;

	@Schema(description = "Earliest date of time period", examples = "1920")
	private String earliest;

	@Schema(description = "Latest date of time period", examples = "1925")
	private String latest;

	@Schema(description = "Observation date")
	private String observationDate;

	@Schema(description = "Indeterminable place (free-text fallback from F_OPLATS)")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over locationText when set)")
	private String location;

	@Schema(description = "Storage location")
	private String storageLocation;

	@Schema(description = "Object type", examples = "Foto")
	private String objectType;

	@Schema(description = "Black & white / colour")
	private String colorMode;

	@Schema(description = "Negative / positive")
	private String negativePositive;

	@Schema(description = "Transmissive / reflective")
	private String transmissiveReflective;

	@Schema(description = "Image carrier")
	private String imageCarrier;

	@Schema(description = "Material")
	private String material;

	@Schema(description = "Technique")
	private String technique;

	@Schema(description = "Function")
	private String function;

	@Schema(description = "Height (cm)")
	private String height;

	@Schema(description = "Width (cm)")
	private String width;

	@Schema(description = "Diameter")
	private String diameter;

	@Schema(description = "Framed / mounted")
	private String framed;

	@Schema(description = "Condition category")
	private String conditionCategory;

	@Schema(description = "Condition assessment")
	private String conditionAssessment;

	@Schema(description = "Observer name")
	private String observerName;

	@Schema(description = "Treatment / action taken")
	private String treatment;

	@Schema(description = "Treatment date")
	private String treatmentDate;

	@Schema(description = "Signature")
	private String signature;

	@Schema(description = "Image rights")
	private String rights;

	@Schema(description = "Restrictions (Yes/No)")
	private String restricted;

	@Schema(description = "Restriction comment")
	private String restrictionNote;

	@Schema(description = "Comment about creator/donor")
	private String provenance;

	@Schema(description = "Thumbnail file name")
	private String thumbnailFilename;

	@Schema(description = "Large image file name")
	private String largeImageFilename;

	@Schema(description = "IDs of related photos via FOTO_FOTO (only returned on detail lookup)")
	private List<Integer> relatedPhotoIds;

	@Schema(description = "Subjects / ämnesklassificering via FOTO_OCM (only returned on detail lookup)")
	private List<Subject> subjects;

	public static Photo create() {
		return new Photo();
	}

	public Integer getPhotoId() {
		return photoId;
	}

	public void setPhotoId(final Integer photoId) {
		this.photoId = photoId;
	}

	public Photo withPhotoId(final Integer photoId) {
		this.photoId = photoId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Photo withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public Photo withAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
		return this;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(final String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public Photo withReferenceCode(final String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}

	public String getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(final String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public Photo withInventoryNumber(final String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
		return this;
	}

	public String getEarlierReference() {
		return earlierReference;
	}

	public void setEarlierReference(final String earlierReference) {
		this.earlierReference = earlierReference;
	}

	public Photo withEarlierReference(final String earlierReference) {
		this.earlierReference = earlierReference;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Photo withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public String getSubjectKeyword() {
		return subjectKeyword;
	}

	public void setSubjectKeyword(final String subjectKeyword) {
		this.subjectKeyword = subjectKeyword;
	}

	public Photo withSubjectKeyword(final String subjectKeyword) {
		this.subjectKeyword = subjectKeyword;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public Photo withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getEarliest() {
		return earliest;
	}

	public void setEarliest(final String earliest) {
		this.earliest = earliest;
	}

	public Photo withEarliest(final String earliest) {
		this.earliest = earliest;
		return this;
	}

	public String getLatest() {
		return latest;
	}

	public void setLatest(final String latest) {
		this.latest = latest;
	}

	public Photo withLatest(final String latest) {
		this.latest = latest;
		return this;
	}

	public String getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(final String observationDate) {
		this.observationDate = observationDate;
	}

	public Photo withObservationDate(final String observationDate) {
		this.observationDate = observationDate;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public Photo withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Photo withLocation(final String location) {
		this.location = location;
		return this;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(final String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public Photo withStorageLocation(final String storageLocation) {
		this.storageLocation = storageLocation;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public Photo withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getColorMode() {
		return colorMode;
	}

	public void setColorMode(final String colorMode) {
		this.colorMode = colorMode;
	}

	public Photo withColorMode(final String colorMode) {
		this.colorMode = colorMode;
		return this;
	}

	public String getNegativePositive() {
		return negativePositive;
	}

	public void setNegativePositive(final String negativePositive) {
		this.negativePositive = negativePositive;
	}

	public Photo withNegativePositive(final String negativePositive) {
		this.negativePositive = negativePositive;
		return this;
	}

	public String getTransmissiveReflective() {
		return transmissiveReflective;
	}

	public void setTransmissiveReflective(final String transmissiveReflective) {
		this.transmissiveReflective = transmissiveReflective;
	}

	public Photo withTransmissiveReflective(final String transmissiveReflective) {
		this.transmissiveReflective = transmissiveReflective;
		return this;
	}

	public String getImageCarrier() {
		return imageCarrier;
	}

	public void setImageCarrier(final String imageCarrier) {
		this.imageCarrier = imageCarrier;
	}

	public Photo withImageCarrier(final String imageCarrier) {
		this.imageCarrier = imageCarrier;
		return this;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(final String material) {
		this.material = material;
	}

	public Photo withMaterial(final String material) {
		this.material = material;
		return this;
	}

	public String getTechnique() {
		return technique;
	}

	public void setTechnique(final String technique) {
		this.technique = technique;
	}

	public Photo withTechnique(final String technique) {
		this.technique = technique;
		return this;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(final String function) {
		this.function = function;
	}

	public Photo withFunction(final String function) {
		this.function = function;
		return this;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(final String height) {
		this.height = height;
	}

	public Photo withHeight(final String height) {
		this.height = height;
		return this;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(final String width) {
		this.width = width;
	}

	public Photo withWidth(final String width) {
		this.width = width;
		return this;
	}

	public String getDiameter() {
		return diameter;
	}

	public void setDiameter(final String diameter) {
		this.diameter = diameter;
	}

	public Photo withDiameter(final String diameter) {
		this.diameter = diameter;
		return this;
	}

	public String getFramed() {
		return framed;
	}

	public void setFramed(final String framed) {
		this.framed = framed;
	}

	public Photo withFramed(final String framed) {
		this.framed = framed;
		return this;
	}

	public String getConditionCategory() {
		return conditionCategory;
	}

	public void setConditionCategory(final String conditionCategory) {
		this.conditionCategory = conditionCategory;
	}

	public Photo withConditionCategory(final String conditionCategory) {
		this.conditionCategory = conditionCategory;
		return this;
	}

	public String getConditionAssessment() {
		return conditionAssessment;
	}

	public void setConditionAssessment(final String conditionAssessment) {
		this.conditionAssessment = conditionAssessment;
	}

	public Photo withConditionAssessment(final String conditionAssessment) {
		this.conditionAssessment = conditionAssessment;
		return this;
	}

	public String getObserverName() {
		return observerName;
	}

	public void setObserverName(final String observerName) {
		this.observerName = observerName;
	}

	public Photo withObserverName(final String observerName) {
		this.observerName = observerName;
		return this;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(final String treatment) {
		this.treatment = treatment;
	}

	public Photo withTreatment(final String treatment) {
		this.treatment = treatment;
		return this;
	}

	public String getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(final String treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	public Photo withTreatmentDate(final String treatmentDate) {
		this.treatmentDate = treatmentDate;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(final String signature) {
		this.signature = signature;
	}

	public Photo withSignature(final String signature) {
		this.signature = signature;
		return this;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(final String rights) {
		this.rights = rights;
	}

	public Photo withRights(final String rights) {
		this.rights = rights;
		return this;
	}

	public String getRestricted() {
		return restricted;
	}

	public void setRestricted(final String restricted) {
		this.restricted = restricted;
	}

	public Photo withRestricted(final String restricted) {
		this.restricted = restricted;
		return this;
	}

	public String getRestrictionNote() {
		return restrictionNote;
	}

	public void setRestrictionNote(final String restrictionNote) {
		this.restrictionNote = restrictionNote;
	}

	public Photo withRestrictionNote(final String restrictionNote) {
		this.restrictionNote = restrictionNote;
		return this;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(final String provenance) {
		this.provenance = provenance;
	}

	public Photo withProvenance(final String provenance) {
		this.provenance = provenance;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public Photo withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public Photo withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public List<Integer> getRelatedPhotoIds() {
		return relatedPhotoIds;
	}

	public void setRelatedPhotoIds(final List<Integer> relatedPhotoIds) {
		this.relatedPhotoIds = relatedPhotoIds;
	}

	public Photo withRelatedPhotoIds(final List<Integer> relatedPhotoIds) {
		this.relatedPhotoIds = relatedPhotoIds;
		return this;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(final List<Subject> subjects) {
		this.subjects = subjects;
	}

	public Photo withSubjects(final List<Subject> subjects) {
		this.subjects = subjects;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Photo that = (Photo) o;
		return Objects.equals(photoId, that.photoId) && Objects.equals(filename, that.filename) && Objects.equals(accessionNumber, that.accessionNumber)
			&& Objects.equals(referenceCode, that.referenceCode) && Objects.equals(inventoryNumber, that.inventoryNumber) && Objects.equals(earlierReference, that.earlierReference)
			&& Objects.equals(documentTitle, that.documentTitle) && Objects.equals(subjectKeyword, that.subjectKeyword) && Objects.equals(comment, that.comment)
			&& Objects.equals(earliest, that.earliest) && Objects.equals(latest, that.latest) && Objects.equals(observationDate, that.observationDate)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(location, that.location) && Objects.equals(storageLocation, that.storageLocation)
			&& Objects.equals(objectType, that.objectType) && Objects.equals(colorMode, that.colorMode) && Objects.equals(negativePositive, that.negativePositive)
			&& Objects.equals(transmissiveReflective, that.transmissiveReflective) && Objects.equals(imageCarrier, that.imageCarrier) && Objects.equals(material, that.material)
			&& Objects.equals(technique, that.technique) && Objects.equals(function, that.function) && Objects.equals(height, that.height) && Objects.equals(width, that.width)
			&& Objects.equals(diameter, that.diameter) && Objects.equals(framed, that.framed) && Objects.equals(conditionCategory, that.conditionCategory)
			&& Objects.equals(conditionAssessment, that.conditionAssessment) && Objects.equals(observerName, that.observerName) && Objects.equals(treatment, that.treatment)
			&& Objects.equals(treatmentDate, that.treatmentDate) && Objects.equals(signature, that.signature) && Objects.equals(rights, that.rights)
			&& Objects.equals(restricted, that.restricted) && Objects.equals(restrictionNote, that.restrictionNote) && Objects.equals(provenance, that.provenance)
			&& Objects.equals(thumbnailFilename, that.thumbnailFilename) && Objects.equals(largeImageFilename, that.largeImageFilename)
			&& Objects.equals(relatedPhotoIds, that.relatedPhotoIds) && Objects.equals(subjects, that.subjects);
	}

	@Override
	public int hashCode() {
		return Objects.hash(photoId, filename, accessionNumber, referenceCode, inventoryNumber, earlierReference, documentTitle, subjectKeyword, comment,
			earliest, latest, observationDate, locationText, location, storageLocation, objectType, colorMode, negativePositive, transmissiveReflective,
			imageCarrier, material, technique, function, height, width, diameter, framed, conditionCategory, conditionAssessment,
			observerName, treatment, treatmentDate, signature, rights, restricted, restrictionNote, provenance,
			thumbnailFilename, largeImageFilename, relatedPhotoIds, subjects);
	}

	@Override
	public String toString() {
		return "Photo{" +
			"photoId=" + photoId +
			", filename='" + filename + '\'' +
			", accessionNumber='" + accessionNumber + '\'' +
			", referenceCode='" + referenceCode + '\'' +
			", inventoryNumber='" + inventoryNumber + '\'' +
			", earlierReference='" + earlierReference + '\'' +
			", documentTitle='" + documentTitle + '\'' +
			", subjectKeyword='" + subjectKeyword + '\'' +
			", comment='" + comment + '\'' +
			", earliest='" + earliest + '\'' +
			", latest='" + latest + '\'' +
			", observationDate='" + observationDate + '\'' +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			", storageLocation='" + storageLocation + '\'' +
			", objectType='" + objectType + '\'' +
			", colorMode='" + colorMode + '\'' +
			", negativePositive='" + negativePositive + '\'' +
			", transmissiveReflective='" + transmissiveReflective + '\'' +
			", imageCarrier='" + imageCarrier + '\'' +
			", material='" + material + '\'' +
			", technique='" + technique + '\'' +
			", function='" + function + '\'' +
			", height='" + height + '\'' +
			", width='" + width + '\'' +
			", diameter='" + diameter + '\'' +
			", framed='" + framed + '\'' +
			", conditionCategory='" + conditionCategory + '\'' +
			", conditionAssessment='" + conditionAssessment + '\'' +
			", observerName='" + observerName + '\'' +
			", treatment='" + treatment + '\'' +
			", treatmentDate='" + treatmentDate + '\'' +
			", signature='" + signature + '\'' +
			", rights='" + rights + '\'' +
			", restricted='" + restricted + '\'' +
			", restrictionNote='" + restrictionNote + '\'' +
			", provenance='" + provenance + '\'' +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", relatedPhotoIds=" + relatedPhotoIds +
			", subjects=" + subjects +
			'}';
	}
}
