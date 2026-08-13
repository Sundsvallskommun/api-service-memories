package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedTextResponse;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.TextMediaRepository;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.TextMapper;
import se.sundsvall.memories.service.model.FileVariant;
import se.sundsvall.memories.service.util.FileStreamer;
import se.sundsvall.memories.service.util.FileVariants;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TextService {

	private final TextRepository textRepository;
	private final TextMediaRepository textMediaRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final FileStreamer fileStreamer;

	public TextService(final TextRepository textRepository, final TextMediaRepository textMediaRepository,
		final SambaIntegrationProperties sambaProperties, final FileStreamer fileStreamer) {
		this.textRepository = textRepository;
		this.textMediaRepository = textMediaRepository;
		this.sambaProperties = sambaProperties;
		this.fileStreamer = fileStreamer;
	}

	@Transactional(readOnly = true)
	public PagedTextResponse search(final TextParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = textRepository.findAllByParameters(parameters, pageable);

		return PagedTextResponse.create()
			.withTexts(TextMapper.toTextList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	private TextEntity findVisible(final Integer id) {
		return textRepository.findVisibleById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Text with id '%s' not found".formatted(id)));
	}

	@Transactional(readOnly = true)
	public Text getById(final Integer id) {
		final var entity = findVisible(id);
		final var mediaEntities = textMediaRepository.findByTextIdOrderById(id);

		return TextMapper.toText(entity, mediaEntities);
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = findVisible(id);

		final var filename = ofNullable(FileVariants.filename(entity, variant))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Text with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		final var path = FileStreamer.smbPath(sambaProperties.textFolder(), variant, filename);

		fileStreamer.streamInline(path, filename, variant == FileVariant.TEXT, response,
			"IOException occurred when streaming file for text with id '%s'".formatted(id));
	}

	public void streamMediaFile(final Integer textId, final Integer mediaId, final FileVariant variant, final HttpServletResponse response) {
		final var entity = textMediaRepository.findById(new TextMediaEntity.TextMediaId(textId, mediaId))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Media file with id '%s' for text with id '%s' not found".formatted(mediaId, textId)));

		final var filename = ofNullable(FileVariants.filename(entity, variant))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Media file with id '%s' for text with id '%s' has no file for variant '%s'".formatted(mediaId, textId, variant.name().toLowerCase())));

		// TEXT_MULTI media files live in their own folder on the share (configured via
		// integration.samba.text-multi-folder, e.g. .../MEDIA/TEXT_MULTI/); the
		// fil_liten/fil_stor/fil_original subfolders mirror the primary text layout.
		final var path = FileStreamer.smbPath(sambaProperties.textMultiFolder(), variant, filename);

		// Media files are images, never XML — no XSLT transform.
		fileStreamer.streamInline(path, filename, false, response,
			"IOException occurred when streaming media file '%s' for text with id '%s'".formatted(mediaId, textId));
	}

}
