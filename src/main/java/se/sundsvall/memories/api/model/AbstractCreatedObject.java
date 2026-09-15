package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * What every object type reports alike, declared once rather than in five identical copies: the upphovsman
 * (originator), and the archive node the object was created in.
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

	@Schema(description = "ID of the archive node (arkiv, serie or volym) the object was created in, or null when it sits in none. Resolve it with /nodes/{id} for the name and the path", examples = "19000")
	protected Integer nodeId;

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

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
	}

	@SuppressWarnings("unchecked")
	public T withNodeId(final Integer nodeId) {
		this.nodeId = nodeId;
		return (T) this;
	}
}
