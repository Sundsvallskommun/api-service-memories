package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Archive or collection node")
public class Node {

	@Schema(description = "Node ID", examples = "100")
	private Integer id;

	@Schema(description = "ID of the node this one sits under, or null for a root node", examples = "10")
	private Integer parentId;

	@Schema(description = "Node name. An archive named after its arkivbildare carries no name of its own, and is then named after the legal entity, or the person as Efternamn, Förnamn", examples = "Sundsvalls stads arkiv")
	private String name;

	@Schema(description = "Node type ID", examples = "1")
	private Integer nodeTypeId;

	@Schema(description = "Resolved node type name (arkiv, serie, volym and so on)", examples = "Arkiv")
	private String nodeType;

	@Schema(description = "First year the node covers, or null when unknown", examples = "1862")
	private Integer startYear;

	@Schema(description = "Last year the node covers, or null when it has not ended", examples = "1951")
	private Integer stopYear;

	@Schema(description = "Description of what the node contains", examples = "Handlingar från stadsfullmäktige")
	private String description;

	@Schema(description = "Sort order among its siblings, as set in the archive", examples = "10")
	private Integer sortOrder;

	@Schema(description = "Number of items below the node", examples = "42")
	private Integer subItemCount;

	@Schema(description = "Number of published items below the node", examples = "40")
	private Integer publishedSubItemCount;

	@Schema(description = "Status bitmask; bit 4 marks the node as published", examples = "6")
	private Integer options;

	@Schema(description = "The arkivbildare — the person or legal entity whose archive this is. Set on archives, not on the series and volumes under them", accessMode = READ_ONLY)
	private Creator creator;

	@Schema(description = "First year of the arkivbildare's activity (verksamhetstid), when it is a legal entity. Free text in the archive", examples = "1862")
	private String activityStartDate;

	@Schema(description = "Last year of the arkivbildare's activity (verksamhetstid), when it is a legal entity", examples = "1951")
	private String activityEndDate;

	@Schema(description = "ID of the institution (arkivinstitution) holding the archive, as listed by /institutions", examples = "3")
	private Integer institutionId;

	@Schema(description = "Name of the institution holding the archive", examples = "Sundsvalls museum")
	private String institution;

	@Schema(description = "Code (institutionskod) of the institution holding the archive", examples = "SVM")
	private String institutionCode;

	@Schema(description = "ID of the arkivbildare's category (verksamhetskategori), as listed by /categories", examples = "5")
	private Integer categoryId;

	@Schema(description = "Name of the arkivbildare's category", examples = "Företag")
	private String category;

	@Schema(description = "ID of the place (topografi) the archive is about or comes from, as listed by /topographies", examples = "4")
	private Integer topographyId;

	@Schema(description = "Resolved place name from TOPOGRAFI, the same name /topographies lists it under", examples = "Kvissleby, Njurunda")
	private String location;

	@Schema(description = "Free-text place (obestämd plats), used when no topography fits", examples = "Okänd by i Medelpad")
	private String locationText;

	@Schema(description = "Subject (ämne, OCM) of the node. Set on series", accessMode = READ_ONLY)
	private Subject subject;

	@Schema(description = "Series signum (seriesignum). Set on series", examples = "A1")
	private String seriesSignum;

	@Schema(description = "Earlier series signum (gammalt seriesignum). Set on series", examples = "A I")
	private String oldSeriesSignum;

	@Schema(description = "Volume number (volymnummer). Set on volumes", examples = "001")
	private String volumeNumber;

	@Schema(description = "Number of volumes (volymantal). Set on archives", examples = "3")
	private Integer volumeCount;

	@Schema(description = "Shelf metres (hyllmeter) the archive takes up", examples = "12.50")
	private BigDecimal shelfMeters;

	@Schema(description = "Where the volume is kept (volymplacering). Set on volumes", examples = "Hylla 3")
	private String volumePlacement;

	@Schema(description = "Accession number (accessionsnummer)", examples = "ACC-1862")
	private String accessionNumber;

	@Schema(description = "Holdings code (beståndskod)", examples = "B1")
	private String holdingsCode;

	@Schema(description = "Name of the history (historik/biografi) file the archive has uploaded for the node, when there is one", examples = "arkiv_100_historik.xml")
	private String historyFilename;

	public static Node create() {
		return new Node();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Node withId(final Integer id) {
		this.id = id;
		return this;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(final Integer parentId) {
		this.parentId = parentId;
	}

	public Node withParentId(final Integer parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Node withName(final String name) {
		this.name = name;
		return this;
	}

	public Integer getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public Node withNodeTypeId(final Integer nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
		return this;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(final String nodeType) {
		this.nodeType = nodeType;
	}

	public Node withNodeType(final String nodeType) {
		this.nodeType = nodeType;
		return this;
	}

	public Integer getStartYear() {
		return startYear;
	}

	public void setStartYear(final Integer startYear) {
		this.startYear = startYear;
	}

	public Node withStartYear(final Integer startYear) {
		this.startYear = startYear;
		return this;
	}

	public Integer getStopYear() {
		return stopYear;
	}

	public void setStopYear(final Integer stopYear) {
		this.stopYear = stopYear;
	}

	public Node withStopYear(final Integer stopYear) {
		this.stopYear = stopYear;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Node withDescription(final String description) {
		this.description = description;
		return this;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(final Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Node withSortOrder(final Integer sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public Integer getSubItemCount() {
		return subItemCount;
	}

	public void setSubItemCount(final Integer subItemCount) {
		this.subItemCount = subItemCount;
	}

	public Node withSubItemCount(final Integer subItemCount) {
		this.subItemCount = subItemCount;
		return this;
	}

	public Integer getPublishedSubItemCount() {
		return publishedSubItemCount;
	}

	public void setPublishedSubItemCount(final Integer publishedSubItemCount) {
		this.publishedSubItemCount = publishedSubItemCount;
	}

	public Node withPublishedSubItemCount(final Integer publishedSubItemCount) {
		this.publishedSubItemCount = publishedSubItemCount;
		return this;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(final Integer options) {
		this.options = options;
	}

	public Node withOptions(final Integer options) {
		this.options = options;
		return this;
	}

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(final Creator creator) {
		this.creator = creator;
	}

	public Node withCreator(final Creator creator) {
		this.creator = creator;
		return this;
	}

	public String getActivityStartDate() {
		return activityStartDate;
	}

	public void setActivityStartDate(final String activityStartDate) {
		this.activityStartDate = activityStartDate;
	}

	public Node withActivityStartDate(final String activityStartDate) {
		this.activityStartDate = activityStartDate;
		return this;
	}

	public String getActivityEndDate() {
		return activityEndDate;
	}

	public void setActivityEndDate(final String activityEndDate) {
		this.activityEndDate = activityEndDate;
	}

	public Node withActivityEndDate(final String activityEndDate) {
		this.activityEndDate = activityEndDate;
		return this;
	}

	public Integer getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
	}

	public Node withInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
		return this;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(final String institution) {
		this.institution = institution;
	}

	public Node withInstitution(final String institution) {
		this.institution = institution;
		return this;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(final String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public Node withInstitutionCode(final String institutionCode) {
		this.institutionCode = institutionCode;
		return this;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Node withCategoryId(final Integer categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public Node withCategory(final String category) {
		this.category = category;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public Node withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public Node withLocation(final String location) {
		this.location = location;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public Node withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(final Subject subject) {
		this.subject = subject;
	}

	public Node withSubject(final Subject subject) {
		this.subject = subject;
		return this;
	}

	public String getSeriesSignum() {
		return seriesSignum;
	}

	public void setSeriesSignum(final String seriesSignum) {
		this.seriesSignum = seriesSignum;
	}

	public Node withSeriesSignum(final String seriesSignum) {
		this.seriesSignum = seriesSignum;
		return this;
	}

	public String getOldSeriesSignum() {
		return oldSeriesSignum;
	}

	public void setOldSeriesSignum(final String oldSeriesSignum) {
		this.oldSeriesSignum = oldSeriesSignum;
	}

	public Node withOldSeriesSignum(final String oldSeriesSignum) {
		this.oldSeriesSignum = oldSeriesSignum;
		return this;
	}

	public String getVolumeNumber() {
		return volumeNumber;
	}

	public void setVolumeNumber(final String volumeNumber) {
		this.volumeNumber = volumeNumber;
	}

	public Node withVolumeNumber(final String volumeNumber) {
		this.volumeNumber = volumeNumber;
		return this;
	}

	public Integer getVolumeCount() {
		return volumeCount;
	}

	public void setVolumeCount(final Integer volumeCount) {
		this.volumeCount = volumeCount;
	}

	public Node withVolumeCount(final Integer volumeCount) {
		this.volumeCount = volumeCount;
		return this;
	}

	public BigDecimal getShelfMeters() {
		return shelfMeters;
	}

	public void setShelfMeters(final BigDecimal shelfMeters) {
		this.shelfMeters = shelfMeters;
	}

	public Node withShelfMeters(final BigDecimal shelfMeters) {
		this.shelfMeters = shelfMeters;
		return this;
	}

	public String getVolumePlacement() {
		return volumePlacement;
	}

	public void setVolumePlacement(final String volumePlacement) {
		this.volumePlacement = volumePlacement;
	}

	public Node withVolumePlacement(final String volumePlacement) {
		this.volumePlacement = volumePlacement;
		return this;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public Node withAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
		return this;
	}

	public String getHoldingsCode() {
		return holdingsCode;
	}

	public void setHoldingsCode(final String holdingsCode) {
		this.holdingsCode = holdingsCode;
	}

	public Node withHoldingsCode(final String holdingsCode) {
		this.holdingsCode = holdingsCode;
		return this;
	}

	public String getHistoryFilename() {
		return historyFilename;
	}

	public void setHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
	}

	public Node withHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Node that = (Node) o;
		return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(name, that.name)
			&& Objects.equals(nodeTypeId, that.nodeTypeId) && Objects.equals(nodeType, that.nodeType)
			&& Objects.equals(startYear, that.startYear) && Objects.equals(stopYear, that.stopYear)
			&& Objects.equals(description, that.description) && Objects.equals(sortOrder, that.sortOrder)
			&& Objects.equals(subItemCount, that.subItemCount)
			&& Objects.equals(publishedSubItemCount, that.publishedSubItemCount) && Objects.equals(options, that.options)
			&& Objects.equals(creator, that.creator) && Objects.equals(activityStartDate, that.activityStartDate)
			&& Objects.equals(activityEndDate, that.activityEndDate) && Objects.equals(institutionId, that.institutionId)
			&& Objects.equals(institution, that.institution) && Objects.equals(institutionCode, that.institutionCode)
			&& Objects.equals(categoryId, that.categoryId) && Objects.equals(category, that.category)
			&& Objects.equals(topographyId, that.topographyId) && Objects.equals(location, that.location)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(subject, that.subject)
			&& Objects.equals(seriesSignum, that.seriesSignum) && Objects.equals(oldSeriesSignum, that.oldSeriesSignum)
			&& Objects.equals(volumeNumber, that.volumeNumber) && Objects.equals(volumeCount, that.volumeCount)
			&& Objects.equals(shelfMeters, that.shelfMeters) && Objects.equals(volumePlacement, that.volumePlacement)
			&& Objects.equals(accessionNumber, that.accessionNumber) && Objects.equals(holdingsCode, that.holdingsCode)
			&& Objects.equals(historyFilename, that.historyFilename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, name, nodeTypeId, nodeType, startYear, stopYear, description, sortOrder, subItemCount, publishedSubItemCount, options,
			creator, activityStartDate, activityEndDate, institutionId, institution, institutionCode, categoryId, category, topographyId, location,
			locationText, subject, seriesSignum, oldSeriesSignum, volumeNumber, volumeCount, shelfMeters, volumePlacement, accessionNumber, holdingsCode,
			historyFilename);
	}

	@Override
	public String toString() {
		return "Node{" +
			"id=" + id +
			", parentId=" + parentId +
			", name=" + name +
			", nodeTypeId=" + nodeTypeId +
			", nodeType=" + nodeType +
			", startYear=" + startYear +
			", stopYear=" + stopYear +
			", description=" + description +
			", sortOrder=" + sortOrder +
			", subItemCount=" + subItemCount +
			", publishedSubItemCount=" + publishedSubItemCount +
			", options=" + options +
			", creator=" + creator +
			", activityStartDate=" + activityStartDate +
			", activityEndDate=" + activityEndDate +
			", institutionId=" + institutionId +
			", institution=" + institution +
			", institutionCode=" + institutionCode +
			", categoryId=" + categoryId +
			", category=" + category +
			", topographyId=" + topographyId +
			", location=" + location +
			", locationText=" + locationText +
			", subject=" + subject +
			", seriesSignum=" + seriesSignum +
			", oldSeriesSignum=" + oldSeriesSignum +
			", volumeNumber=" + volumeNumber +
			", volumeCount=" + volumeCount +
			", shelfMeters=" + shelfMeters +
			", volumePlacement=" + volumePlacement +
			", accessionNumber=" + accessionNumber +
			", holdingsCode=" + holdingsCode +
			", historyFilename=" + historyFilename +
			'}';
	}
}
