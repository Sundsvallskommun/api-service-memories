package se.sundsvall.memories.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.integration.db.FilmRepository;
import se.sundsvall.memories.integration.db.model.FilmEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

	@Mock
	private FilmRepository repositoryMock;

	@InjectMocks
	private FilmService service;

	@SuppressWarnings("unchecked")
	@Test
	void searchWithQuery() {
		final var query = "sundsvall";
		final var entity = FilmEntity.create().withFilmId(1L).withDoktitel("Sundsvall film");

		when(repositoryMock.findAll(any(Specification.class))).thenReturn(List.of(entity));

		final var result = service.search(query);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getDoktitel()).isEqualTo("Sundsvall film");
		verify(repositoryMock).findAll(any(Specification.class));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithNullQuery() {
		final var entity = FilmEntity.create().withFilmId(1L);

		when(repositoryMock.findAll()).thenReturn(List.of(entity));

		final var result = service.search(null);

		assertThat(result).hasSize(1);
		verify(repositoryMock).findAll();
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void searchWithBlankQuery() {
		final var entity = FilmEntity.create().withFilmId(1L);

		when(repositoryMock.findAll()).thenReturn(List.of(entity));

		final var result = service.search("   ");

		assertThat(result).hasSize(1);
		verify(repositoryMock).findAll();
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void getById() {
		final var id = 1L;
		final var entity = FilmEntity.create().withFilmId(id).withDoktitel("Test");

		when(repositoryMock.findById(id)).thenReturn(Optional.of(entity));

		final var result = service.getById(id);

		assertThat(result).isNotNull();
		assertThat(result.getFilmId()).isEqualTo(id);
		assertThat(result.getDoktitel()).isEqualTo("Test");
		verify(repositoryMock).findById(id);
	}

	@Test
	void getByIdNotFound() {
		final var id = 999L;

		when(repositoryMock.findById(id)).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(id));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Film with id '999' not found");
		verify(repositoryMock).findById(id);
	}
}
