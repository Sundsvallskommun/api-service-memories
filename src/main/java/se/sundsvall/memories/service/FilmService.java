package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.service.mapper.FilmMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.withQuery;
import static se.sundsvall.memories.service.mapper.FilmMapper.toFilmList;

@Service
public class FilmService {

	private final FilmRepository filmRepository;
	private final SambaIntegration sambaIntegration;

	public FilmService(final FilmRepository filmRepository, final SambaIntegration sambaIntegration) {
		this.filmRepository = filmRepository;
		this.sambaIntegration = sambaIntegration;
	}

	public List<Film> search(final String query) {
		if (query == null || query.isBlank()) {
			return toFilmList(filmRepository.findAll());
		}

		return toFilmList(filmRepository.findAll(withQuery(query)));
	}

	public Film getById(final Integer id) {
		return filmRepository.findById(id)
			.map(FilmMapper::toFilm)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Film with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final HttpServletResponse response) {
		final var entity = filmRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Film with id '%s' not found".formatted(id)));

		final var mimeType = ofNullable(entity.getFilmMimeType()).orElse(APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_TYPE, mimeType);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(entity.getFilnamn()));

		streamFileContent(entity, response);
	}

	private void streamFileContent(final FilmEntity entity, final HttpServletResponse response) {
		try {
			sambaIntegration.streamFile(entity.getFilmObjFil(), response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for film with id '%s': %s".formatted(entity.getFilmId(), e.getMessage()));
		}
	}
}
