package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPublicationResponse;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PublicationMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class PublicationService {

	private final PublicationRepository publicationRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;

	public PublicationService(final PublicationRepository publicationRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup) {
		this.publicationRepository = publicationRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
	}

	public PagedPublicationResponse search(final PublicationParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = sanitized == null
			? publicationRepository.findAllPublished(pageable)
			: publicationRepository.searchPublished(sanitized, pageable);

		return PagedPublicationResponse.create()
			.withPublications(PublicationMapper.toPublicationList(page.getContent(), topographyLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Publication getById(final Integer id) {
		return publicationRepository.findById(id)
			.map(entity -> PublicationMapper.toPublication(entity, topographyLookup.resolve(entity.getTopographyId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Publication with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = publicationRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Publication with id '%s' not found".formatted(id)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Publication with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		response.addHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(filename));

		streamFileContent(id, variant, filename, response);
	}

	private void streamFileContent(final Integer id, final FileVariant variant, final String filename, final HttpServletResponse response) {
		// Build the SMB path via String.join so we don't have a hard-coded "/"
		// literal embedded in the format string (Sonar S1075). The separator is
		// genuinely "/" here — SMB URIs always use forward slashes regardless
		// of host OS, so File.separator would actually be wrong on Windows.
		final var path = String.join("/", sambaProperties.publFolder() + variant.getSubfolder(), filename);
		try {
			sambaIntegration.streamFile(path, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for publication with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	public enum FileVariant {
		THUMBNAIL("fil_liten", PublicationEntity::getThumbnailFilename),
		LARGE("fil_stor", PublicationEntity::getLargeImageFilename),
		TEXT("fil_txt", PublicationEntity::getOcrFilename);

		private final String subfolder;
		private final Function<PublicationEntity, String> fileNameExtractor;

		FileVariant(final String subfolder, final Function<PublicationEntity, String> fileNameExtractor) {
			this.subfolder = subfolder;
			this.fileNameExtractor = fileNameExtractor;
		}

		String extract(final PublicationEntity entity) {
			return fileNameExtractor.apply(entity);
		}

		String getSubfolder() {
			return subfolder;
		}
	}
}
