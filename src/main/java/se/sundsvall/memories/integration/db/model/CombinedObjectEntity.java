package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.Immutable;

/**
 * Read-only entity mapped to the {@code VW_MEMORY_OBJECTS} view — the union of the five object tables (FOTO incl.
 * Föremål, FILM, LJUD, TEXT, PUBL). Backs the combined {@code /objects} search so sorting and pagination happen
 * globally on the server side.
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

	@Column(name = "SORT_YEAR")
	private Integer year;

	@Column(name = "TOPOGRAPHY_ID")
	private Integer topographyId;

	@Column(name = "LOCATION_TEXT")
	private String locationText;

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

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public CombinedObjectEntity withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
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

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CombinedObjectEntity that = (CombinedObjectEntity) o;
		return Objects.equals(objectKey, that.objectKey) && Objects.equals(sourceId, that.sourceId) && Objects.equals(objectType, that.objectType)
			&& Objects.equals(title, that.title) && Objects.equals(year, that.year) && Objects.equals(topographyId, that.topographyId) && Objects.equals(locationText, that.locationText);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectKey, sourceId, objectType, title, year, topographyId, locationText);
	}

	@Override
	public String toString() {
		return "CombinedObjectEntity{" +
			"objectKey='" + objectKey + '\'' +
			", sourceId=" + sourceId +
			", objectType='" + objectType + '\'' +
			", title='" + title + '\'' +
			", year=" + year +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			'}';
	}
}
