package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPublicationResponse;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PublicationMapper;
import se.sundsvall.memories.service.model.FileVariant;
import se.sundsvall.memories.service.util.FileStreamer;
import se.sundsvall.memories.service.util.FileVariants;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.PUBLICATION;

@Service
public class PublicationService {

	private final PublicationRepository publicationRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final FileStreamer fileStreamer;

	public PublicationService(final PublicationRepository publicationRepository, final SambaIntegrationProperties sambaProperties,
		final FileStreamer fileStreamer) {
		this.publicationRepository = publicationRepository;
		this.sambaProperties = sambaProperties;
		this.fileStreamer = fileStreamer;
	}

	@Transactional(readOnly = true)
	public PagedPublicationResponse search(final PublicationParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = publicationRepository.findAllByParameters(parameters, pageable);

		return PagedPublicationResponse.create()
			.withPublications(PublicationMapper.toPublicationList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	private PublicationEntity findVisible(final Integer id) {
		return publicationRepository.findVisibleById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Publication with id '%s' not found".formatted(id)));
	}

	@Transactional(readOnly = true)
	public Publication getById(final Integer id) {
		return PublicationMapper.toPublication(findVisible(id));
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = findVisible(id);

		final var filename = ofNullable(FileVariants.filename(entity, variant))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Publication with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		final var path = FileStreamer.smbPath(sambaProperties.publicationFolder(), variant, filename);

		final var downloadFilename = FileStreamer.downloadFilename(PUBLICATION, id, filename);

		fileStreamer.streamInline(path, filename, downloadFilename, variant == FileVariant.TEXT, response,
			"IOException occurred when streaming file for publication with id '%s'".formatted(id));
	}

}
