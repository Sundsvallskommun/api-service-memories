package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Extra media file attached to a text (TEXT_MULTI)")
public class TextMediaFile {

	@Schema(description = "Media file ID (per-text sequence number, MIID)", examples = "1")
	private Integer id;

	@Schema(description = "Thumbnail file name", examples = "TEXT.id_1001.multi_1.fil_liten.jpeg")
	private String thumbnailFilename;

	@Schema(description = "Large image file name", examples = "TEXT.id_1001.multi_1.fil_stor.jpeg")
	private String largeImageFilename;

	@Schema(description = "Original file name", examples = "TEXT.id_1001.multi_1.fil_original.jpeg")
	private String originalFilename;

	public static TextMediaFile create() {
		return new TextMediaFile();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public TextMediaFile withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	public void setThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	public TextMediaFile withThumbnailFilename(final String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
		return this;
	}

	public String getLargeImageFilename() {
		return largeImageFilename;
	}

	public void setLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
	}

	public TextMediaFile withLargeImageFilename(final String largeImageFilename) {
		this.largeImageFilename = largeImageFilename;
		return this;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public TextMediaFile withOriginalFilename(final String originalFilename) {
		this.originalFilename = originalFilename;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final TextMediaFile that = (TextMediaFile) o;
		return Objects.equals(id, that.id) && Objects.equals(thumbnailFilename, that.thumbnailFilename) && Objects.equals(largeImageFilename, that.largeImageFilename)
			&& Objects.equals(originalFilename, that.originalFilename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, thumbnailFilename, largeImageFilename, originalFilename);
	}

	@Override
	public String toString() {
		return "TextMediaFile{" +
			"id=" + id +
			", thumbnailFilename='" + thumbnailFilename + '\'' +
			", largeImageFilename='" + largeImageFilename + '\'' +
			", originalFilename='" + originalFilename + '\'' +
			'}';
	}
}
