package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepository.TypeCount;
import se.sundsvall.memories.integration.db.model.CombinedObjectEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CombinedObjectServiceTest {

	@Mock
	private CombinedObjectRepository repositoryMock;

	@Mock
	private TopographyLookup topographyLookupMock;

	@InjectMocks
	private CombinedObjectService service;

	private static TypeCount typeCount(final String type, final long total) {
		final var tc = mock(TypeCount.class);
		when(tc.getObjectType()).thenReturn(type);
		when(tc.getTotal()).thenReturn(total);
		return tc;
	}

	@Test
	void searchDelegatesResolvesLocationAndBuildsTypeCounts() {
		final var parameters = CombinedObjectParameters.create().withQuery("Sundsvall").withYearFrom(1900).withYearTo(1950).withLocation("Sundsvall").withPage(1).withLimit(100);
		final var entity = CombinedObjectEntity.create().withObjectKey("foto-1001").withObjectType("Foto").withTitle("Stadsvy").withTopographyId(1);
		final var fotoCount = typeCount("Foto", 1L);
		final var textCount = typeCount("Text", 3L);
		when(repositoryMock.search(eq("Sundsvall"), eq(1900), eq(1950), eq("Sundsvall"), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 100), 1));
		when(repositoryMock.countByType("Sundsvall", 1900, 1950, "Sundsvall")).thenReturn(List.of(fotoCount, textCount));
		when(topographyLookupMock.resolve(1)).thenReturn("Sundsvalls kommun");

		final var result = service.search(parameters);

		assertThat(result.getObjects()).hasSize(1);
		assertThat(result.getObjects().getFirst().getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getTypeCounts()).containsExactly(entry("Foto", 1L), entry("Text", 3L));
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(repositoryMock).search(eq("Sundsvall"), eq(1900), eq(1950), eq("Sundsvall"), any(Pageable.class));
	}

	@Test
	void searchNormalizesBlankFiltersToNull() {
		final var parameters = CombinedObjectParameters.create().withQuery("   ").withLocation("");
		when(repositoryMock.search(isNull(), isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
		when(repositoryMock.countByType(isNull(), isNull(), isNull(), isNull())).thenReturn(List.of());

		final var result = service.search(parameters);

		assertThat(result.getObjects()).isEmpty();
		assertThat(result.getTypeCounts()).isEmpty();
		verify(repositoryMock).search(isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
		verify(repositoryMock).countByType(isNull(), isNull(), isNull(), isNull());
	}
}
