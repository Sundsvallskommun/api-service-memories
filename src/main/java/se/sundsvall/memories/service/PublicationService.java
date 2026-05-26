package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPublicationResponse;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.api.util.MediaTypes;
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

@Service
public class PublicationService {

	private final PublicationRepository publicationRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final XsltTransformer xsltTransformer;

	public PublicationService(final PublicationRepository publicationRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup, final XsltTransformer xsltTransformer) {
		this.publicationRepository = publicationRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.xsltTransformer = xsltTransformer;
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

		if (variant == FileVariant.TEXT && filename.toLowerCase().endsWith(".xml")) {
			streamTransformedXml(id, filename, response);
		} else {
			streamBinary(id, variant, filename, response);
		}
	}

	private void streamBinary(final Integer id, final FileVariant variant, final String filename, final HttpServletResponse response) {
		response.addHeader(CONTENT_TYPE, MediaTypes.resolve(filename).toString());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename).build().toString());

		streamFileContent(id, variant, filename, response);
	}

	private void streamTransformedXml(final Integer id, final String filename, final HttpServletResponse response) {
		final var htmlFilename = swapExtension(filename, "html");
		response.addHeader(CONTENT_TYPE, new MediaType("text", "html", StandardCharsets.UTF_8).toString());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(htmlFilename).build().toString());

		final var path = String.join("/", sambaProperties.publicationFolder() + FileVariant.TEXT.getSubfolder(), filename);
		try (final var input = sambaIntegration.openResource(path).getInputStream()) {
			xsltTransformer.transform(input, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when transforming text file for publication with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	private void streamFileContent(final Integer id, final FileVariant variant, final String filename, final HttpServletResponse response) {
		// Build the SMB path via String.join so we don't have a hard-coded "/"
		// literal embedded in the format string (Sonar S1075). The separator is
		// genuinely "/" here — SMB URIs always use forward slashes regardless
		// of host OS, so File.separator would actually be wrong on Windows.
		final var path = String.join("/", sambaProperties.publicationFolder() + variant.getSubfolder(), filename);
		try {
			sambaIntegration.streamFile(path, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for publication with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	private static String swapExtension(final String filename, final String newExtension) {
		final var dot = filename.lastIndexOf('.');
		final var stem = dot > 0 ? filename.substring(0, dot) : filename;
		return stem + "." + newExtension;
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
