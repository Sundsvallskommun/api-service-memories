package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "FOTO_OCM")
public class FotoOcmEntity {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "F_ID1")
	private Integer photoId;

	@Column(name = "O_ID")
	private Integer ocmId;

	public static FotoOcmEntity create() {
		return new FotoOcmEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public FotoOcmEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public Integer getPhotoId() {
		return photoId;
	}

	public void setPhotoId(final Integer photoId) {
		this.photoId = photoId;
	}

	public FotoOcmEntity withPhotoId(final Integer photoId) {
		this.photoId = photoId;
		return this;
	}

	public Integer getOcmId() {
		return ocmId;
	}

	public void setOcmId(final Integer ocmId) {
		this.ocmId = ocmId;
	}

	public FotoOcmEntity withOcmId(final Integer ocmId) {
		this.ocmId = ocmId;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final FotoOcmEntity that = (FotoOcmEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(photoId, that.photoId) && Objects.equals(ocmId, that.ocmId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, photoId, ocmId);
	}

	@Override
	public String toString() {
		return "FotoOcmEntity{" +
			"id=" + id +
			", photoId=" + photoId +
			", ocmId=" + ocmId +
			'}';
	}
}
