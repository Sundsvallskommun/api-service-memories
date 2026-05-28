package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "TEXT_MULTI")
@IdClass(TextMediaEntity.TextMediaId.class)
public class TextMediaEntity {

	@Id
	@Column(name = "IID")
	private Integer textId;

	@Id
	@Column(name = "MIID")
	private Integer id;

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
		return Objects.equals(textId, that.textId) && Objects.equals(id, that.id) && Objects.equals(thumbnailFilename, that.thumbnailFilename)
			&& Objects.equals(largeImageFilename, that.largeImageFilename) && Objects.equals(originalFilename, that.originalFilename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(textId, id, thumbnailFilename, largeImageFilename, originalFilename);
	}

	@Override
	public String toString() {
		return "TextMediaEntity{" +
			"textId=" + textId +
			", id=" + id +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", originalFilename='" + originalFilename + '\'' +
			'}';
	}

	/**
	 * Composite primary key for {@code TEXT_MULTI}: {@code IID} is the parent TEXT id, {@code MIID} a per-text sequence
	 * number. Mirrors the source schema's {@code PRIMARY KEY (IID, MIID)} — field names must match the entity's
	 * {@code @Id} fields ({@code textId}, {@code id}).
	 */
	public static class TextMediaId implements Serializable {

		private Integer textId;
		private Integer id;

		public TextMediaId() {}

		public TextMediaId(final Integer textId, final Integer id) {
			this.textId = textId;
			this.id = id;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null || getClass() != o.getClass())
				return false;
			final TextMediaId that = (TextMediaId) o;
			return Objects.equals(textId, that.textId) && Objects.equals(id, that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(textId, id);
		}
	}
}
