package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.Immutable;

/**
 * Read-only entity mapped to the {@code VW_MEMORY_OBJECTS} view: the union of the five object tables (FOTO, FILM,
 * LJUD, TEXT, PUBL) and the three registers (PERSON, JURPERS, SJOMAN). Backs the combined {@code /objects} search.
 */
@Entity
@Immutable
@Table(name = "VW_MEMORY_OBJECTS")
public class CombinedObjectEntity {

	@Id
	@Column(name = "OBJECT_KEY")
	private String objectKey;

	@Column(name = "SOURCE_ID")
	private Integer sourceId;

	@Column(name = "OBJECT_TYPE")
	private String objectType;

	@Column(name = "TITLE")
	private String title;

	/**
	 * The title, or the composed name for the registers. The column the relevance ordering reads, so a hit here
	 * outranks one that only occurred in {@link #searchText}.
	 */
	@Column(name = "NAME_TEXT")
	private String nameText;

	/**
	 * Title and comment concatenated by the view, and the only column the free-text filter reads. Selected with every
	 * row, and a comment can be several kilobytes.
	 */
	@Column(name = "SEARCH_TEXT")
	private String searchText;

	@Column(name = "SORT_YEAR")
	private Integer year;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOPOGRAPHY_ID")
	private TopographyEntity topography;

	@Column(name = "LOCATION_TEXT")
	private String locationText;

	/** {@code KON} for the branches that record one (PERSON, MANTAL), {@code NULL} everywhere else. */
	@Column(name = "GENDER")
	private String gender;

	/** The upphovsman (originator). The registers emit {@code NULL}; the objects use a sentinel id for "none". */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR_PERSON_ID")
	private PersonEntity creatorPerson;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR_LEGAL_ENTITY_ID")
	private LegalEntityEntity creatorLegalEntity;

	public static CombinedObjectEntity create() {
		return new CombinedObjectEntity();
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(final String objectKey) {
		this.objectKey = objectKey;
	}

	public CombinedObjectEntity withObjectKey(final String objectKey) {
		this.objectKey = objectKey;
		return this;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(final Integer sourceId) {
		this.sourceId = sourceId;
	}

	public CombinedObjectEntity withSourceId(final Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public CombinedObjectEntity withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public CombinedObjectEntity withTitle(final String title) {
		this.title = title;
		return this;
	}

	public String getNameText() {
		return nameText;
	}

	public void setNameText(final String nameText) {
		this.nameText = nameText;
	}

	public CombinedObjectEntity withNameText(final String nameText) {
		this.nameText = nameText;
		return this;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}

	public CombinedObjectEntity withSearchText(final String searchText) {
		this.searchText = searchText;
		return this;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(final Integer year) {
		this.year = year;
	}

	public CombinedObjectEntity withYear(final Integer year) {
		this.year = year;
		return this;
	}

	public TopographyEntity getTopography() {
		return topography;
	}

	public void setTopography(final TopographyEntity topography) {
		this.topography = topography;
	}

	public CombinedObjectEntity withTopography(final TopographyEntity topography) {
		this.topography = topography;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public CombinedObjectEntity withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public CombinedObjectEntity withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	public PersonEntity getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(final PersonEntity creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public CombinedObjectEntity withCreatorPerson(final PersonEntity creatorPerson) {
		this.creatorPerson = creatorPerson;
		return this;
	}

	public LegalEntityEntity getCreatorLegalEntity() {
		return creatorLegalEntity;
	}

	public void setCreatorLegalEntity(final LegalEntityEntity creatorLegalEntity) {
		this.creatorLegalEntity = creatorLegalEntity;
	}

	public CombinedObjectEntity withCreatorLegalEntity(final LegalEntityEntity creatorLegalEntity) {
		this.creatorLegalEntity = creatorLegalEntity;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CombinedObjectEntity that = (CombinedObjectEntity) o;
		return Objects.equals(objectKey, that.objectKey) && Objects.equals(sourceId, that.sourceId) && Objects.equals(objectType, that.objectType)
			&& Objects.equals(title, that.title) && Objects.equals(nameText, that.nameText) && Objects.equals(searchText, that.searchText) && Objects.equals(year, that.year)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(gender, that.gender);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectKey, sourceId, objectType, title, nameText, searchText, year, locationText, gender);
	}

	@Override
	public String toString() {
		return "CombinedObjectEntity{" +
			"objectKey='" + objectKey + '\'' +
			", sourceId=" + sourceId +
			", objectType='" + objectType + '\'' +
			", title='" + title + '\'' +
			", nameText='" + nameText + '\'' +
			", searchText='" + searchText + '\'' +
			", year=" + year +
			", locationText='" + locationText + '\'' +
			", gender='" + gender + '\'' +
			'}';
	}
}
