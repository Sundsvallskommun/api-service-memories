package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPhotoResponse;
import se.sundsvall.memories.api.model.Photo;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.model.PhotoEntity;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PhotoMapper;
import se.sundsvall.memories.service.util.FileStreamer;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PhotoService {

	private final PhotoRepository photoRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final FileStreamer fileStreamer;

	public PhotoService(final PhotoRepository photoRepository, final SambaIntegrationProperties sambaProperties,
		final FileStreamer fileStreamer) {
		this.photoRepository = photoRepository;
		this.sambaProperties = sambaProperties;
		this.fileStreamer = fileStreamer;
	}

	@Transactional(readOnly = true)
	public PagedPhotoResponse search(final PhotoParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = photoRepository.findAllByParameters(parameters, pageable);

		return PagedPhotoResponse.create()
			.withPhotos(PhotoMapper.toPhotoList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	private PhotoEntity findVisible(final Integer id) {
		return photoRepository.findVisibleById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));
	}

	@Transactional(readOnly = true)
	public Photo getById(final Integer id) {
		final var entity = findVisible(id);

		return PhotoMapper.toPhoto(entity, photoRepository.findRelatedPhotoIds(id));
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = findVisible(id);

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Photo with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		// SMB URI separator is always "/" — see SambaIntegration for the reason String.join is
		// preferred over a literal "/" concatenation.
		final var path = String.join("/", sambaProperties.photoFolder() + variant.getSubfolder(), filename);

		fileStreamer.streamInline(path, filename, false, response,
			"IOException occurred when streaming file for photo with id '%s'".formatted(id));
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
