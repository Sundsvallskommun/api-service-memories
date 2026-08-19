package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "TOPOGRAFI")
public class TopographyEntity {

	@Id
	@Column(name = "T_ID")
	private Integer id;

	@Column(name = "TOPNAMN", length = 64)
	private String name;

	@Column(name = "TOPKOD", length = 6)
	private String code;

	@Column(name = "PLATS", length = 64)
	private String place;

	@Column(name = "LAND", length = 64)
	private String country;

	public static TopographyEntity create() {
		return new TopographyEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public TopographyEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public TopographyEntity withName(final String name) {
		this.name = name;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public TopographyEntity withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public TopographyEntity withPlace(final String place) {
		this.place = place;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public TopographyEntity withCountry(final String country) {
		this.country = country;
		return this;
	}

	/**
	 * Resolves this entry to the string used to present a place. Prefers {@code TOPNAMN}, falls back to {@code PLATS},
	 * then {@code TOPKOD}. Blank values are treated as absent, since the legacy data uses empty strings rather than
	 * {@code NULL}.
	 *
	 * @return the display name, or {@code null} if all three columns are missing or blank
	 */
	public String getDisplayName() {
		return Optional.ofNullable(name).filter(s -> !s.isBlank())
			.or(() -> Optional.ofNullable(place).filter(s -> !s.isBlank()))
			.or(() -> Optional.ofNullable(code).filter(s -> !s.isBlank()))
			.orElse(null);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TopographyEntity that = (TopographyEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(code, that.code) && Objects.equals(place, that.place)
			&& Objects.equals(country, that.country);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, code, place, country);
	}

	@Override
	public String toString() {
		return "TopographyEntity{" +
			"id=" + id +
			", name='" + name + '\'' +
			", code='" + code + '\'' +
			", place='" + place + '\'' +
			", country='" + country + '\'' +
			'}';
	}
}
