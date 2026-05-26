package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPhotoResponse;
import se.sundsvall.memories.api.model.Photo;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.api.util.MediaTypes;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PhotoMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PhotoService {

	private final PhotoRepository photoRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;

	public PhotoService(final PhotoRepository photoRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup) {
		this.photoRepository = photoRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
	}

	public PagedPhotoResponse search(final PhotoParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());
		final var objectType = trimToNull(parameters.getObjectType());

		final var page = fetchPage(sanitized, objectType, pageable);

		return PagedPhotoResponse.create()
			.withPhotos(PhotoMapper.toPhotoList(page.getContent(), topographyLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	private Page<PhotoEntity> fetchPage(final String sanitizedQuery, final String objectType, final Pageable pageable) {
		if (objectType != null && sanitizedQuery != null) {
			return photoRepository.searchPublishedByObjectType(sanitizedQuery, objectType, pageable);
		}
		if (objectType != null) {
			return photoRepository.findAllPublishedByObjectType(objectType, pageable);
		}
		if (sanitizedQuery != null) {
			return photoRepository.searchPublished(sanitizedQuery, pageable);
		}
		return photoRepository.findAllPublished(pageable);
	}

	private static String trimToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.orElse(null);
	}

	public Photo getById(final Integer id) {
		return photoRepository.findById(id)
			.map(entity -> PhotoMapper.toPhoto(entity, topographyLookup.resolve(entity.getTopographyId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = photoRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Photo with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		response.addHeader(CONTENT_TYPE, MediaTypes.resolve(filename).toString());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename).build().toString());

		streamFileContent(id, variant, filename, response);
	}

	private void streamFileContent(final Integer id, final FileVariant variant, final String filename, final HttpServletResponse response) {
		// SMB URI separator is always "/" — see comment in PublicationService for the
		// reason String.join is preferred over a literal "/" concatenation.
		final var path = String.join("/", sambaProperties.photoFolder() + variant.getSubfolder(), filename);
		try {
			sambaIntegration.streamFile(path, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for photo with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	public enum FileVariant {
		THUMBNAIL("fil_liten", PhotoEntity::getThumbnailFilename),
		LARGE("fil_stor", PhotoEntity::getLargeImageFilename);

		private final String subfolder;
		private final Function<PhotoEntity, String> fileNameExtractor;

		FileVariant(final String subfolder, final Function<PhotoEntity, String> fileNameExtractor) {
			this.subfolder = subfolder;
			this.fileNameExtractor = fileNameExtractor;
		}

		String extract(final PhotoEntity entity) {
			return fileNameExtractor.apply(entity);
		}

		String getSubfolder() {
			return subfolder;
		}
	}
}
