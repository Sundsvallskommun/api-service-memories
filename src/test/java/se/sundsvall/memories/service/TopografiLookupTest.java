package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.integration.db.TopografiRepository;
import se.sundsvall.memories.integration.db.model.TopografiEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopografiLookupTest {

	@Mock
	private TopografiRepository topografiRepositoryMock;

	@InjectMocks
	private TopografiLookup lookup;

	@Test
	void resolvesByTopNamnWhenPresent() {
		when(topografiRepositoryMock.findAll()).thenReturn(List.of(
			TopografiEntity.create().withTId(1).withTopNamn("Sundsvall").withPlats("ignored").withTopKod("ignored")));
		lookup.loadCache();

		assertThat(lookup.resolve(1)).isEqualTo("Sundsvall");
	}

	@Test
	void fallsBackToPlatsWhenTopNamnIsBlank() {
		when(topografiRepositoryMock.findAll()).thenReturn(List.of(
			TopografiEntity.create().withTId(2).withTopNamn("").withPlats("Indal").withTopKod("ignored")));
		lookup.loadCache();

		assertThat(lookup.resolve(2)).isEqualTo("Indal");
	}

	@Test
	void fallsBackToTopKodWhenOthersBlank() {
		when(topografiRepositoryMock.findAll()).thenReturn(List.of(
			TopografiEntity.create().withTId(3).withTopKod("0001")));
		lookup.loadCache();

		assertThat(lookup.resolve(3)).isEqualTo("0001");
	}

	@Test
	void returnsNullForUnknownId() {
		when(topografiRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(999)).isNull();
	}

	@Test
	void returnsNullForNullId() {
		when(topografiRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(null)).isNull();
	}

	@Test
	void skipsEntriesWithNullId() {
		when(topografiRepositoryMock.findAll()).thenReturn(List.of(
			TopografiEntity.create().withTopNamn("Without ID"),
			TopografiEntity.create().withTId(5).withTopNamn("With ID")));
		lookup.loadCache();

		assertThat(lookup.resolve(5)).isEqualTo("With ID");
	}
}
