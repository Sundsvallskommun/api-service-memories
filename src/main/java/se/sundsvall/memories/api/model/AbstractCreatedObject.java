package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The upphovsman (originator) every object type reports, declared once rather than in five identical copies.
 *
 * <p>
 * The type parameter is the concrete model, so {@link #withCreator(Creator)} returns it and the mappers keep building
 * in one expression.
 *
 * @param <T> the concrete object model
 */
public abstract class AbstractCreatedObject<T extends AbstractCreatedObject<T>> {

	@Schema(implementation = Creator.class)
	protected Creator creator;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(final Creator creator) {
		this.creator = creator;
	}

	@SuppressWarnings("unchecked")
	public T withCreator(final Creator creator) {
		this.creator = creator;
		return (T) this;
	}
}
