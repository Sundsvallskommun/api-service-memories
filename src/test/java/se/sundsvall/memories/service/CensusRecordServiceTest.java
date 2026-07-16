package se.sundsvall.memories.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.CensusRecordParameters;
import se.sundsvall.memories.integration.db.CensusRecordRepository;
import se.sundsvall.memories.integration.db.model.CensusRecordEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class CensusRecordServiceTest {

	@Mock
	private CensusRecordRepository repositoryMock;

	@InjectMocks
	private CensusRecordService service;

	@Test
	void searchDelegatesAndMaps() {
		final var parameters = CensusRecordParameters.create()
			.withLastName("Nordin")
			.withFirstName("Anton")
			.withGender("man")
			.withYearFrom(1850)
			.withYearTo(1900)
			.withPage(1)
			.withLimit(100);
		final var entity = CensusRecordEntity.create().withId(1).withLastName("Nordin");
		when(repositoryMock.search(eq("Nordin"), eq("Anton"), eq("man"), eq(1850), eq(1900), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 100), 1));

		final var result = service.search(parameters);

		assertThat(result.getCensusRecords()).hasSize(1);
		assertThat(result.getCensusRecords().getFirst().getLastName()).isEqualTo("Nordin");
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).search(eq("Nordin"), eq("Anton"), eq("man"), eq(1850), eq(1900), any(Pageable.class));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchNormalizesBlankFiltersAndBadaGenderToNull() {
		final var parameters = CensusRecordParameters.create().withLastName("  ").withFirstName("").withGender("Båda");
		when(repositoryMock.search(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of()));

		service.search(parameters);

		verify(repositoryMock).search(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
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
