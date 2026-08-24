package se.sundsvall.memories.api.model;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.Application;
import se.sundsvall.memories.integration.db.AudioRepository;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.integration.db.PersonRepository;
import se.sundsvall.memories.integration.db.PhotoRepository;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.SeamanRepository;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.service.util.Pageables;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Every value a {@code sortBy} whitelist accepts has to resolve as an attribute of the entity behind it. A property
 * that does not is invisible until it reaches Spring Data, where it becomes a 500 — which is the bug the whitelists
 * exist to prevent, and which a whitelist listing the wrong name would reintroduce in a way no unit test would catch.
 *
 * <p>
 * This runs the real search once per accepted value, against a real database, and only asserts that it does not throw.
 * What the order actually is belongs to the per-type tests.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@Transactional
class SortablePropertiesTest {

	@Autowired
	private AudioRepository audioRepository;
	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private TextRepository textRepository;
	@Autowired
	private PublicationRepository publicationRepository;
	@Autowired
	private PhotoRepository photoRepository;
	@Autowired
	private SeamanRepository seamanRepository;
	@Autowired
	private LegalEntityRepository legalEntityRepository;
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private CensusRecordRepository censusRecordRepository;
	@Autowired
	private CombinedObjectRepository combinedObjectRepository;

	private static Stream<Arguments> sortableProperties() {
		return Stream.of(
			arguments("audio", List.of("documentTitle", "date", "id")),
			arguments("film", List.of("documentTitle", "date", "id")),
			arguments("text", List.of("documentTitle", "documentDate", "id")),
			arguments("publication", List.of("documentTitle", "documentDate", "date", "id")),
			arguments("photo", List.of("documentTitle", "earliest", "latest", "objectType", "id")),
			arguments("seaman", List.of("lastName1", "firstName", "birthDate", "birthParish", "id")),
			arguments("legalEntity", List.of("name", "startDate", "endDate", "legalEntityId")),
			arguments("person", List.of("lastName", "firstName", "birthDate", "birthParish")),
			arguments("censusRecord", List.of("lastName", "firstName", "birthYear")),
			arguments("combinedObject", List.of("objectKey", "title", "year", "objectType")));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("sortableProperties")
	void everyAcceptedSortPropertyResolves(final String type, final List<String> properties) {
		for (final var property : properties) {
			assertThatNoException()
				.describedAs("%s sorted by %s", type, property)
				.isThrownBy(() -> search(type, property));
		}
	}

	private void search(final String type, final String property) {
		switch (type) {
			case "audio" -> audioRepository.findAllByParameters(sorted(AudioParameters.create(), property), pageable(AudioParameters.create(), property, "id"));
			case "film" -> filmRepository.findAllByParameters(sorted(FilmParameters.create(), property), pageable(FilmParameters.create(), property, "id"));
			case "text" -> textRepository.findAllByParameters(sorted(TextParameters.create(), property), pageable(TextParameters.create(), property, "id"));
			case "publication" -> publicationRepository.findAllByParameters(sorted(PublicationParameters.create(), property), pageable(PublicationParameters.create(), property, "id"));
			case "photo" -> photoRepository.findAllByParameters(sorted(PhotoParameters.create(), property), pageable(PhotoParameters.create(), property, "id"));
			case "seaman" -> seamanRepository.findAllByParameters(sorted(SeamanParameters.create(), property), pageable(SeamanParameters.create(), property, "id"));
			case "legalEntity" -> legalEntityRepository.findAllByParameters(sorted(LegalEntityParameters.create(), property), pageable(LegalEntityParameters.create(), property, "legalEntityId"));
			case "person" -> personRepository.findAllByParameters(sorted(PersonParameters.create(), property), pageable(PersonParameters.create(), property, "personId"));
			case "censusRecord" -> censusRecordRepository.findAllByParameters(sorted(CensusRecordParameters.create(), property), pageable(CensusRecordParameters.create(), property, "id"));
			// The combined search orders itself from its specification, so it is the one that gets no sort here.
			case "combinedObject" -> combinedObjectRepository.findAllByParameters(sorted(CombinedObjectParameters.create(), property), Pageable.ofSize(10));
			default -> throw new IllegalArgumentException(type);
		}
	}

	private static <T extends se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase> T sorted(final T parameters, final String property) {
		parameters.setSortBy(List.of(property));
		return parameters;
	}

	private static Pageable pageable(final se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase parameters, final String property,
		final String idAttribute) {
		return Pageables.of(sorted(parameters, property), idAttribute);
	}
}
