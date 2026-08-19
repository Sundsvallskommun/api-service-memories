package se.sundsvall.memories.service.mapper;

import java.util.Optional;
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
 */
final class CreatorMapper {

	private static final Integer PERSON_PLACEHOLDER_ID = 0;

	private static final Integer LEGAL_ENTITY_PLACEHOLDER_ID = 1;

	private CreatorMapper() {}

	static Integer personId(final PersonEntity person) {
		return realPerson(person)
			.map(PersonEntity::getPersonId)
			.orElse(null);
	}

	static String personName(final PersonEntity person) {
		return realPerson(person)
			.map(PersonEntity::getDisplayName)
			.orElse(null);
	}

	static Integer legalEntityId(final LegalEntityEntity legalEntity) {
		return realLegalEntity(legalEntity)
			.map(LegalEntityEntity::getLegalEntityId)
			.orElse(null);
	}

	static String legalEntityName(final LegalEntityEntity legalEntity) {
		return realLegalEntity(legalEntity)
			.map(LegalEntityEntity::getName)
			.orElse(null);
	}

	private static Optional<PersonEntity> realPerson(final PersonEntity person) {
		return ofNullable(person)
			.filter(candidate -> !PERSON_PLACEHOLDER_ID.equals(candidate.getPersonId()));
	}

	private static Optional<LegalEntityEntity> realLegalEntity(final LegalEntityEntity legalEntity) {
		return ofNullable(legalEntity)
			.filter(candidate -> !LEGAL_ENTITY_PLACEHOLDER_ID.equals(candidate.getLegalEntityId()));
	}
}
