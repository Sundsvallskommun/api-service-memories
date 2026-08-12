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

	// The display-name fallback this class used to own now lives on OcmEntity.getDisplayName() and is tested there.
	// What remains here is the cache itself.

	@Test
	void skipsEntriesWithNullId() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of(
			OcmEntity.create().withText("Without ID"),
			OcmEntity.create().withId(5).withText("With ID")));
		lookup.loadCache();

		assertThat(lookup.resolveSubject(5).getText()).isEqualTo("With ID");
	}

	@Test
	void resolveSubjectReturnsCodeTextDescription() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of(
			OcmEntity.create().withId(10).withCode("MID").withText("Midsommar").withDescription("Swedish midsummer")));
		lookup.loadCache();

		final var subject = lookup.resolveSubject(10);
		assertThat(subject).isNotNull();
		assertThat(subject.getCode()).isEqualTo("MID");
		assertThat(subject.getText()).isEqualTo("Midsommar");
		assertThat(subject.getDescription()).isEqualTo("Swedish midsummer");
	}

	@Test
	void resolveSubjectReturnsNullForNullId() {
		assertThat(lookup.resolveSubject(null)).isNull();
	}

	@Test
	void resolveSubjectReturnsNullForUnknownId() {
		when(ocmRepositoryMock.findAll()).thenReturn(List.of());
		lookup.loadCache();

		assertThat(lookup.resolveSubject(999)).isNull();
	}
}
