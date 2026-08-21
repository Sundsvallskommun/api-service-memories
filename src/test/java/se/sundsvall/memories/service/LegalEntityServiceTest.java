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
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.integration.db.LegalEntityRepository;
import se.sundsvall.memories.integration.db.model.CategoryEntity;
import se.sundsvall.memories.integration.db.model.LegalEntityEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class LegalEntityServiceTest {

	@Mock
	private LegalEntityRepository repositoryMock;

	@InjectMocks
	private LegalEntityService service;

	@Test
	void searchDelegatesAndResolvesAssociations() {
		final var pageable = PageRequest.of(0, 100, Sort.by("legalEntityId"));
		final var parameters = LegalEntityParameters.create()
			.withName("Nödhjälp")
			.withLocation("Sundsvall")
			.withCategoryId(5)
			.withYearFrom(1880)
			.withYearTo(1920)
			.withPage(1)
			.withLimit(100);
		final var entity = LegalEntityEntity.create().withLegalEntityId(1).withName("Nödhjälpskommittén")
			.withTopography(TopographyEntity.create().withId(42).withName("Sundsvalls kommun"))
			.withCategory(CategoryEntity.create().withCategoryId(5).withName("Kommitté"));

		when(repositoryMock.findAllByParameters(any(LegalEntityParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		final var result = service.search(parameters);

		assertThat(result.getLegalEntities()).hasSize(1);
		assertThat(result.getLegalEntities().getFirst().getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getLegalEntities().getFirst().getCategory()).isEqualTo("Kommitté");
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).findAllByParameters(any(LegalEntityParameters.class), eq(pageable));
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100, Sort.by("legalEntityId"));
		final var parameters = LegalEntityParameters.create().withName("  ").withLocation("");

		when(repositoryMock.findAllByParameters(any(LegalEntityParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getLegalEntities()).isEmpty();
		// Trimming and the "blank means no filter" rule live in the specifications, verified against a real database in
		// LegalEntitySpecificationTest. The service only has to hand the parameters over untouched.
		final var parametersCaptor = ArgumentCaptor.forClass(LegalEntityParameters.class);
		verify(repositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
	}

	@Test
	void getByIdFound() {
		when(repositoryMock.findVisibleById(1)).thenReturn(Optional.of(LegalEntityEntity.create().withLegalEntityId(1).withName("Berg AB")
			.withTopography(TopographyEntity.create().withId(7).withName("Sundsvall"))
			.withCategory(CategoryEntity.create().withCategoryId(2).withName("Aktiebolag"))));

		final var result = service.getById(1);

		assertThat(result.getLegalEntityId()).isEqualTo(1);
		assertThat(result.getName()).isEqualTo("Berg AB");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getCategory()).isEqualTo("Aktiebolag");
		verify(repositoryMock).findVisibleById(1);
	}

	@Test
	void getByIdNotFound() {
		when(repositoryMock.findVisibleById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(999))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Legal entity with id '999' not found");
		verify(repositoryMock).findVisibleById(999);
	}

	/**
	 * The placeholder row {@code J_ID = 1} ("ingen") is filtered out by the repository query, so the service must surface
	 * it as an ordinary 404 rather than returning the sentinel.
	 */
	@Test
	void getByIdPlaceholderNotFound() {
		when(repositoryMock.findVisibleById(1)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(1))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND)
			.hasMessageContaining("Legal entity with id '1' not found");
		verify(repositoryMock).findVisibleById(1);
	}
}
