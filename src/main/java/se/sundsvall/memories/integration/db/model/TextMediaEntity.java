package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "TEXT_MULTI")
public class TextMediaEntity {

	@Id
	@Column(name = "MIID")
	private Integer id;

	@Column(name = "IID")
	private Integer textId;

	@Column(name = "FIL_LITEN", length = 256)
	private String thumbnailFilename;

	@Column(name = "FIL_STOR", length = 256)
	private String largeImageFilename;

	@Column(name = "FIL_ORIGINAL", length = 256)
	private String originalFilename;

	public static TextMediaEntity create() {
		return new TextMediaEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public TextMediaEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public Integer getTextId() {
		return textId;
	}

	public void setTextId(final Integer textId) {
		this.textId = textId;
	}

	public TextMediaEntity withTextId(final Integer textId) {
		this.textId = textId;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public TextMediaEntity withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public TextMediaEntity withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public TextMediaEntity withOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TextMediaEntity that = (TextMediaEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(textId, that.textId) && Objects.equals(thumbnailFilename, that.thumbnailFilename)
			&& Objects.equals(largeImageFilename, that.largeImageFilename) && Objects.equals(originalFilename, that.originalFilename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, textId, thumbnailFilename, largeImageFilename, originalFilename);
	}

	@Override
	public String toString() {
		return "TextMediaEntity{" +
			"id=" + id +
			", textId=" + textId +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", originalFilename='" + originalFilename + '\'' +
			'}';
	}
}
