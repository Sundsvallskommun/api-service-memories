package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "PUBL_TYP")
public class PublTypEntity {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "PUBLIKTYP", length = 256)
	private String publiktyp;

	public static PublTypEntity create() {
		return new PublTypEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public PublTypEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getPubliktyp() {
		return publiktyp;
	}

	public void setPubliktyp(final String publiktyp) {
		this.publiktyp = publiktyp;
	}

	public PublTypEntity withPubliktyp(final String publiktyp) {
		this.publiktyp = publiktyp;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PublTypEntity that = (PublTypEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(publiktyp, that.publiktyp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, publiktyp);
	}

	@Override
	public String toString() {
		return "PublTypEntity{" +
			"id=" + id +
			", publiktyp='" + publiktyp + '\'' +
			'}';
	}
}
