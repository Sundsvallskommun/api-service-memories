package se.sundsvall.memories.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

// Which rows the filters select is verified against a real database in CensusRecordSpecificationTest. These tests cover
// what the service itself does: build the pageable, forward the parameters, and map the resulting page.
@ExtendWith(MockitoExtension.class)
class CensusRecordServiceTest {

	@Mock
	private CensusRecordRepository repositoryMock;

	@InjectMocks
	private CensusRecordService service;

	@Test
	void searchDelegatesAndMaps() {
		final var pageable = PageRequest.of(0, 100, Sort.by("id"));
		final var parameters = CensusRecordParameters.create()
			.withLastName("Nordin")
			.withFirstName("Anton")
			.withGender("man")
			.withYearFrom(1850)
			.withYearTo(1900)
			.withPage(1)
			.withLimit(100);
		final var entity = CensusRecordEntity.create().withId(1).withLastName("Nordin");

		when(repositoryMock.findAllByParameters(any(CensusRecordParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(parameters);

		assertThat(result.getCensusRecords()).hasSize(1);
		assertThat(result.getCensusRecords().getFirst().getLastName()).isEqualTo("Nordin");
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).findAllByParameters(any(CensusRecordParameters.class), eq(pageable));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100, Sort.by("id"));
		final var parameters = CensusRecordParameters.create().withLastName(" Nordin ").withGender("   ");

		when(repositoryMock.findAllByParameters(any(CensusRecordParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getCensusRecords()).isEmpty();
		// Trimming and the "blank means no filter" rule live in the specifications, verified against a real database in
		// CensusRecordSpecificationTest. The service only has to hand the parameters over untouched.
		final var parametersCaptor = ArgumentCaptor.forClass(CensusRecordParameters.class);
		verify(repositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25, Sort.by("id"));

		when(repositoryMock.findAllByParameters(any(CensusRecordParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(CensusRecordEntity.create().withId(1)), pageable, 51));

		final var result = service.search(CensusRecordParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
	}

	@Test
	void getByIdFound() {
		when(repositoryMock.findById(1)).thenReturn(Optional.of(CensusRecordEntity.create().withId(1).withFirstName("Anton")));

		final var result = service.getById(1);

		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getFirstName()).isEqualTo("Anton");
		verify(repositoryMock).findById(1);
	}

	@Test
	void getByIdNotFound() {
		when(repositoryMock.findById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(999))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Census record with id '999' not found");
		verify(repositoryMock).findById(999);
	}
}
