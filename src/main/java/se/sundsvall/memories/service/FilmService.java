package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.FilmMapper;
import se.sundsvall.memories.service.model.StreamPayload;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class FilmService {

	private static final String FILM_NOT_FOUND = "Film with id '%s' not found";

	private final FilmRepository filmRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopographyLookup topographyLookup;

	public FilmService(final FilmRepository filmRepository, final SambaIntegration sambaIntegration,
		final SambaIntegrationProperties sambaProperties, final TopographyLookup topographyLookup) {
		this.filmRepository = filmRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topographyLookup = topographyLookup;
	}

	private static String deriveFilename(final FilmEntity entity) {
		return ofNullable(entity.getObjectFilePath())
			.filter(path -> !path.isBlank())
			.map(path -> path.replace('\\', '/'))
			.map(path -> path.substring(path.lastIndexOf('/') + 1))
			.filter(name -> !name.isBlank())
			.orElseGet(() -> "film-" + entity.getFilmId());
	}

	public PagedFilmResponse search(final FilmParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = sanitized == null
			? filmRepository.findAllPublished(pageable)
			: filmRepository.searchPublished(sanitized, pageable);

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
		final var resource = sambaIntegration.openResource(sambaProperties.filmFolder() + entity.getObjectFilePath());
		return new StreamPayload(resource, mimeType, deriveFilename(entity));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = filmRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, FILM_NOT_FOUND.formatted(id)));

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_TYPE, mimeType);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(deriveFilename(entity)));

		streamFileContent(entity, response);
	}

	private void streamFileContent(final FilmEntity entity, final HttpServletResponse response) {
		try {
			sambaIntegration.streamFile(sambaProperties.filmFolder() + entity.getObjectFilePath(), response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for film with id '%s': %s".formatted(entity.getFilmId(), e.getMessage()));
		}
	}
}
