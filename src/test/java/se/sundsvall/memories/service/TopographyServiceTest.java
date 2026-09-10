package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.api.model.Topography;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopographyServiceTest {

	@Mock
	private TopographyRepository repositoryMock;

	@InjectMocks
	private TopographyService service;

	@Test
	void getTopographiesDelegatesAndKeepsTheRepositoryOrder() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of(
			TopographyEntity.create().withId(1).withName("Sundsvall").withCode("SUN"),
			TopographyEntity.create().withId(2).withName("Timrå").withCode("TIM")));

		final var result = service.getTopographies();

		assertThat(result).extracting(Topography::getTopographyId, Topography::getDisplayName, Topography::getCode)
			.containsExactly(tuple(1, "Sundsvall", "SUN"), tuple(2, "Timrå", "TIM"));
		verify(repositoryMock).findAllSelectable();
	}

	@Test
	void getTopographiesWhenThereAreNone() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of());

		assertThat(service.getTopographies()).isEmpty();
		verify(repositoryMock).findAllSelectable();
	}
}
