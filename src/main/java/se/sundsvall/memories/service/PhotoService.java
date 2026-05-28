package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
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
import se.sundsvall.memories.integration.db.FotoOcmRepository;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.FotoOcmEntity;
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
	private final FotoOcmRepository fotoOcmRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final OcmLookup ocmLookup;
	private final FileTypeDetector fileTypeDetector;

	public PhotoService(final PhotoRepository photoRepository, final FotoOcmRepository fotoOcmRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup, final OcmLookup ocmLookup, final FileTypeDetector fileTypeDetector) {
		this.photoRepository = photoRepository;
		this.fotoOcmRepository = fotoOcmRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.ocmLookup = ocmLookup;
		this.fileTypeDetector = fileTypeDetector;
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
		final var entity = photoRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));

		final var relatedPhotoIds = photoRepository.findRelatedPhotoIds(id);
		final var subjects = fotoOcmRepository.findByPhotoIdOrderById(id).stream()
			.map(FotoOcmEntity::getOcmId)
			.map(ocmLookup::resolveSubject)
			.filter(Objects::nonNull)
			.toList();

		return PhotoMapper.toPhoto(entity, topographyLookup.resolve(entity.getTopographyId()), relatedPhotoIds, subjects);
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = photoRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Photo with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		// SMB URI separator is always "/" — see SambaIntegration for the reason String.join is
		// preferred over a literal "/" concatenation.
		final var path = String.join("/", sambaProperties.photoFolder() + variant.getSubfolder(), filename);

		try (final var input = sambaIntegration.openResource(path).getInputStream()) {
			final var detected = fileTypeDetector.detect(input, filename);

			response.addHeader(CONTENT_TYPE, detected.mimeType());
			response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename).build().toString());
			detected.writeTo(response.getOutputStream());
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
