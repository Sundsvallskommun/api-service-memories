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
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class LegalEntityServiceTest {

	@Mock
	private LegalEntityRepository repositoryMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@Mock
	private CategoryLookup categoryLookupMock;

	@InjectMocks
	private LegalEntityService service;

	@Test
	void searchDelegatesAndResolvesLookups() {
		final var parameters = LegalEntityParameters.create()
			.withName("Nödhjälp")
			.withLocation("Sundsvall")
			.withCategoryId(5)
			.withYearFrom(1880)
			.withYearTo(1920)
			.withPage(1)
			.withLimit(100);
		final var entity = LegalEntityEntity.create().withLegalEntityId(1).withName("Nödhjälpskommittén").withTopographyId(42).withCategoryId(5);
		when(repositoryMock.search(eq("Nödhjälp"), eq("Sundsvall"), eq(5), eq(1880), eq(1920), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 100), 1));
		when(topographyLookupMock.resolve(42)).thenReturn("Sundsvalls kommun");
		when(categoryLookupMock.resolve(5)).thenReturn("Kommitté");

		final var result = service.search(parameters);

		assertThat(result.getLegalEntities()).hasSize(1);
		assertThat(result.getLegalEntities().getFirst().getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getLegalEntities().getFirst().getCategory()).isEqualTo("Kommitté");
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).search(eq("Nödhjälp"), eq("Sundsvall"), eq(5), eq(1880), eq(1920), any(Pageable.class));
	}

	@Test
	void searchNormalizesBlankTextFiltersToNull() {
		final var parameters = LegalEntityParameters.create().withName("  ").withLocation("");
		when(repositoryMock.search(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of()));

		service.search(parameters);

		verify(repositoryMock).search(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
	}

	@Test
	void getByIdFound() {
		when(repositoryMock.findById(1)).thenReturn(Optional.of(LegalEntityEntity.create().withLegalEntityId(1).withName("Berg AB").withTopographyId(7).withCategoryId(2)));
		when(topographyLookupMock.resolve(7)).thenReturn("Sundsvall");
		when(categoryLookupMock.resolve(2)).thenReturn("Aktiebolag");

		final var result = service.getById(1);

		assertThat(result.getLegalEntityId()).isEqualTo(1);
		assertThat(result.getName()).isEqualTo("Berg AB");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getCategory()).isEqualTo("Aktiebolag");
		verify(repositoryMock).findById(1);
	}

	@Test
	void getByIdNotFound() {
		when(repositoryMock.findById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(999))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Legal entity with id '999' not found");
		verify(repositoryMock).findById(999);
	}
}
