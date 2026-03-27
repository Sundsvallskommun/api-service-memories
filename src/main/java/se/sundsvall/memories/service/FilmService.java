package se.sundsvall.memories.service;

import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Film;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.service.mapper.FilmMapper;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.withQuery;
import static se.sundsvall.memories.service.mapper.FilmMapper.toFilmList;

@Service
public class FilmService {

	private final FilmRepository filmRepository;

	public FilmService(final FilmRepository filmRepository) {
		this.filmRepository = filmRepository;
	}

	public List<Film> search(final String query) {
		if (query == null || query.isBlank()) {
			return toFilmList(filmRepository.findAll());
		}

		return toFilmList(filmRepository.findAll(withQuery(query)));
	}

	public Film getById(final Long id) {
		return filmRepository.findById(id)
			.map(FilmMapper::toFilm)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Film with id '%s' not found".formatted(id)));
	}
}
