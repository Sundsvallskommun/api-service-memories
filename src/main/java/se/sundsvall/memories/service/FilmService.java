package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.api.model.FilmParameters;
import se.sundsvall.memories.api.model.PagedFilmResponse;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.db.specification.FilmSpecification;
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
	private final FileStreamer fileStreamer;

	public FilmService(final FilmRepository filmRepository, final SambaIntegrationProperties sambaProperties,
		final FileStreamer fileStreamer) {
		this.filmRepository = filmRepository;
		this.sambaProperties = sambaProperties;
		this.fileStreamer = fileStreamer;
	}

	@Transactional(readOnly = true)
	public PagedFilmResponse search(final FilmParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var specification = Specification.allOf(
			FilmSpecification.fetchTopography(),
			FilmSpecification.notDeleted(),
			FilmSpecification.published(),
			FilmSpecification.matches(parameters.getQuery()));

		final var page = filmRepository.findAll(specification, pageable);

		return PagedFilmResponse.create()
			.withFilms(FilmMapper.toFilmList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	/**
	 * Loads a single film by id, applying the same visibility rules as a search so that a soft-deleted film cannot be
	 * reached by guessing its id.
	 *
	 * <p>
	 * Unpublished films are deliberately still reachable here — an administrative interface is planned that needs to
	 * show them.
	 */
	private FilmEntity findVisible(final Integer id) {
		return filmRepository.findOne(Specification.allOf(
			FilmSpecification.fetchTopography(),
			FilmSpecification.hasId(id),
			FilmSpecification.notDeleted()))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, FILM_NOT_FOUND.formatted(id)));
	}

	@Transactional(readOnly = true)
	public Film getById(final Integer id) {
		return FilmMapper.toFilm(findVisible(id));
	}

	/**
	 * Opens a film file as a Range-aware {@link StreamPayload} for inline playback. See
	 * {@link AudioService#openForPlayback(Integer)} for details on the streaming contract.
	 */
	public StreamPayload openForPlayback(final Integer id) {
		final var entity = findVisible(id);

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		return fileStreamer.openForPlayback(sambaProperties.filmFolder() + entity.getObjectFilePath(), mimeType, deriveFilename(entity));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = findVisible(id);

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		fileStreamer.streamAttachment(sambaProperties.filmFolder() + entity.getObjectFilePath(), mimeType, deriveFilename(entity), response,
			"IOException occurred when streaming file for film with id '%s'".formatted(id));
	}

	private static String deriveFilename(final FilmEntity entity) {
		return FileStreamer.filenameFromPath(entity.getObjectFilePath(), "film-" + entity.getFilmId());
	}
}
