package se.sundsvall.memories.service.mapper;

import java.util.Optional;
import se.sundsvall.memories.api.model.Creator;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static java.util.Optional.ofNullable;

/**
 * Maps the upphovsman (originator) an object points at — a person, a legal entity, or neither.
 *
 * <p>
 * Both columns default to a sentinel rather than to {@code NULL}: {@code U_E_ID} to {@code 0} ("ingen person") and
 * {@code U_J_ID} to {@code 1} ("ingen"). Those rows exist in {@code PERSON} and {@code JURPERS} and are perfectly
 * loadable, so without this the API would report an originator named "Ingen" on nearly every object. They are read as
 * absent instead, the same way the person and legal entity searches exclude them.
 *
 * <p>
 * A soft-deleted register record is read as absent too. Its own endpoint answers {@code 404}, so naming it here would
 * publish a reference the API then refuses to resolve — and the name of a record the archive has deleted.
 */
final class CreatorMapper {

	private static final Integer PERSON_PLACEHOLDER_ID = 0;

	private static final Integer LEGAL_ENTITY_PLACEHOLDER_ID = 1;

	private CreatorMapper() {}

	/**
	 * The originator of an object, or {@code null} when it has none — which is the common case, since both columns
	 * default to a sentinel.
	 *
	 * @param  person      the association behind {@code U_E_ID}
	 * @param  legalEntity the association behind {@code U_J_ID}
	 * @return             the mapped {@link Creator}, or {@code null} when neither points at a real row
	 */
	static Creator toCreator(final PersonEntity person, final LegalEntityEntity legalEntity) {
		final var creator = Creator.create()
			.withPersonId(personId(person))
			.withPerson(personName(person))
			.withLegalEntityId(legalEntityId(legalEntity))
			.withLegalEntity(legalEntityName(legalEntity));

		return Optional.of(creator)
			.filter(candidate -> !candidate.equals(Creator.create()))
			.orElse(null);
	}

	private static Integer personId(final PersonEntity person) {
		return realPerson(person)
			.map(PersonEntity::getPersonId)
			.orElse(null);
	}

	private static String personName(final PersonEntity person) {
		return realPerson(person)
			.map(PersonEntity::getDisplayName)
			.orElse(null);
	}

	private static Integer legalEntityId(final LegalEntityEntity legalEntity) {
		return realLegalEntity(legalEntity)
			.map(LegalEntityEntity::getLegalEntityId)
			.orElse(null);
	}

	private static String legalEntityName(final LegalEntityEntity legalEntity) {
		return realLegalEntity(legalEntity)
			.map(LegalEntityEntity::getName)
			.orElse(null);
	}

	private static Optional<PersonEntity> realPerson(final PersonEntity person) {
		return ofNullable(person)
			.filter(candidate -> !PERSON_PLACEHOLDER_ID.equals(candidate.getPersonId()))
			.filter(candidate -> candidate.getDeletedDate() == null);
	}

	private static Optional<LegalEntityEntity> realLegalEntity(final LegalEntityEntity legalEntity) {
		return ofNullable(legalEntity)
			.filter(candidate -> !LEGAL_ENTITY_PLACEHOLDER_ID.equals(candidate.getLegalEntityId()))
			.filter(candidate -> candidate.getDeletedDate() == null);
	}
}
