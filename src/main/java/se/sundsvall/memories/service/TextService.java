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
import se.sundsvall.memories.api.model.PagedTextResponse;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.TextMediaRepository;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.TextMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TextService {

	private final TextRepository textRepository;
	private final TextMediaRepository textMediaRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final XsltTransformer xsltTransformer;
	private final FileTypeDetector fileTypeDetector;

	public TextService(final TextRepository textRepository, final TextMediaRepository textMediaRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup, final XsltTransformer xsltTransformer,
		final FileTypeDetector fileTypeDetector) {
		this.textRepository = textRepository;
		this.textMediaRepository = textMediaRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.xsltTransformer = xsltTransformer;
		this.fileTypeDetector = fileTypeDetector;
	}

	public PagedTextResponse search(final TextParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = sanitized == null
			? textRepository.findAllPublished(pageable)
			: textRepository.searchPublished(sanitized, pageable);

		return PagedTextResponse.create()
			.withTexts(TextMapper.toTextList(page.getContent(), topographyLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Text getById(final Integer id) {
		final var entity = textRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Text with id '%s' not found".formatted(id)));
		final var mediaEntities = textMediaRepository.findByTextIdOrderById(id);
		return TextMapper.toText(entity, topographyLookup.resolve(entity.getTopographyId()), mediaEntities);
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

		try (final var input = sambaIntegration.openResource(path).getInputStream()) {
			final var detected = fileTypeDetector.detect(input, filename);

			if (variant == FileVariant.TEXT && isXmlMimeType(detected.mimeType())) {
				streamTransformedXml(filename, detected, response);
			} else {
				streamBinary(filename, detected, response);
			}
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for text with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	private void streamBinary(final String filename, final FileTypeDetector.Detected detected, final HttpServletResponse response) throws IOException {
		response.addHeader(CONTENT_TYPE, detected.mimeType());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename).build().toString());
		detected.writeTo(response.getOutputStream());
	}

	private void streamTransformedXml(final String filename, final FileTypeDetector.Detected detected, final HttpServletResponse response) throws IOException {
		final var htmlFilename = swapExtension(filename, "html");
		response.addHeader(CONTENT_TYPE, new MediaType("text", "html", StandardCharsets.UTF_8).toString());
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.inline().filename(htmlFilename).build().toString());
		xsltTransformer.transform(detected.fullStream(), response.getOutputStream());
	}

	private static boolean isXmlMimeType(final String mimeType) {
		return mimeType != null && mimeType.contains("xml");
	}

	private static String swapExtension(final String filename, final String newExtension) {
		final var dot = filename.lastIndexOf('.');
		final var stem = dot > 0 ? filename.substring(0, dot) : filename;
		return stem + "." + newExtension;
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
}
