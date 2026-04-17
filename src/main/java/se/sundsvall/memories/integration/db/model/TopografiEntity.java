package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "TOPOGRAFI")
public class TopografiEntity {

	@Id
	@Column(name = "T_ID")
	private Integer tId;

	@Column(name = "TOPNAMN", length = 64)
	private String topNamn;

	@Column(name = "TOPKOD", length = 6)
	private String topKod;

	@Column(name = "PLATS", length = 64)
	private String plats;

	@Column(name = "LAND", length = 64)
	private String land;

	public static TopografiEntity create() {
		return new TopografiEntity();
	}

	public Integer getTId() {
		return tId;
	}

	public void setTId(final Integer tId) {
		this.tId = tId;
	}

	public TopografiEntity withTId(final Integer tId) {
		this.tId = tId;
		return this;
	}

	public String getTopNamn() {
		return topNamn;
	}

	public void setTopNamn(final String topNamn) {
		this.topNamn = topNamn;
	}

	public TopografiEntity withTopNamn(final String topNamn) {
		this.topNamn = topNamn;
		return this;
	}

	public String getTopKod() {
		return topKod;
	}

	public void setTopKod(final String topKod) {
		this.topKod = topKod;
	}

	public TopografiEntity withTopKod(final String topKod) {
		this.topKod = topKod;
		return this;
	}

	public String getPlats() {
		return plats;
	}

	public void setPlats(final String plats) {
		this.plats = plats;
	}

	public TopografiEntity withPlats(final String plats) {
		this.plats = plats;
		return this;
	}

	public String getLand() {
		return land;
	}

	public void setLand(final String land) {
		this.land = land;
	}

	public TopografiEntity withLand(final String land) {
		this.land = land;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TopografiEntity that = (TopografiEntity) o;
		return Objects.equals(tId, that.tId) && Objects.equals(topNamn, that.topNamn) && Objects.equals(topKod, that.topKod) && Objects.equals(plats, that.plats)
			&& Objects.equals(land, that.land);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tId, topNamn, topKod, plats, land);
	}

	@Override
	public String toString() {
		return "TopografiEntity{" +
			"TId=" + tId +
			", topNamn='" + topNamn + '\'' +
			", topKod='" + topKod + '\'' +
			", plats='" + plats + '\'' +
			", land='" + land + '\'' +
			'}';
	}
}
