package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Combined object search hit (any object type)")
public class CombinedObject {

	@Schema(description = "Stable key across types ({type}-{id})", examples = "foto-1001")
	private String objectKey;

	@Schema(description = "Source id within its own type", examples = "1001")
	private Integer sourceId;

	@Schema(description = "Object type", examples = "Foto")
	private String objectType;

	@Schema(description = "Title", examples = "Stadsvy från Norra berget")
	private String title;

	@Schema(description = "Year (derived from the object's date; used for sorting and display)", examples = "1920")
	private Integer year;

	@Schema(description = "Topography ID", examples = "1")
	private Integer topographyId;

	@Schema(description = "Free-text location", examples = "Sundsvall")
	private String locationText;

	@Schema(description = "Resolved place name from TOPOGRAFI (preferred over locationText when set)", examples = "Sundsvall")
	private String location;

	public static CombinedObject create() {
		return new CombinedObject();
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(final String objectKey) {
		this.objectKey = objectKey;
	}

	public CombinedObject withObjectKey(final String objectKey) {
		this.objectKey = objectKey;
		return this;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(final Integer sourceId) {
		this.sourceId = sourceId;
	}

	public CombinedObject withSourceId(final Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public CombinedObject withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public CombinedObject withTitle(final String title) {
		this.title = title;
		return this;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(final Integer year) {
		this.year = year;
	}

	public CombinedObject withYear(final Integer year) {
		this.year = year;
		return this;
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public CombinedObject withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(final String locationText) {
		this.locationText = locationText;
	}

	public CombinedObject withLocationText(final String locationText) {
		this.locationText = locationText;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public CombinedObject withLocation(final String location) {
		this.location = location;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CombinedObject that = (CombinedObject) o;
		return Objects.equals(objectKey, that.objectKey) && Objects.equals(sourceId, that.sourceId) && Objects.equals(objectType, that.objectType)
			&& Objects.equals(title, that.title) && Objects.equals(year, that.year) && Objects.equals(topographyId, that.topographyId)
			&& Objects.equals(locationText, that.locationText) && Objects.equals(location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectKey, sourceId, objectType, title, year, topographyId, locationText, location);
	}

	@Override
	public String toString() {
		return "CombinedObject{" +
			"objectKey='" + objectKey + '\'' +
			", sourceId=" + sourceId +
			", objectType='" + objectType + '\'' +
			", title='" + title + '\'' +
			", year=" + year +
			", topographyId=" + topographyId +
			", locationText='" + locationText + '\'' +
			", location='" + location + '\'' +
			'}';
	}
}
