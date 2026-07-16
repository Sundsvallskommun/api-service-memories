package se.sundsvall.memories.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPersonResponse;
import se.sundsvall.memories.api.model.Person;
import se.sundsvall.memories.api.model.PersonParameters;
import se.sundsvall.memories.integration.db.PersonRepository;
import se.sundsvall.memories.service.mapper.PersonMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PersonService {

	private static final String PERSON_NOT_FOUND = "Person with id '%s' not found";

	private final PersonRepository personRepository;

	public PersonService(final PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public PagedPersonResponse search(final PersonParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = personRepository.search(
			blankToNull(parameters.getLastName()),
			blankToNull(parameters.getFirstName()),
			blankToNull(parameters.getBirthParish()),
			gender(parameters.getGender()),
			parameters.getYearFrom(),
			parameters.getYearTo(),
			pageable);

		return PagedPersonResponse.create()
			.withPersons(PersonMapper.toPersonList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Person getById(final Integer id) {
		return personRepository.findById(id)
			.map(PersonMapper::toPerson)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, PERSON_NOT_FOUND.formatted(id)));
	}

	private static String blankToNull(final String value) {
		return ofNullable(value)
			.map(String::trim)
			.filter(v -> !v.isEmpty())
			.orElse(null);
	}

	/**
	 * Normalises the gender filter: blank and the "both sources" sentinel ("båda") mean "no filter" and map to
	 * {@code null}; any other value is passed through and matched case-insensitively against the stored {@code KON}.
	 */
	private static String gender(final String value) {
		return ofNullable(blankToNull(value))
			.filter(v -> !"båda".equalsIgnoreCase(v))
			.orElse(null);
	}
}
