package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.api.model.PagedFilmResponse;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.FilmMapper;
import se.sundsvall.memories.service.model.StreamPayload;
import se.sundsvall.memories.service.util.FileStreamer;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class FilmService {

	private static final String FILM_NOT_FOUND = "Film with id '%s' not found";

	private final FilmRepository filmRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;
	private final FileStreamer fileStreamer;

	public FilmService(final FilmRepository filmRepository, final SambaIntegrationProperties sambaProperties,
		final TopographyLookup topographyLookup, final FileStreamer fileStreamer) {
		this.filmRepository = filmRepository;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
		this.fileStreamer = fileStreamer;
	}

	public PagedFilmResponse search(final FilmParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = ofNullable(sanitized)
			.map(query -> filmRepository.searchPublished(query, pageable))
			.orElseGet(() -> filmRepository.findAllPublished(pageable));

		return PagedFilmResponse.create()
			.withFilms(FilmMapper.toFilmList(page.getContent(), topographyLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Film getById(final Integer id) {
		return filmRepository.findById(id)
			.map(entity -> FilmMapper.toFilm(entity, topographyLookup.resolve(entity.getTopographyId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, FILM_NOT_FOUND.formatted(id)));
	}

	/**
	 * Opens a film file as a Range-aware {@link StreamPayload} for inline playback. See
	 * {@link AudioService#openForPlayback(Integer)} for details on the streaming contract.
	 */
	public StreamPayload openForPlayback(final Integer id) {
		final var entity = filmRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, FILM_NOT_FOUND.formatted(id)));

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		return fileStreamer.openForPlayback(sambaProperties.filmFolder() + entity.getObjectFilePath(), mimeType, deriveFilename(entity));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = filmRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, FILM_NOT_FOUND.formatted(id)));

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		fileStreamer.streamAttachment(sambaProperties.filmFolder() + entity.getObjectFilePath(), mimeType, deriveFilename(entity), response,
			"IOException occurred when streaming file for film with id '%s'".formatted(id));
	}

	private static String deriveFilename(final FilmEntity entity) {
		return FileStreamer.downloadFilename("sundsvallsminnen-" + entity.getFilmId(), entity.getObjectFilePath());
	}
}
