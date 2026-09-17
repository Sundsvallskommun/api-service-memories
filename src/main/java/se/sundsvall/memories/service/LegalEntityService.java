package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.api.model.PagedLegalEntityResponse;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.LegalEntityMapper;
import se.sundsvall.memories.service.util.FileStreamer;
import se.sundsvall.memories.service.util.Pageables;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.LEGAL_ENTITY;

@Service
public class LegalEntityService {

	private static final String LEGAL_ENTITY_NOT_FOUND = "Legal entity with id '%s' not found";

	/** Every legal entity history lives in this one subfolder of the legal entity folder on the share. */
	private static final String HISTORY_SUBFOLDER = "historia";

	private final LegalEntityRepository legalEntityRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final FileStreamer fileStreamer;

	public LegalEntityService(final LegalEntityRepository legalEntityRepository, final SambaIntegrationProperties sambaProperties,
		final FileStreamer fileStreamer) {
		this.legalEntityRepository = legalEntityRepository;
		this.sambaProperties = sambaProperties;
		this.fileStreamer = fileStreamer;
	}

	@Transactional(readOnly = true)
	public PagedLegalEntityResponse search(final LegalEntityParameters parameters) {
		final var pageable = Pageables.of(parameters, "legalEntityId");

		final var page = legalEntityRepository.findAllByParameters(parameters, pageable);

		return PagedLegalEntityResponse.create()
			.withLegalEntities(LegalEntityMapper.toLegalEntityList(page.getContent()))
			.withMetaData(Pageables.metaDataOf(page, "legalEntityId"));
	}

	@Transactional(readOnly = true)
	public LegalEntity getById(final Integer id) {
		return legalEntityRepository.findVisibleById(id)
			.map(LegalEntityMapper::toLegalEntity)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, LEGAL_ENTITY_NOT_FOUND.formatted(id)));
	}

	/**
	 * Writes the legal entity's history, an XML document on the share that is transformed to HTML on the way out. Only
	 * 18 of the 6 727 legal entities have one, so a 404 here is the ordinary case rather than a fault.
	 *
	 * @param id       the legal entity id whose history to stream
	 * @param response the response to write to
	 */
	public void streamHistory(final Integer id, final HttpServletResponse response) {
		final var entity = legalEntityRepository.findVisibleById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, LEGAL_ENTITY_NOT_FOUND.formatted(id)));

		final var filename = ofNullable(entity.getHistoryFilename())
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Legal entity with id '%s' has no history".formatted(id)));

		final var path = FileStreamer.smbPath(sambaProperties.legalEntityFolder(), HISTORY_SUBFOLDER, filename);

		fileStreamer.streamInline(path, filename, FileStreamer.downloadFilename(LEGAL_ENTITY, id, filename), true, response,
			"IOException occurred when streaming history for legal entity with id '%s'".formatted(id));
	}
}
