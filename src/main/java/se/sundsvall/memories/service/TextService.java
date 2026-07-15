package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedTextResponse;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.TextMediaRepository;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.TextMapper;
import se.sundsvall.memories.service.util.FileStreamer;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TextService {

	private final TextRepository textRepository;
	private final TextMediaRepository textMediaRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final OcmLookup ocmLookup;
	private final FileStreamer fileStreamer;

	public TextService(final TextRepository textRepository, final TextMediaRepository textMediaRepository,
		final SambaIntegrationProperties sambaProperties, final TopographyLookup topographyLookup,
		final OcmLookup ocmLookup, final FileStreamer fileStreamer) {
		this.textRepository = textRepository;
		this.textMediaRepository = textMediaRepository;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.ocmLookup = ocmLookup;
		this.fileStreamer = fileStreamer;
	}

	public PagedTextResponse search(final TextParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());
		final var location = blankToNull(parameters.getLocation());

		final Page<TextEntity> page;
		if (parameters.getYearFrom() != null || parameters.getYearTo() != null || location != null) {
			page = textRepository.searchFiltered(sanitized, parameters.getYearFrom(), parameters.getYearTo(), location, pageable);
		} else {
			page = ofNullable(sanitized)
				.map(query -> textRepository.searchPublished(query, pageable))
				.orElseGet(() -> textRepository.findAllPublished(pageable));
		}

		return PagedTextResponse.create()
			.withTexts(TextMapper.toTextList(page.getContent(), topographyLookup::resolve, ocmLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	private static String blankToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.orElse(null);
	}

	public Text getById(final Integer id) {
		final var entity = textRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Text with id '%s' not found".formatted(id)));
		final var mediaEntities = textMediaRepository.findByTextIdOrderById(id);
		return TextMapper.toText(entity, topographyLookup.resolve(entity.getTopographyId()), ocmLookup.resolve(entity.getSubjectId()), mediaEntities);
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = textRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Text with id '%s' not found".formatted(id)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Text with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		// SMB URI separator is always "/" — see SambaIntegration for the reason String.join is
		// preferred over a literal "/" concatenation.
		final var path = String.join("/", sambaProperties.textFolder() + variant.getSubfolder(), filename);

		fileStreamer.streamInline(path, filename, variant == FileVariant.TEXT, response,
			"IOException occurred when streaming file for text with id '%s'".formatted(id));
	}

	public void streamMediaFile(final Integer textId, final Integer mediaId, final MediaFileVariant variant, final HttpServletResponse response) {
		final var entity = textMediaRepository.findById(new TextMediaEntity.TextMediaId(textId, mediaId))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Media file with id '%s' for text with id '%s' not found".formatted(mediaId, textId)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Media file with id '%s' for text with id '%s' has no file for variant '%s'".formatted(mediaId, textId, variant.name().toLowerCase())));

		// TEXT_MULTI media files live in their own folder on the share (configured via
		// integration.samba.text-multi-folder, e.g. .../MEDIA/TEXT_MULTI/); the
		// fil_liten/fil_stor/fil_original subfolders mirror the primary text layout.
		final var path = String.join("/", sambaProperties.textMultiFolder() + variant.getSubfolder(), filename);

		// Media files are images, never XML — no XSLT transform.
		fileStreamer.streamInline(path, filename, false, response,
			"IOException occurred when streaming media file '%s' for text with id '%s'".formatted(mediaId, textId));
	}

	public enum FileVariant {
		THUMBNAIL("fil_liten", TextEntity::getThumbnailFilename),
		LARGE("fil_stor", TextEntity::getLargeImageFilename),
		TEXT("fil_txt", TextEntity::getOcrFilename);

		private final String subfolder;
		private final Function<TextEntity, String> fileNameExtractor;

		FileVariant(final String subfolder, final Function<TextEntity, String> fileNameExtractor) {
			this.subfolder = subfolder;
			this.fileNameExtractor = fileNameExtractor;
		}

		String extract(final TextEntity entity) {
			return fileNameExtractor.apply(entity);
		}

		String getSubfolder() {
			return subfolder;
		}
	}

	public enum MediaFileVariant {
		THUMBNAIL("fil_liten", TextMediaEntity::getThumbnailFilename),
		LARGE("fil_stor", TextMediaEntity::getLargeImageFilename),
		ORIGINAL("fil_original", TextMediaEntity::getOriginalFilename);

		private final String subfolder;
		private final Function<TextMediaEntity, String> fileNameExtractor;

		MediaFileVariant(final String subfolder, final Function<TextMediaEntity, String> fileNameExtractor) {
			this.subfolder = subfolder;
			this.fileNameExtractor = fileNameExtractor;
		}

		String extract(final TextMediaEntity entity) {
			return fileNameExtractor.apply(entity);
		}

		String getSubfolder() {
			return subfolder;
		}
	}
}
