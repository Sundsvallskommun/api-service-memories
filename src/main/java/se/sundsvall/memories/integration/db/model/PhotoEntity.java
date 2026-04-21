package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "FOTO")
public class PhotoEntity {

	@Id
	@Column(name = "F_ID")
	private Integer photoId;

	@Column(name = "F_T_ID")
	private Integer topographyId;

	@Column(name = "FILNAMN", length = 256)
	private String filename;

	@Column(name = "ACCNR", length = 15)
	private String accessionNumber;

	@Column(name = "REFKOD", length = 15)
	private String referenceCode;

	@Column(name = "INVENTNR", length = 15)
	private String inventoryNumber;

	@Column(name = "TIDIGNR", length = 30)
	private String earlierReference;

	@Column(name = "DOKTITEL", length = 256)
	private String documentTitle;

	@Column(name = "SAKORD", length = 128)
	private String subjectKeyword;

	@Column(name = "KOMMENT_FF", length = 4000)
	private String comment;

	@Column(name = "TIDIG", length = 10)
	private String earliest;

	@Column(name = "SENAST", length = 10)
	private String latest;

	@Column(name = "OBSDATUM", length = 10)
	private String observationDate;

	@Column(name = "F_OPLATS", length = 64)
	private String locationText;

	@Column(name = "FORPLATS", length = 35)
	private String storageLocation;

	@Column(name = "OBJTYP", length = 10)
	private String objectType;

	@Column(name = "SVVITFARG", length = 10)
	private String colorMode;

	@Column(name = "NEGPOS", length = 10)
	private String negativePositive;

	@Column(name = "GENPAS", length = 10)
	private String transmissiveReflective;

	@Column(name = "BILDBAR", length = 10)
	private String imageCarrier;

	@Column(name = "MATERIAL", length = 50)
	private String material;

	@Column(name = "TEKNIK", length = 50)
	private String technique;

	@Column(name = "FUNKTION", length = 500)
	private String function;

	@Column(name = "HOJD", length = 20)
	private String height;

	@Column(name = "BREDD", length = 20)
	private String width;

	@Column(name = "DIAM", length = 20)
	private String diameter;

	@Column(name = "RAM", length = 10)
	private String framed;

	@Column(name = "TILLSKAT", length = 60)
	private String conditionCategory;

	@Column(name = "TILLSTAND", length = 1000)
	private String conditionAssessment;

	@Column(name = "OBSNAMN", length = 50)
	private String observerName;

	@Column(name = "ATGARD", length = 1000)
	private String treatment;

	@Column(name = "ATDDATUM", length = 10)
	private String treatmentDate;

	@Column(name = "SIGN", length = 30)
	private String signature;

	@Column(name = "G_RATTIGH", length = 1000)
	private String rights;

	@Column(name = "G_FORBEH", length = 3)
	private String restricted;

	@Column(name = "ANVANDO", length = 1000)
	private String restrictionNote;

	@Column(name = "KOMMENT_UPPH", length = 1000)
	private String provenance;

	@Column(name = "FIL_LITEN", length = 256)
	private String thumbnailFilename;

	@Column(name = "FIL_STOR", length = 256)
	private String largeImageFilename;

	@Column(name = "FIL_ORIGINAL", length = 256)
	private String originalFilename;

	@Column(name = "NODEID")
	private Integer nodeId;

	@Column(name = "OPTIONS")
	private Integer options;

	@Column(name = "DELETEDDATE")
	private LocalDate deletedDate;

	public static PhotoEntity create() {
		return new PhotoEntity();
	}

	public Integer getPhotoId() {
		return photoId;
	}

	public void setPhotoId(final Integer photoId) {
		this.photoId = photoId;
	}

	public PhotoEntity withPhotoId(final Integer photoId) {
		this.photoId = photoId;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public PhotoEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public PhotoEntity withFilename(final String filename) {
		this.filename = filename;
		return this;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public PhotoEntity withAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
		return this;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(final String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public PhotoEntity withReferenceCode(final String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}

	public String getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(final String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public PhotoEntity withInventoryNumber(final String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
		return this;
	}

	public String getEarlierReference() {
		return earlierReference;
	}

	public void setEarlierReference(final String earlierReference) {
		this.earlierReference = earlierReference;
	}

	public PhotoEntity withEarlierReference(final String earlierReference) {
		this.earlierReference = earlierReference;
		return this;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public PhotoEntity withDocumentTitle(final String documentTitle) {
		this.documentTitle = documentTitle;
		return this;
	}

	public String getSubjectKeyword() {
		return subjectKeyword;
	}

	public void setSubjectKeyword(final String subjectKeyword) {
		this.subjectKeyword = subjectKeyword;
	}

	public PhotoEntity withSubjectKeyword(final String subjectKeyword) {
		this.subjectKeyword = subjectKeyword;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public PhotoEntity withComment(final String comment) {
		this.comment = comment;
		return this;
	}

	public String getEarliest() {
		return earliest;
	}

	public void setEarliest(final String earliest) {
		this.earliest = earliest;
	}

	public PhotoEntity withEarliest(final String earliest) {
		this.earliest = earliest;
		return this;
	}

	public String getLatest() {
		return latest;
	}

	public void setLatest(final String latest) {
		this.latest = latest;
	}

	public PhotoEntity withLatest(final String latest) {
		this.latest = latest;
		return this;
	}

	public String getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(final String observationDate) {
		this.observationDate = observationDate;
	}

	public PhotoEntity withObservationDate(final String observationDate) {
		this.observationDate = observationDate;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public PhotoEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(final String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public PhotoEntity withStorageLocation(final String storageLocation) {
		this.storageLocation = storageLocation;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public PhotoEntity withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getColorMode() {
		return colorMode;
	}

	public void setColorMode(final String colorMode) {
		this.colorMode = colorMode;
	}

	public PhotoEntity withColorMode(final String colorMode) {
		this.colorMode = colorMode;
		return this;
	}

	public String getNegativePositive() {
		return negativePositive;
	}

	public void setNegativePositive(final String negativePositive) {
		this.negativePositive = negativePositive;
	}

	public PhotoEntity withNegativePositive(final String negativePositive) {
		this.negativePositive = negativePositive;
		return this;
	}

	public String getTransmissiveReflective() {
		return transmissiveReflective;
	}

	public void setTransmissiveReflective(final String transmissiveReflective) {
		this.transmissiveReflective = transmissiveReflective;
	}

	public PhotoEntity withTransmissiveReflective(final String transmissiveReflective) {
		this.transmissiveReflective = transmissiveReflective;
		return this;
	}

	public String getImageCarrier() {
		return imageCarrier;
	}

	public void setImageCarrier(final String imageCarrier) {
		this.imageCarrier = imageCarrier;
	}

	public PhotoEntity withImageCarrier(final String imageCarrier) {
		this.imageCarrier = imageCarrier;
		return this;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(final String material) {
		this.material = material;
	}

	public PhotoEntity withMaterial(final String material) {
		this.material = material;
		return this;
	}

	public String getTechnique() {
		return technique;
	}

	public void setTechnique(final String technique) {
		this.technique = technique;
	}

	public PhotoEntity withTechnique(final String technique) {
		this.technique = technique;
		return this;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(final String function) {
		this.function = function;
	}

	public PhotoEntity withFunction(final String function) {
		this.function = function;
		return this;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(final String height) {
		this.height = height;
	}

	public PhotoEntity withHeight(final String height) {
		this.height = height;
		return this;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(final String width) {
		this.width = width;
	}

	public PhotoEntity withWidth(final String width) {
		this.width = width;
		return this;
	}

	public String getDiameter() {
		return diameter;
	}

	public void setDiameter(final String diameter) {
		this.diameter = diameter;
	}

	public PhotoEntity withDiameter(final String diameter) {
		this.diameter = diameter;
		return this;
	}

	public String getFramed() {
		return framed;
	}

	public void setFramed(final String framed) {
		this.framed = framed;
	}

	public PhotoEntity withFramed(final String framed) {
		this.framed = framed;
		return this;
	}

	public String getConditionCategory() {
		return conditionCategory;
	}

	public void setConditionCategory(final String conditionCategory) {
		this.conditionCategory = conditionCategory;
	}

	public PhotoEntity withConditionCategory(final String conditionCategory) {
		this.conditionCategory = conditionCategory;
		return this;
	}

	public String getConditionAssessment() {
		return conditionAssessment;
	}

	public void setConditionAssessment(final String conditionAssessment) {
		this.conditionAssessment = conditionAssessment;
	}

	public PhotoEntity withConditionAssessment(final String conditionAssessment) {
		this.conditionAssessment = conditionAssessment;
		return this;
	}

	public String getObserverName() {
		return observerName;
	}

	public void setObserverName(final String observerName) {
		this.observerName = observerName;
	}

	public PhotoEntity withObserverName(final String observerName) {
		this.observerName = observerName;
		return this;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(final String treatment) {
		this.treatment = treatment;
	}

	public PhotoEntity withTreatment(final String treatment) {
		this.treatment = treatment;
		return this;
	}

	public String getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(final String treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	public PhotoEntity withTreatmentDate(final String treatmentDate) {
		this.treatmentDate = treatmentDate;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(final String signature) {
		this.signature = signature;
	}

	public PhotoEntity withSignature(final String signature) {
		this.signature = signature;
		return this;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(final String rights) {
		this.rights = rights;
	}

	public PhotoEntity withRights(final String rights) {
		this.rights = rights;
		return this;
	}

	public String getRestricted() {
		return restricted;
	}

	public void setRestricted(final String restricted) {
		this.restricted = restricted;
	}

	public PhotoEntity withRestricted(final String restricted) {
		this.restricted = restricted;
		return this;
	}

	public String getRestrictionNote() {
		return restrictionNote;
	}

	public void setRestrictionNote(final String restrictionNote) {
		this.restrictionNote = restrictionNote;
	}

	public PhotoEntity withRestrictionNote(final String restrictionNote) {
		this.restrictionNote = restrictionNote;
		return this;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(final String provenance) {
		this.provenance = provenance;
	}

	public PhotoEntity withProvenance(final String provenance) {
		this.provenance = provenance;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public PhotoEntity withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public PhotoEntity withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public PhotoEntity withOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
		return this;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public PhotoEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public PhotoEntity withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public LocalDate getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
	}

	public PhotoEntity withDeletedDate(final LocalDate deletedDate) {
		this.deletedDate = deletedDate;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PhotoEntity that = (PhotoEntity) o;
		return Objects.equals(photoId, that.photoId) && Objects.equals(topographyId, that.topographyId) && Objects.equals(filename, that.filename) && Objects.equals(accessionNumber, that.accessionNumber)
			&& Objects.equals(referenceCode, that.referenceCode) && Objects.equals(inventoryNumber, that.inventoryNumber) && Objects.equals(earlierReference, that.earlierReference)
			&& Objects.equals(documentTitle, that.documentTitle) && Objects.equals(subjectKeyword, that.subjectKeyword) && Objects.equals(comment, that.comment)
			&& Objects.equals(earliest, that.earliest) && Objects.equals(latest, that.latest) && Objects.equals(observationDate, that.observationDate)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(storageLocation, that.storageLocation) && Objects.equals(objectType, that.objectType)
			&& Objects.equals(colorMode, that.colorMode) && Objects.equals(negativePositive, that.negativePositive) && Objects.equals(transmissiveReflective, that.transmissiveReflective)
			&& Objects.equals(imageCarrier, that.imageCarrier) && Objects.equals(material, that.material) && Objects.equals(technique, that.technique)
			&& Objects.equals(function, that.function) && Objects.equals(height, that.height) && Objects.equals(width, that.width) && Objects.equals(diameter, that.diameter)
			&& Objects.equals(framed, that.framed) && Objects.equals(conditionCategory, that.conditionCategory) && Objects.equals(conditionAssessment, that.conditionAssessment)
			&& Objects.equals(observerName, that.observerName) && Objects.equals(treatment, that.treatment) && Objects.equals(treatmentDate, that.treatmentDate)
			&& Objects.equals(signature, that.signature) && Objects.equals(rights, that.rights) && Objects.equals(restricted, that.restricted)
			&& Objects.equals(restrictionNote, that.restrictionNote) && Objects.equals(provenance, that.provenance) && Objects.equals(thumbnailFilename, that.thumbnailFilename)
			&& Objects.equals(largeImageFilename, that.largeImageFilename) && Objects.equals(originalFilename, that.originalFilename)
			&& Objects.equals(nodeId, that.nodeId) && Objects.equals(options, that.options) && Objects.equals(deletedDate, that.deletedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(photoId, topographyId, filename, accessionNumber, referenceCode, inventoryNumber, earlierReference, documentTitle, subjectKeyword, comment,
			earliest, latest, observationDate, locationText, storageLocation, objectType, colorMode, negativePositive, transmissiveReflective,
			imageCarrier, material, technique, function, height, width, diameter, framed, conditionCategory, conditionAssessment,
			observerName, treatment, treatmentDate, signature, rights, restricted, restrictionNote, provenance,
			thumbnailFilename, largeImageFilename, originalFilename, nodeId, options, deletedDate);
	}

	@Override
	public String toString() {
		return "PhotoEntity{" +
			"photoId=" + photoId +
			", topographyId=" + topographyId +
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
			", originalFilename='" + originalFilename + '\'' +
			", nodeId=" + nodeId +
			", options=" + options +
			", deletedDate=" + deletedDate +
			'}';
	}
}
