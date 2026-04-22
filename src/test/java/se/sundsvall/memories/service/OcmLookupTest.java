package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.integration.db.OcmRepository;
import se.sundsvall.memories.integration.db.model.OcmEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcmLookupTest {

	@Mock
	private OcmRepository ocmRepositoryMock;

	@InjectMocks
	private OcmLookup lookup;

	@Test
	void resolvesByTextWhenPresent() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of(
			OcmEntity.create().withId(1).withText("Midsommar").withDescription("ignored").withCode("ignored")));
		lookup.loadCache();

		assertThat(lookup.resolve(1)).isEqualTo("Midsommar");
	}

	@Test
	void fallsBackToDescriptionWhenTextBlank() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of(
			OcmEntity.create().withId(2).withText("").withDescription("Folkfest").withCode("ignored")));
		lookup.loadCache();

		assertThat(lookup.resolve(2)).isEqualTo("Folkfest");
	}

	@Test
	void fallsBackToCodeWhenOthersBlank() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of(
			OcmEntity.create().withId(3).withCode("MID")));
		lookup.loadCache();

		assertThat(lookup.resolve(3)).isEqualTo("MID");
	}

	@Test
	void returnsNullForUnknownId() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(999)).isNull();
	}

	@Test
	void returnsNullForNullId() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolve(null)).isNull();
	}

	@Test
	void skipsEntriesWithNullId() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of(
			OcmEntity.create().withText("Without ID"),
			OcmEntity.create().withId(5).withText("With ID")));
		lookup.loadCache();

		assertThat(lookup.resolve(5)).isEqualTo("With ID");
	}
}
