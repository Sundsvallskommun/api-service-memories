package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.PagedPersonResponse;
import se.sundsvall.memories.api.model.Person;
import se.sundsvall.memories.api.model.PersonParameters;
import se.sundsvall.memories.integration.db.PersonRepository;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PersonMapper;
import se.sundsvall.memories.service.util.FileStreamer;
import se.sundsvall.memories.service.util.Pageables;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.service.util.FileStreamer.MaterialType.PERSON;

@Service
public class PersonService {

	private static final String PERSON_NOT_FOUND = "Person with id '%s' not found";

	/** Every person biography lives in this one subfolder of the person folder on the share. */
	private static final String BIOGRAPHY_SUBFOLDER = "biografi";

	private final PersonRepository personRepository;
	private final SambaIntegrationProperties sambaProperties;
	private final FileStreamer fileStreamer;

	public PersonService(final PersonRepository personRepository, final SambaIntegrationProperties sambaProperties, final FileStreamer fileStreamer) {
		this.personRepository = personRepository;
		this.sambaProperties = sambaProperties;
		this.fileStreamer = fileStreamer;
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

	/**
	 * Writes the person's biography, which the legacy migration left on the share as an XML document and which is
	 * transformed to HTML on the way out, the same way the digitised text of a document is. Only 23 of the 50 412
	 * persons have one, so a 404 here is the ordinary case rather than a fault.
	 *
	 * @param id       the person id whose biography to stream
	 * @param response the response to write to
	 */
	public void streamBiography(final Integer id, final HttpServletResponse response) {
		final var entity = personRepository.findVisibleById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, PERSON_NOT_FOUND.formatted(id)));

		final var filename = ofNullable(entity.getBiographyFilename())
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Person with id '%s' has no biography".formatted(id)));

		final var path = FileStreamer.smbPath(sambaProperties.personFolder(), BIOGRAPHY_SUBFOLDER, filename);

		fileStreamer.streamInline(path, filename, FileStreamer.downloadFilename(PERSON, id, filename), true, response,
			"IOException occurred when streaming biography for person with id '%s'".formatted(id));
	}
}
