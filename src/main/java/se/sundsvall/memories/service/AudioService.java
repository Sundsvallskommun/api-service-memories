package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
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
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.AudioMapper;
import se.sundsvall.memories.service.model.StreamPayload;
import se.sundsvall.memories.service.util.FileStreamer;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class AudioService {

	private static final String AUDIO_NOT_FOUND = "Audio with id '%s' not found";

	private final AudioRepository audioRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final OcmLookup ocmLookup;
	private final FileStreamer fileStreamer;

	public AudioService(final AudioRepository audioRepository, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup, final OcmLookup ocmLookup, final FileStreamer fileStreamer) {
		this.audioRepository = audioRepository;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.ocmLookup = ocmLookup;
		this.fileStreamer = fileStreamer;
	}

	public PagedAudioResponse search(final AudioParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = ofNullable(sanitized)
			.map(query -> audioRepository.searchPublished(query, pageable))
			.orElseGet(() -> audioRepository.findAllPublished(pageable));

		return PagedAudioResponse.create()
			.withAudios(AudioMapper.toAudioList(page.getContent(), topographyLookup::resolve, ocmLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Audio getById(final Integer id) {
		return audioRepository.findById(id)
			.map(entity -> AudioMapper.toAudio(entity, topographyLookup.resolve(entity.getTopographyId()), ocmLookup.resolve(entity.getSubjectId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, AUDIO_NOT_FOUND.formatted(id)));
	}

	/**
	 * Opens an audio file as a Range-aware {@link StreamPayload} for inline playback. The returned resource looks up its
	 * own length on demand (cached in SambaIntegration) and opens a fresh SMB stream per read, which is what
	 * Spring's {@code ResourceRegionHttpMessageConverter} requires to serve {@code 206 Partial Content} responses.
	 *
	 * @param  id the audio id
	 * @return    the payload (resource, mime type, filename)
	 */
	public StreamPayload openForPlayback(final Integer id) {
		final var entity = audioRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, AUDIO_NOT_FOUND.formatted(id)));

		final var mimeType = ofNullable(entity.getAudioMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		return fileStreamer.openForPlayback(sambaProperties.audioFolder() + entity.getObjectFilePath(), mimeType, deriveFilename(entity));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = audioRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, AUDIO_NOT_FOUND.formatted(id)));

		final var mimeType = ofNullable(entity.getAudioMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		fileStreamer.streamAttachment(sambaProperties.audioFolder() + entity.getObjectFilePath(), mimeType, deriveFilename(entity), response,
			"IOException occurred when streaming file for audio with id '%s'".formatted(id));
	}

	private static String deriveFilename(final AudioEntity entity) {
		return FileStreamer.filenameFromPath(entity.getObjectFilePath(), "audio-" + entity.getAudioId());
	}
}
