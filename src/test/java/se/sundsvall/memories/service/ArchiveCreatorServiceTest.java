package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchiveCreatorServiceTest {

	@Mock
	private CombinedObjectRepository repositoryMock;

	@InjectMocks
	private ArchiveCreatorService service;

	@Test
	void getLegalEntityIdsWithObjects() {
		when(repositoryMock.findArchiveCreatorLegalEntityIds()).thenReturn(List.of(10, 20));

		final var result = service.getLegalEntityIdsWithObjects();

		assertThat(result).containsExactlyInAnyOrder(10, 20);
		verify(repositoryMock).findArchiveCreatorLegalEntityIds();
	}
}
