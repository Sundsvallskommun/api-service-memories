package se.sundsvall.memories.integration.db.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@code TEXT_MULTI}: {@code IID} is the parent TEXT id, {@code MIID} a per-text sequence
 * number. Mirrors the source schema's {@code PRIMARY KEY (IID, MIID)} — field names must match the {@code @Id} fields
 * of {@link TextMediaEntity} ({@code textId}, {@code id}).
 */
public class TextMediaId implements Serializable {

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
