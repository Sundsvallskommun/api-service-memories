package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

@Entity
@Table(name = "TOPOGRAFI")
public class TopographyEntity {

	@Id
	@Column(name = "T_ID")
	private Integer id;

	/** {@code TOPNAMN} — the wider place, a parish (socken) in the Swedish material. Shared by every place in it. */
	@Column(name = "TOPNAMN", length = 64)
	private String name;

	/** {@code TOPKOD} — the code of that wider place, not a name. Shared by every row under it. */
	@Column(name = "TOPKOD", length = 6)
	private String code;

	/** {@code PLATS} — the specific place inside it, a village or a farm. What distinguishes one row from another. */
	@Column(name = "PLATS", length = 64)
	private String place;

	/**
	 * {@code LAND} — the municipality, despite the column's name: the rows under Anundsjö carry {@code Örnsköldsvik},
	 * not {@code Sverige}.
	 */
	@Column(name = "LAND", length = 64)
	private String municipality;

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

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(final String municipality) {
		this.municipality = municipality;
	}

	public TopographyEntity withMunicipality(final String municipality) {
		this.municipality = municipality;
		return this;
	}

	/**
	 * Resolves this entry to the string a place is presented under: the specific place and the wider one it sits in,
	 * {@code PLATS, TOPNAMN}. {@code TOPNAMN} on its own names a whole parish and is shared by every place in it — a
	 * couple of thousand rows resolve to some 160 parish names — so a list showing it alone cannot be picked from.
	 * Either column carries the label when the other is blank. {@code TOPKOD} is a code rather than a name and is never
	 * shown. Blank values are treated as absent, since the legacy data uses empty strings rather than {@code NULL}.
	 *
	 * @return the display name, or {@code null} if neither column holds anything
	 */
	public String getDisplayName() {
		final var label = Stream.of(place, name)
			.map(part -> ofNullable(part).map(String::trim).orElse(""))
			.filter(not(String::isEmpty))
			.collect(joining(", "));
		return ofNullable(label).filter(not(String::isEmpty)).orElse(null);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TopographyEntity that = (TopographyEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(code, that.code) && Objects.equals(place, that.place)
			&& Objects.equals(municipality, that.municipality);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, code, place, municipality);
	}

	@Override
	public String toString() {
		return "TopographyEntity{" +
			"id=" + id +
			", name='" + name + '\'' +
			", code='" + code + '\'' +
			", place='" + place + '\'' +
			", municipality='" + municipality + '\'' +
			'}';
	}
}
