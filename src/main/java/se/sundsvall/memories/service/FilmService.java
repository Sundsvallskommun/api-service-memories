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

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class FilmService {

	private final FilmRepository filmRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;
	private final TopografiLookup topografiLookup;

	public FilmService(final FilmRepository filmRepository, final SambaIntegration sambaIntegration,
		final SambaIntegrationProperties sambaProperties, final TopografiLookup topografiLookup) {
		this.filmRepository = filmRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
		this.topografiLookup = topografiLookup;
	}

	private static String deriveFilename(final FilmEntity entity) {
		return ofNullable(entity.getFilmObjFil())
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
			.withFilms(FilmMapper.toFilmList(page.getContent(), topografiLookup::resolve))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Film getById(final Integer id) {
		return filmRepository.findById(id)
			.map(entity -> FilmMapper.toFilm(entity, topografiLookup.resolve(entity.getFilmTId())))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Film with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = filmRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Film with id '%s' not found".formatted(id)));

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_TYPE, mimeType);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(deriveFilename(entity)));

		streamFileContent(entity, response);
	}

	private void streamFileContent(final FilmEntity entity, final HttpServletResponse response) {
		try {
			sambaIntegration.streamFile(sambaProperties.filmFolder() + entity.getFilmObjFil(), response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for film with id '%s': %s".formatted(entity.getFilmId(), e.getMessage()));
		}
	}
}
