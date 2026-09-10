package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Topography (place) model")
public class Topography {

	@Schema(description = "Topography ID, which the topographyId filter accepts", examples = "1")
	private Integer topographyId;

	@Schema(description = "The place as it is shown, and as an object reports its location: the specific place and the wider one it sits in. "
		+ "The wider one alone is shared by every place in it, so it is not enough to pick from.", examples = "Bredbyn, Anundsjö")
	private String displayName;

	@Schema(description = "The wider place (TOPNAMN) — a parish (socken) in the Swedish material, shared by every place in it", examples = "Anundsjö")
	private String name;

	@Schema(description = "Code of the wider place (TOPKOD), shared by every place under it. A code, not a name.", examples = "228471")
	private String code;

	@Schema(description = "The specific place (PLATS) — a village or a farm. What tells one row from another.", examples = "Bredbyn")
	private String place;

	@Schema(description = "Municipality (LAND). The column is named for a country but holds a municipality.", examples = "Örnsköldsvik")
	private String municipality;

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

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(final String municipality) {
		this.municipality = municipality;
	}

	public Topography withMunicipality(final String municipality) {
		this.municipality = municipality;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Topography that = (Topography) o;
		return Objects.equals(topographyId, that.topographyId) && Objects.equals(displayName, that.displayName) && Objects.equals(name, that.name)
			&& Objects.equals(code, that.code) && Objects.equals(place, that.place) && Objects.equals(municipality, that.municipality);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topographyId, displayName, name, code, place, municipality);
	}

	@Override
	public String toString() {
		return "Topography{" +
			"topographyId=" + topographyId +
			", displayName='" + displayName + '\'' +
			", name='" + name + '\'' +
			", code='" + code + '\'' +
			", place='" + place + '\'' +
			", municipality='" + municipality + '\'' +
			'}';
	}
}
