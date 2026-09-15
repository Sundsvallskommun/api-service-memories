package se.sundsvall.memories.service.mapper;

import java.util.List;
import se.sundsvall.memories.api.model.Institution;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class InstitutionMapper {

	private InstitutionMapper() {}

	/**
	 * Map a single {@link InstitutionEntity} to an {@link Institution}.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Institution}, or {@code null} if {@code entity} is null
	 */
	public static Institution toInstitution(final InstitutionEntity entity) {
		return ofNullable(entity)
			.map(e -> Institution.create()
				.withInstitutionId(e.getId())
				.withCode(e.getCode())
				.withName(e.getName())
				.withDescription(e.getDescription())
				.withUrl(e.getUrl())
				.withEmail(e.getEmail()))
			.orElse(null);
	}

	/**
	 * Map a list of {@link InstitutionEntity} objects, keeping their order.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Institution} objects (empty if {@code entities} is null)
	 */
	public static List<Institution> toInstitutionList(final List<InstitutionEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(InstitutionMapper::toInstitution)
			.toList();
	}
}
