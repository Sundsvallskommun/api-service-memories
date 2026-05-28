package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "PUBL_TYP")
public class PublicationTypeEntity {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "PUBLIKTYP", length = 40)
	private String publicationType;

	public static PublicationTypeEntity create() {
		return new PublicationTypeEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public PublicationTypeEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(final String publicationType) {
		this.publicationType = publicationType;
	}

	public PublicationTypeEntity withPublicationType(final String publicationType) {
		this.publicationType = publicationType;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PublicationTypeEntity that = (PublicationTypeEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(publicationType, that.publicationType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, publicationType);
	}

	@Override
	public String toString() {
		return "PublicationTypeEntity{" +
			"id=" + id +
			", publicationType='" + publicationType + '\'' +
			'}';
	}
}
