package se.sundsvall.memories.service;

import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPersonResponse;
import se.sundsvall.memories.api.model.Person;
import se.sundsvall.memories.api.model.PersonParameters;
import se.sundsvall.memories.integration.db.PersonRepository;
import se.sundsvall.memories.service.mapper.PersonMapper;
import se.sundsvall.memories.service.util.Pageables;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PersonService {

	private static final String PERSON_NOT_FOUND = "Person with id '%s' not found";

	private final PersonRepository personRepository;

	public PersonService(final PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public PagedPersonResponse search(final PersonParameters parameters) {
		final var pageable = Pageables.of(parameters, "personId");

		final var page = personRepository.findAllByParameters(parameters, pageable);

		return PagedPersonResponse.create()
			.withPersons(PersonMapper.toPersonList(page.getContent()))
			.withMetaData(Pageables.metaDataOf(page, "personId"));
	}

	/**
	 * Fetches a single person by id. Unpublished persons are intentionally still returned here even though
	 * {@link #search} hides them; the {@code P_ID = 0} placeholder row and soft-deleted persons are not.
	 *
	 * @param  id the person id to look up
	 * @return    the matching {@link Person}
	 */
	public Person getById(final Integer id) {
		return personRepository.findVisibleById(id)
			.map(PersonMapper::toPerson)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, PERSON_NOT_FOUND.formatted(id)));
	}
}
