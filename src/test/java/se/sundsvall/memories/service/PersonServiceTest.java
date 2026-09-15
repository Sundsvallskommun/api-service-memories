package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.PersonParameters;
import se.sundsvall.memories.integration.db.PersonRepository;
import se.sundsvall.memories.integration.db.model.PersonEntity;
import se.sundsvall.memories.service.util.FileStreamer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.integration.samba.SambaTestProperties.SAMBA_PROPERTIES;

// Which rows the filters select — including the published bit and the "ingen person" placeholder — is verified against
// a real database in PersonSpecificationTest. These tests cover what the service itself does: build the pageable,
// forward the parameters, and map the resulting page.
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

	@Mock
	private PersonRepository repositoryMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private PersonService service;

	@BeforeEach
	void setUp() {
		service = new PersonService(repositoryMock, SAMBA_PROPERTIES, fileStreamerMock);
	}

	@Test
	void searchDelegatesAndMaps() {
		final var pageable = PageRequest.of(0, 100, Sort.by("personId"));
		final var parameters = PersonParameters.create()
			.withLastName("Nordin")
			.withFirstName("Anton")
			.withBirthParish("Sundsvall")
			.withGender("man")
			.withYearFrom(1850)
			.withYearTo(1900)
			.withPage(1)
			.withLimit(100);
		final var entity = PersonEntity.create().withPersonId(1).withLastName("Nordin");

		when(repositoryMock.findAllByParameters(any(PersonParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(parameters);

		assertThat(result.getPersons()).hasSize(1);
		assertThat(result.getPersons().getFirst().getLastName()).isEqualTo("Nordin");
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).findAllByParameters(any(PersonParameters.class), eq(pageable));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100, Sort.by("personId"));
		final var parameters = PersonParameters.create().withLastName("  Nordin  ").withGender("   ");

		when(repositoryMock.findAllByParameters(any(PersonParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getPersons()).isEmpty();
		// Trimming and the "blank means no filter" rule live in the specifications, verified against a real database in
		// PersonSpecificationTest. The service only has to hand the parameters over untouched.
		final var parametersCaptor = ArgumentCaptor.forClass(PersonParameters.class);
		verify(repositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25, Sort.by("personId"));

		when(repositoryMock.findAllByParameters(any(PersonParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(PersonEntity.create().withPersonId(1)), pageable, 51));

		final var result = service.search(PersonParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
	}

	@Test
	void getByIdFound() {
		when(repositoryMock.findVisibleById(1)).thenReturn(Optional.of(PersonEntity.create().withPersonId(1).withFirstName("Anton")));

		final var result = service.getById(1);

		assertThat(result.getPersonId()).isEqualTo(1);
		assertThat(result.getFirstName()).isEqualTo("Anton");
		verify(repositoryMock).findVisibleById(1);
	}

	@Test
	void getByIdNotFound() {
		when(repositoryMock.findVisibleById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(999))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Person with id '999' not found");
		verify(repositoryMock).findVisibleById(999);
	}

	/**
	 * The placeholder row ("ingen person") is filtered out by the repository, so the service must surface it as a plain
	 * 404 rather than returning a person-shaped payload for a non-person.
	 */
	@Test
	void getByIdPlaceholderNotFound() {
		when(repositoryMock.findVisibleById(0)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(0))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Person with id '0' not found");
		verify(repositoryMock).findVisibleById(0);
	}

	@Test
	void streamBiographyDelegatesToFileStreamer() {
		final var responseMock = mock(HttpServletResponse.class);
		when(repositoryMock.findVisibleById(any())).thenReturn(Optional.of(PersonEntity.create().withBiographyFilename("PERSON.id_8238_biografi.xml")));

		service.streamBiography(8238, responseMock);

		// The document is XML on the share, so it is transformed to HTML on the way out.
		verify(fileStreamerMock).streamInline("/person/biografi/PERSON.id_8238_biografi.xml", "PERSON.id_8238_biografi.xml", "sundsvallsminnen-person-8238.xml", true, responseMock,
			"IOException occurred when streaming biography for person with id '8238'");
	}

	@Test
	void streamBiographyNotFoundWhenTheRecordIsMissing() {
		final var responseMock = mock(HttpServletResponse.class);
		when(repositoryMock.findVisibleById(any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.streamBiography(999, responseMock))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Person with id '999' not found");

		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamBiographyNotFoundWhenTheRecordHasNoFile() {
		final var responseMock = mock(HttpServletResponse.class);
		// The ordinary case: almost no person carries one.
		when(repositoryMock.findVisibleById(any())).thenReturn(Optional.of(PersonEntity.create().withBiographyFilename("   ")));

		assertThatThrownBy(() -> service.streamBiography(8238, responseMock))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("Person with id '8238' has no biography");

		verifyNoInteractions(fileStreamerMock);
	}
}
