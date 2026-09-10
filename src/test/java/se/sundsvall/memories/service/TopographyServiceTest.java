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
	void getTopographiesMapsEveryPlace() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of(
			TopographyEntity.create().withId(1).withName("Sundsvall").withCode("SUN"),
			TopographyEntity.create().withId(2).withName("Timrå").withCode("TIM")));

		final var result = service.getTopographies();

		assertThat(result).extracting(Topography::getTopographyId, Topography::getDisplayName, Topography::getCode)
			.containsExactly(tuple(1, "Sundsvall", "SUN"), tuple(2, "Timrå", "TIM"));
		verify(repositoryMock).findAllSelectable();
	}

	/**
	 * Swedish order, not the database's: Å, Ä and Ö come after Z, which a {@code general_ci} collation would not give.
	 * Places sharing a display name keep a stable order, by id.
	 */
	@Test
	void getTopographiesSortsByDisplayNameInSwedishOrder() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of(
			TopographyEntity.create().withId(1).withName("Överhörnäs"),
			TopographyEntity.create().withId(2).withName("Bergsåker"),
			TopographyEntity.create().withId(3).withName("Ånge"),
			TopographyEntity.create().withId(4).withName("Alnö"),
			TopographyEntity.create().withId(6).withName("Ävja"),
			TopographyEntity.create().withId(9).withName("Alnö"),
			TopographyEntity.create().withId(5).withPlace("Zäta")));

		assertThat(service.getTopographies()).extracting(Topography::getTopographyId, Topography::getDisplayName)
			.containsExactly(
				tuple(4, "Alnö"), tuple(9, "Alnö"), tuple(2, "Bergsåker"), tuple(5, "Zäta"),
				tuple(3, "Ånge"), tuple(6, "Ävja"), tuple(1, "Överhörnäs"));
	}

	/**
	 * The repository leaves out the rows blank in every column, but in the database's terms. getDisplayName() has the
	 * last word: a row whose only value is whitespace has no name to show, whatever the column's collation makes of it.
	 */
	@Test
	void getTopographiesLeavesOutAPlaceWithNoDisplayName() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of(
			TopographyEntity.create().withId(1).withName("Sundsvall"),
			TopographyEntity.create().withId(2).withName("\t").withCode("  ").withPlace(""),
			TopographyEntity.create().withId(3).withMunicipality("Sverige")));

		assertThat(service.getTopographies()).extracting(Topography::getTopographyId).containsExactly(1);
		verify(repositoryMock).findAllSelectable();
	}

	@Test
	void getTopographiesWhenThereAreNone() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of());

		assertThat(service.getTopographies()).isEmpty();
		verify(repositoryMock).findAllSelectable();
	}
}
