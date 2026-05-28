package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.integration.db.PublicationTypeRepository;
import se.sundsvall.memories.integration.db.model.PublicationTypeEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationTypeLookupTest {

	@Mock
	private PublicationTypeRepository repositoryMock;

	@InjectMocks
	private PublicationTypeLookup lookup;

	@Test
	void resolvesWhenPresent() {
		when(repositoryMock.findAll()).thenReturn(List.of(
			PublicationTypeEntity.create().withId(1).withPublicationType("Tidning")));
		lookup.loadCache();

		assertThat(lookup.resolve(1)).isEqualTo("Tidning");
	}

	@Test
	void skipsBlankEntries() {
		when(repositoryMock.findAll()).thenReturn(List.of(
			PublicationTypeEntity.create().withId(2).withPublicationType("   "),
			PublicationTypeEntity.create().withId(3).withPublicationType("Broschyr")));
		lookup.loadCache();

		assertThat(lookup.resolve(2)).isNull();
		assertThat(lookup.resolve(3)).isEqualTo("Broschyr");
	}

	@Test
	void returnsNullForUnknownId() {
		when(repositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(999)).isNull();
	}

	@Test
	void returnsNullForNullId() {
		when(repositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(null)).isNull();
	}

	@Test
	void lazilyLoadsCacheWhenEmpty() {
		when(repositoryMock.findAll()).thenReturn(List.of(
			PublicationTypeEntity.create().withId(7).withPublicationType("Affisch")));

		assertThat(lookup.resolve(7)).isEqualTo("Affisch");
	}

	@Test
	void skipsEntriesWithNullId() {
		when(repositoryMock.findAll()).thenReturn(List.of(
			PublicationTypeEntity.create().withPublicationType("Without ID"),
			PublicationTypeEntity.create().withId(5).withPublicationType("With ID")));
		lookup.loadCache();

		assertThat(lookup.resolve(5)).isEqualTo("With ID");
	}
}
