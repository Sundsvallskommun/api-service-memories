package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity for the {@code TBL_NODEATTRIBUTES} table — what the archive records about a node beyond its name and period.
 * One row per node, keyed by the node's id, with the fields numbered rather than named in the legacy schema: the
 * lookups (arkivbildare, institution, category, place, subject) and the free values (seriesignum, volymnummer,
 * hyllmeter, volymplacering and so on). Which of them are filled depends on the level: an archive names its
 * institution and arkivbildare, a series its signum and subject, a volume its number and placement.
 */
@Entity
@Table(name = "TBL_NODEATTRIBUTES")
public class NodeAttributesEntity {

	@Id
	@Column(name = "NODEID")
	private Integer nodeId;

	/** {@code FIELD1} — the arkivbildare when it is a legal entity. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIELD1")
	private LegalEntityEntity legalEntity;

	/** {@code FIELD2} — the arkivbildare when it is a person. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIELD2")
	private PersonEntity person;

	/** {@code FIELD3} — the institution holding the archive. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIELD3")
	private InstitutionEntity institution;

	/** {@code FIELD4} — the verksamhetskategori of the arkivbildare. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIELD4")
	private CategoryEntity category;

	/** {@code FIELD5} — the place the archive is about or comes from. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIELD5")
	private TopographyEntity topography;

	/** {@code FIELD6} — volymnummer. */
	@Column(name = "FIELD6", length = 100)
	private String volumeNumber;

	/** {@code FIELD7} — hyllmeter. */
	@Column(name = "FIELD7", precision = 10, scale = 2)
	private BigDecimal shelfMeters;

	/** {@code FIELD8} — volymantal. */
	@Column(name = "FIELD8")
	private Integer volumeCount;

	/** {@code FIELD9} — the OCM subject (ämne). */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIELD9")
	private OcmEntity subject;

	/** {@code FIELD10} — gammalt seriesignum. */
	@Column(name = "FIELD10", length = 100)
	private String oldSeriesSignum;

	/** {@code FIELD11} — nytt seriesignum. */
	@Column(name = "FIELD11", length = 100)
	private String seriesSignum;

	/** {@code FIELD12} — accessionsnummer. */
	@Column(name = "FIELD12", length = 100)
	private String accessionNumber;

	/** {@code FIELD13} — beståndskod. */
	@Column(name = "FIELD13", length = 100)
	private String holdingsCode;

	/** {@code FIELD14} — obestämd plats, the free-text place used when no topography fits. */
	@Column(name = "FIELD14", length = 100)
	private String locationText;

	/** {@code FIELD15} — volymplacering. */
	@Column(name = "FIELD15", length = 100)
	private String volumePlacement;

	/** {@code FIELD16} — the name of the history (historik/biografi) file, when one has been uploaded. */
	@Column(name = "FIELD16", length = 256)
	private String historyFilename;

	public static NodeAttributesEntity create() {
		return new NodeAttributesEntity();
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	public NodeAttributesEntity withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public LegalEntityEntity getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(final LegalEntityEntity legalEntity) {
		this.legalEntity = legalEntity;
	}

	public NodeAttributesEntity withLegalEntity(final LegalEntityEntity legalEntity) {
		this.legalEntity = legalEntity;
		return this;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(final PersonEntity person) {
		this.person = person;
	}

	public NodeAttributesEntity withPerson(final PersonEntity person) {
		this.person = person;
		return this;
	}

	public InstitutionEntity getInstitution() {
		return institution;
	}

	public void setInstitution(final InstitutionEntity institution) {
		this.institution = institution;
	}

	public NodeAttributesEntity withInstitution(final InstitutionEntity institution) {
		this.institution = institution;
		return this;
	}

	public CategoryEntity getCategory() {
		return category;
	}

	public void setCategory(final CategoryEntity category) {
		this.category = category;
	}

	public NodeAttributesEntity withCategory(final CategoryEntity category) {
		this.category = category;
		return this;
	}

	public TopographyEntity getTopography() {
		return topography;
	}

	public void setTopography(final TopographyEntity topography) {
		this.topography = topography;
	}

	public NodeAttributesEntity withTopography(final TopographyEntity topography) {
		this.topography = topography;
		return this;
	}

	public String getVolumeNumber() {
		return volumeNumber;
	}

	public void setVolumeNumber(final String volumeNumber) {
		this.volumeNumber = volumeNumber;
	}

	public NodeAttributesEntity withVolumeNumber(final String volumeNumber) {
		this.volumeNumber = volumeNumber;
		return this;
	}

	public BigDecimal getShelfMeters() {
		return shelfMeters;
	}

	public void setShelfMeters(final BigDecimal shelfMeters) {
		this.shelfMeters = shelfMeters;
	}

	public NodeAttributesEntity withShelfMeters(final BigDecimal shelfMeters) {
		this.shelfMeters = shelfMeters;
		return this;
	}

	public Integer getVolumeCount() {
		return volumeCount;
	}

	public void setVolumeCount(final Integer volumeCount) {
		this.volumeCount = volumeCount;
	}

	public NodeAttributesEntity withVolumeCount(final Integer volumeCount) {
		this.volumeCount = volumeCount;
		return this;
	}

	public OcmEntity getSubject() {
		return subject;
	}

	public void setSubject(final OcmEntity subject) {
		this.subject = subject;
	}

	public NodeAttributesEntity withSubject(final OcmEntity subject) {
		this.subject = subject;
		return this;
	}

	public String getOldSeriesSignum() {
		return oldSeriesSignum;
	}

	public void setOldSeriesSignum(final String oldSeriesSignum) {
		this.oldSeriesSignum = oldSeriesSignum;
	}

	public NodeAttributesEntity withOldSeriesSignum(final String oldSeriesSignum) {
		this.oldSeriesSignum = oldSeriesSignum;
		return this;
	}

	public String getSeriesSignum() {
		return seriesSignum;
	}

	public void setSeriesSignum(final String seriesSignum) {
		this.seriesSignum = seriesSignum;
	}

	public NodeAttributesEntity withSeriesSignum(final String seriesSignum) {
		this.seriesSignum = seriesSignum;
		return this;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public NodeAttributesEntity withAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
		return this;
	}

	public String getHoldingsCode() {
		return holdingsCode;
	}

	public void setHoldingsCode(final String holdingsCode) {
		this.holdingsCode = holdingsCode;
	}

	public NodeAttributesEntity withHoldingsCode(final String holdingsCode) {
		this.holdingsCode = holdingsCode;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public NodeAttributesEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getVolumePlacement() {
		return volumePlacement;
	}

	public void setVolumePlacement(final String volumePlacement) {
		this.volumePlacement = volumePlacement;
	}

	public NodeAttributesEntity withVolumePlacement(final String volumePlacement) {
		this.volumePlacement = volumePlacement;
		return this;
	}

	public String getHistoryFilename() {
		return historyFilename;
	}

	public void setHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
	}

	public NodeAttributesEntity withHistoryFilename(final String historyFilename) {
		this.historyFilename = historyFilename;
		return this;
	}

	/**
	 * Identity and the plain values only. The associations are lazy lookups and are left out, the way
	 * {@link CombinedObjectEntity} leaves out its own: comparing them would initialise a proxy, and two rows pointing
	 * at the same lookup are the same row already by their id.
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final NodeAttributesEntity that = (NodeAttributesEntity) o;
		return Objects.equals(nodeId, that.nodeId) && Objects.equals(volumeNumber, that.volumeNumber) && Objects.equals(shelfMeters, that.shelfMeters)
			&& Objects.equals(volumeCount, that.volumeCount) && Objects.equals(oldSeriesSignum, that.oldSeriesSignum) && Objects.equals(seriesSignum, that.seriesSignum)
			&& Objects.equals(accessionNumber, that.accessionNumber) && Objects.equals(holdingsCode, that.holdingsCode) && Objects.equals(locationText, that.locationText)
			&& Objects.equals(volumePlacement, that.volumePlacement) && Objects.equals(historyFilename, that.historyFilename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeId, volumeNumber, shelfMeters, volumeCount, oldSeriesSignum, seriesSignum, accessionNumber, holdingsCode, locationText,
			volumePlacement, historyFilename);
	}

	@Override
	public String toString() {
		return "NodeAttributesEntity{" +
			"nodeId=" + nodeId +
			", volumeNumber='" + volumeNumber + '\'' +
			", shelfMeters=" + shelfMeters +
			", volumeCount=" + volumeCount +
			", oldSeriesSignum='" + oldSeriesSignum + '\'' +
			", seriesSignum='" + seriesSignum + '\'' +
			", accessionNumber='" + accessionNumber + '\'' +
			", holdingsCode='" + holdingsCode + '\'' +
			", locationText='" + locationText + '\'' +
			", volumePlacement='" + volumePlacement + '\'' +
			", historyFilename='" + historyFilename + '\'' +
			'}';
	}
}
