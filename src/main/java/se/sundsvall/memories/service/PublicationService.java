package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
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
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PublicationMapper;
import se.sundsvall.memories.service.util.FileStreamer;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PublicationService {

	private final PublicationRepository publicationRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final FileStreamer fileStreamer;

	public PublicationService(final PublicationRepository publicationRepository, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup, final FileStreamer fileStreamer) {
		this.publicationRepository = publicationRepository;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.fileStreamer = fileStreamer;
	}

	public PagedPublicationResponse search(final PublicationParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = ofNullable(sanitized)
			.map(query -> publicationRepository.searchPublished(query, pageable))
			.orElseGet(() -> publicationRepository.findAllPublished(pageable));

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

		// SMB URI separator is always "/" — see SambaIntegration for the reason String.join is
		// preferred over a literal "/" concatenation.
		final var path = String.join("/", sambaProperties.publicationFolder() + variant.getSubfolder(), filename);

		final var downloadFilename = FileStreamer.downloadFilename("sundsvallsminnen-" + id, filename);

		fileStreamer.streamInline(path, filename, downloadFilename, variant == FileVariant.TEXT, response,
			"IOException occurred when streaming file for publication with id '%s'".formatted(id));
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
