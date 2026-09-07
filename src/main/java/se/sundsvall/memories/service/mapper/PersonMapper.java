package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Person;
import se.sundsvall.memories.integration.db.model.Gender;
import se.sundsvall.memories.integration.db.model.PersonEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class PersonMapper {

	private PersonMapper() {}

	/**
	 * Map a single {@link PersonEntity} to a {@link Person} API model.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Person}, or {@code null} if {@code entity} is null
	 */
	public static Person toPerson(final PersonEntity entity) {
		return ofNullable(entity)
			.map(e -> Person.create()
				.withPersonId(e.getPersonId())
				.withPersonNumber(e.getPersonNumber())
				.withLastName(e.getLastName())
				.withFirstName(e.getFirstName())
				.withGender(toGender(e.getGender()))
				.withBirthDate(e.getBirthDate())
				.withBirthParish(e.getBirthParish())
				.withDeathDate(e.getDeathDate())
				.withOccupation(e.getOccupation())
				.withRelatedPersonName(e.getRelatedPersonName())
				.withRelatedPersonOccupation(e.getRelatedPersonOccupation())
				.withMovedInParish(e.getMovedInParish())
				.withMovedOutParish(e.getMovedOutParish())
				.withSources(e.getSources())
				.withComment(e.getComment())
				.withBiographyFilename(e.getBiographyFilename())
				.withOptions(e.getOptions())
				.withDeletedDate(e.getDeletedDate()))
			.orElse(null);
	}

	/**
	 * The register writes the gender as a word; the API emits the canonical label, and Okänt for a value naming no
	 * gender, so a person reads the same here as in the census records and the combined search.
	 */
	private static String toGender(final String stored) {
		return Gender.ofSource(stored).getLabel();
	}

	/**
	 * Map a list of {@link PersonEntity} objects to {@link Person} API models.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Person} objects (empty if {@code entities} is null)
	 */
	public static List<Person> toPersonList(final List<PersonEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(PersonMapper::toPerson)
			.toList();
	}
}
