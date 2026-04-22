package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.api.model.PagedAudioResponse;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.model.AudioEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.AudioMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class AudioService {

	private final AudioRepository audioRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final OcmLookup ocmLookup;

	public AudioService(final AudioRepository audioRepository, final SambaIntegration sambaIntegration,
		final SambaIntegrationProperties sambaProperties, final TopographyLookup topographyLookup, final OcmLookup ocmLookup) {
		this.audioRepository = audioRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.ocmLookup = ocmLookup;
	}

	private static String deriveFilename(final AudioEntity entity) {
		return ofNullable(entity.getObjectFilePath())
			.filter(path -> !path.isBlank())
			.map(path -> path.replace('\\', '/'))
			.map(path -> path.substring(path.lastIndexOf('/') + 1))
			.filter(name -> !name.isBlank())
			.orElseGet(() -> "audio-" + entity.getAudioId());
	}

	public PagedAudioResponse search(final AudioParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = sanitized == null
			? audioRepository.findAllPublished(pageable)
			: audioRepository.searchPublished(sanitized, pageable);

		return PagedAudioResponse.create()
			.withAudios(AudioMapper.toAudioList(page.getContent(), topographyLookup::resolve, ocmLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Audio getById(final Integer id) {
		return audioRepository.findById(id)
			.map(entity -> AudioMapper.toAudio(entity, topographyLookup.resolve(entity.getTopographyId()), ocmLookup.resolve(entity.getSubjectId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Audio with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = audioRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Audio with id '%s' not found".formatted(id)));

		final var mimeType = ofNullable(entity.getAudioMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_TYPE, mimeType);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(deriveFilename(entity)));

		streamFileContent(entity, response);
	}

	private void streamFileContent(final AudioEntity entity, final HttpServletResponse response) {
		try {
			sambaIntegration.streamFile(sambaProperties.audioFolder() + entity.getObjectFilePath(), response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for audio with id '%s': %s".formatted(entity.getAudioId(), e.getMessage()));
		}
	}
}
