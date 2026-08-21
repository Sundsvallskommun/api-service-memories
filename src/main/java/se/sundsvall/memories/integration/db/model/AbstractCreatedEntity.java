package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

/**
 * The upphovsman (originator) an object points at — a person, a legal entity, or neither. Every object table carries
 * the pair, so it is mapped once here; the two that name the columns differently ({@code FILM_U_*}, {@code LJUD_U_*})
 * say so with {@code @AssociationOverride}.
 *
 * <p>
 * Both columns default to a sentinel row rather than to {@code NULL}, which
 * {@link se.sundsvall.memories.service.mapper.CreatorMapper} reads as absent.
 *
 * <p>
 * There are no fluent builders here: the application only ever reads these associations.
 */
@MappedSuperclass
public abstract class AbstractCreatedEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "U_E_ID")
	protected PersonEntity creatorPerson;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "U_J_ID")
	protected LegalEntityEntity creatorLegalEntity;

	public PersonEntity getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(final PersonEntity creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public LegalEntityEntity getCreatorLegalEntity() {
		return creatorLegalEntity;
	}

	public void setCreatorLegalEntity(final LegalEntityEntity creatorLegalEntity) {
		this.creatorLegalEntity = creatorLegalEntity;
	}
}
