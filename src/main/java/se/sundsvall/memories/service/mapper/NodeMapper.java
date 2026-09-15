package se.sundsvall.memories.service.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import se.sundsvall.memories.api.model.Creator;
import se.sundsvall.memories.api.model.Node;
import se.sundsvall.memories.api.model.NodeDetail;
import se.sundsvall.memories.api.model.Subject;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.NodeAttributesEntity;
import se.sundsvall.memories.integration.db.model.NodeEntity;
import se.sundsvall.memories.integration.db.model.NodeTypeEntity;
import se.sundsvall.memories.integration.db.model.OcmEntity;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

public final class NodeMapper {

	private NodeMapper() {}

	/**
	 * Map a single {@link NodeEntity} to a {@link Node} API model. The node type is read through the association, which
	 * is {@code null} both when the node has no type and when {@code NODETYPEID} points at a row that does not exist.
	 * The attribute row is optional too, and every field read from it is simply absent when there is none.
	 *
	 * @param  entity the source entity
	 * @return        the mapped {@link Node}, or {@code null} if {@code entity} is null
	 */
	public static Node toNode(final NodeEntity entity) {
		return ofNullable(entity)
			.map(NodeMapper::toNodeWithAttributes)
			.orElse(null);
	}

	private static Node toNodeWithAttributes(final NodeEntity entity) {
		final var attributes = ofNullable(entity.getAttributes());
		final var creator = attributes
			.map(a -> CreatorMapper.toCreator(a.getPerson(), a.getLegalEntity()))
			.orElse(null);
		final var legalEntity = attributes.flatMap(NodeMapper::realLegalEntity);
		final var institution = attributes.map(NodeAttributesEntity::getInstitution);
		final var category = attributes.map(NodeAttributesEntity::getCategory);
		final var topography = attributes.map(NodeAttributesEntity::getTopography);

		return Node.create()
			.withId(entity.getId())
			.withParentId(entity.getParentId())
			.withName(name(entity, creator, attributes))
			.withNodeTypeId(nodeTypeId(entity))
			.withNodeType(nodeTypeName(entity))
			.withStartYear(entity.getStartYear())
			.withStopYear(entity.getStopYear())
			.withDescription(entity.getDescription())
			.withSortOrder(entity.getSortOrder())
			.withSubItemCount(entity.getSubItemCount())
			.withPublishedSubItemCount(entity.getPublishedSubItemCount())
			.withOptions(entity.getOptions())
			.withCreator(creator)
			.withActivityStartDate(legalEntity.map(LegalEntityEntity::getStartDate).orElse(null))
			.withActivityEndDate(legalEntity.map(LegalEntityEntity::getEndDate).orElse(null))
			.withInstitutionId(institution.map(InstitutionEntity::getId).orElse(null))
			.withInstitution(institution.map(InstitutionEntity::getName).orElse(null))
			.withInstitutionCode(institution.map(InstitutionEntity::getCode).orElse(null))
			.withCategoryId(category.map(CategoryEntity::getCategoryId).orElse(null))
			.withCategory(category.map(CategoryEntity::getName).orElse(null))
			.withTopographyId(topography.map(TopographyEntity::getId).orElse(null))
			.withLocation(topography.map(TopographyEntity::getDisplayName).orElse(null))
			.withLocationText(attributes.map(NodeAttributesEntity::getLocationText).orElse(null))
			.withSubject(attributes.map(NodeAttributesEntity::getSubject).map(NodeMapper::toSubject).orElse(null))
			.withSeriesSignum(attributes.map(NodeAttributesEntity::getSeriesSignum).orElse(null))
			.withOldSeriesSignum(attributes.map(NodeAttributesEntity::getOldSeriesSignum).orElse(null))
			.withVolumeNumber(attributes.map(NodeAttributesEntity::getVolumeNumber).orElse(null))
			.withVolumeCount(attributes.map(NodeAttributesEntity::getVolumeCount).orElse(null))
			.withShelfMeters(attributes.map(NodeAttributesEntity::getShelfMeters).orElse(null))
			.withVolumePlacement(attributes.map(NodeAttributesEntity::getVolumePlacement).orElse(null))
			.withAccessionNumber(attributes.map(NodeAttributesEntity::getAccessionNumber).orElse(null))
			.withHoldingsCode(attributes.map(NodeAttributesEntity::getHoldingsCode).orElse(null))
			.withHistoryFilename(attributes.map(NodeAttributesEntity::getHistoryFilename).orElse(null));
	}

	/**
	 * The name a node is shown under. An archive named after its arkivbildare has an empty {@code NAME} of its own; the
	 * archive's own view then falls back to the legal entity and then to the person as {@code Efternamn, Förnamn}, and
	 * so does this. The legal entity is read from the creator already mapped and the person through the same guards, so
	 * the sentinel rows and the soft-deleted ones lend the node no name either.
	 */
	private static String name(final NodeEntity entity, final Creator creator, final Optional<NodeAttributesEntity> attributes) {
		return ofNullable(entity.getName())
			.filter(not(String::isBlank))
			.or(() -> ofNullable(creator).map(Creator::getLegalEntity))
			.or(() -> attributes.map(NodeAttributesEntity::getPerson).flatMap(CreatorMapper::realPerson).flatMap(NodeMapper::archiveName))
			.orElse(null);
	}

	/**
	 * A person's name the way the archive names a node after them, {@code Efternamn, Förnamn}, with a blank part left
	 * out rather than leaving a dangling comma — the {@code CONCAT_WS} the archive's own view uses.
	 */
	private static Optional<String> archiveName(final PersonEntity person) {
		final var name = Stream.of(person.getLastName(), person.getFirstName())
			.map(part -> ofNullable(part).map(String::trim).orElse(""))
			.filter(not(String::isEmpty))
			.collect(joining(", "));
		return Optional.of(name).filter(not(String::isEmpty));
	}

	/** The arkivbildare's legal entity when it is a real one, since only such a one lends the node its activity period. */
	private static Optional<LegalEntityEntity> realLegalEntity(final NodeAttributesEntity attributes) {
		return CreatorMapper.realLegalEntity(attributes.getLegalEntity());
	}

	private static Subject toSubject(final OcmEntity subject) {
		return Subject.create()
			.withCode(subject.getCode())
			.withText(subject.getText())
			.withDescription(subject.getDescription());
	}

	/**
	 * Map a list of {@link NodeEntity} objects to {@link Node} API models.
	 *
	 * @param  entities source entities
	 * @return          list of mapped {@link Node} objects (empty if {@code entities} is null)
	 */
	public static List<Node> toNodeList(final List<NodeEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(NodeMapper::toNode)
			.toList();
	}

	/**
	 * Map a {@link NodeEntity} and its ancestors to a {@link NodeDetail}.
	 *
	 * @param  entity    the node itself
	 * @param  ancestors the nodes above it, root first, excluding the node itself
	 * @return           the mapped {@link NodeDetail}, or {@code null} if {@code entity} is null
	 */
	public static NodeDetail toNodeDetail(final NodeEntity entity, final List<NodeEntity> ancestors) {
		return ofNullable(entity)
			.map(e -> NodeDetail.create()
				.withNode(toNode(e))
				.withPath(toNodeList(ancestors)))
			.orElse(null);
	}

	private static Integer nodeTypeId(final NodeEntity entity) {
		return ofNullable(entity.getNodeType())
			.map(NodeTypeEntity::getId)
			.orElse(null);
	}

	private static String nodeTypeName(final NodeEntity entity) {
		return ofNullable(entity.getNodeType())
			.map(NodeTypeEntity::getName)
			.orElse(null);
	}
}
