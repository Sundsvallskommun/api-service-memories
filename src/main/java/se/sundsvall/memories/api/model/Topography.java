package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Topography (place) model")
public class Topography {

	@Schema(description = "Topography ID, which the topographyId filter accepts", examples = "1")
	private Integer topographyId;

	@Schema(description = "Place name, as shown for an object's location: the name, or failing that the place or the code", examples = "Sundsvall")
	private String displayName;

	@Schema(description = "Name (TOPNAMN)", examples = "Sundsvall")
	private String name;

	@Schema(description = "Code (TOPKOD)", examples = "SUN")
	private String code;

	@Schema(description = "Place (PLATS), typically the municipality", examples = "Sundsvalls kommun")
	private String place;

	@Schema(description = "Country (LAND)", examples = "Sverige")
	private String country;

	public static Topography create() {
		return new Topography();
	}

	public Integer getTopographyId() {
		return topographyId;
	}

	public void setTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
	}

	public Topography withTopographyId(final Integer topographyId) {
		this.topographyId = topographyId;
		return this;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public Topography withDisplayName(final String displayName) {
		this.displayName = displayName;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Topography withName(final String name) {
		this.name = name;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public Topography withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public Topography withPlace(final String place) {
		this.place = place;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public Topography withCountry(final String country) {
		this.country = country;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Topography that = (Topography) o;
		return Objects.equals(topographyId, that.topographyId) && Objects.equals(displayName, that.displayName) && Objects.equals(name, that.name)
			&& Objects.equals(code, that.code) && Objects.equals(place, that.place) && Objects.equals(country, that.country);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topographyId, displayName, name, code, place, country);
	}

	@Override
	public String toString() {
		return "Topography{" +
			"topographyId=" + topographyId +
			", displayName='" + displayName + '\'' +
			", name='" + name + '\'' +
			", code='" + code + '\'' +
			", place='" + place + '\'' +
			", country='" + country + '\'' +
			'}';
	}
}
