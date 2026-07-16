package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Seaman;
import se.sundsvall.memories.integration.db.model.SeamanEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class SeamanMapper {

	private SeamanMapper() {}

	/**
	 * Map a single {@link SeamanEntity} to a {@link Seaman} API model.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Seaman}, or {@code null} if {@code entity} is null
	 */
	public static Seaman toSeaman(final SeamanEntity entity) {
		return ofNullable(entity)
			.map(e -> Seaman.create()
				.withId(e.getId())
				.withFirstName(e.getFirstName())
				.withLastName1(e.getLastName1())
				.withLastName2(e.getLastName2())
				.withIdNumber(e.getIdNumber())
				.withBirthDate(e.getBirthDate())
				.withAge(e.getAge())
				.withBirthParish(e.getBirthParish())
				.withBirthPlace(e.getBirthPlace())
				.withHomeParish(e.getHomeParish())
				.withHomePlace(e.getHomePlace())
				.withCivilStatus(e.getCivilStatus())
				.withFather(e.getFather())
				.withMother(e.getMother())
				.withSeamensHouse(e.getSeamensHouse())
				.withEnrollmentNumber(e.getEnrollmentNumber())
				.withEnrollmentDate(e.getEnrollmentDate())
				.withRank(e.getRank())
				.withSignOnPlace(e.getSignOnPlace())
				.withSignOnDate(e.getSignOnDate())
				.withSignOffPlace(e.getSignOffPlace())
				.withSignOffDate(e.getSignOffDate())
				.withShip(e.getShip())
				.withHomePort(e.getHomePort())
				.withShipType(e.getShipType())
				.withShipOwner(e.getShipOwner())
				.withCaptain(e.getCaptain())
				.withDestination(e.getDestination())
				.withOther(e.getOther())
				.withNote(e.getNote())
				.withArchive(e.getArchive())
				.withVolume(e.getVolume())
				.withArchiveNumber(e.getArchiveNumber())
				.withPage(e.getPage()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link SeamanEntity} objects to {@link Seaman} API models.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Seaman} objects (empty if {@code entities} is null)
	 */
	public static List<Seaman> toSeamanList(final List<SeamanEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(SeamanMapper::toSeaman)
			.toList();
	}
}
