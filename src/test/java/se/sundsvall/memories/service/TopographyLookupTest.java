package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.integration.db.model.TopographyEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopographyLookupTest {

	@Mock
	private TopographyRepository topographyRepositoryMock;

	@InjectMocks
	private TopographyLookup lookup;

	@Test
	void resolvesByNameWhenPresent() {
		when(topographyRepositoryMock.findAll()).thenReturn(List.of(
			TopographyEntity.create().withTId(1).withName("Sundsvall").withPlace("ignored").withCode("ignored")));
		lookup.loadCache();

		assertThat(lookup.resolve(1)).isEqualTo("Sundsvall");
	}

	@Test
	void fallsBackToPlaceWhenNameIsBlank() {
		when(topographyRepositoryMock.findAll()).thenReturn(List.of(
			TopographyEntity.create().withTId(2).withName("").withPlace("Indal").withCode("ignored")));
		lookup.loadCache();

		assertThat(lookup.resolve(2)).isEqualTo("Indal");
	}

	@Test
	void fallsBackToCodeWhenOthersBlank() {
		when(topographyRepositoryMock.findAll()).thenReturn(List.of(
			TopographyEntity.create().withTId(3).withCode("0001")));
		lookup.loadCache();

		assertThat(lookup.resolve(3)).isEqualTo("0001");
	}

	@Test
	void returnsNullForUnknownId() {
		when(topographyRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(999)).isNull();
	}

	@Test
	void returnsNullForNullId() {
		when(topographyRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(null)).isNull();
	}

	@Test
	void skipsEntriesWithNullId() {
		when(topographyRepositoryMock.findAll()).thenReturn(List.of(
			TopographyEntity.create().withName("Without ID"),
			TopographyEntity.create().withTId(5).withName("With ID")));
		lookup.loadCache();

		assertThat(lookup.resolve(5)).isEqualTo("With ID");
	}
}
