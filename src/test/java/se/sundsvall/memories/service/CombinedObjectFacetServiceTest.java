package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import se.sundsvall.memories.api.model.CombinedObjectParameters;
import se.sundsvall.memories.api.model.ObjectTypeCount;
import se.sundsvall.memories.integration.db.CombinedObjectRepository;
import se.sundsvall.memories.integration.db.CombinedObjectRepositoryCustom.TypeCount;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static se.sundsvall.memories.configuration.CacheNames.OBJECT_FACETS_CACHE;

@SpringJUnitConfig
class CombinedObjectFacetServiceTest {

	@Configuration
	@EnableCaching
	static class Config {

		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager(OBJECT_FACETS_CACHE);
		}

		@Bean
		CombinedObjectFacetService combinedObjectFacetService(final CombinedObjectRepository combinedObjectRepository) {
			return new CombinedObjectFacetService(combinedObjectRepository);
		}
	}

	@MockitoBean
	private CombinedObjectRepository repositoryMock;

	@Autowired
	private CombinedObjectFacetService service;

	@Autowired
	private CacheManager cacheManager;

	@BeforeEach
	void setUp() {
		cacheManager.getCache(OBJECT_FACETS_CACHE).clear();
		reset(repositoryMock);
		when(repositoryMock.countByType(any())).thenReturn(of(new TypeCount("Foto", 3L)));
	}

	@Test
	void countsEachFilterSetOnceWhateverThePageOrSort() {
		final var first = CombinedObjectParameters.create().withQuery("Sundsvall").withObjectType(List.of("Foto")).withPage(1).withLimit(20);
		final var later = CombinedObjectParameters.create().withQuery("Sundsvall").withObjectType(List.of("Foto")).withPage(4).withLimit(50);
		later.setSortBy(List.of("title"));
		later.setSortDirection(DESC);

		final var firstFacets = service.getFacets(first);
		final var laterFacets = service.getFacets(later);

		assertThat(laterFacets).isSameAs(firstFacets);
		assertThat(firstFacets.typeCounts()).extracting(ObjectTypeCount::getObjectType, ObjectTypeCount::getCount)
			.containsExactly(tuple("Foto", 3L));
		verify(repositoryMock, times(1)).countByType(any());
		verify(repositoryMock, times(1)).countByGender(any());
		verify(repositoryMock, times(1)).countByCategory(any());
		verify(repositoryMock, times(1)).countByTopography(any());
	}

	@Test
	void countsAgainWhenAFilterChanges() {
		service.getFacets(CombinedObjectParameters.create().withQuery("Sundsvall"));
		service.getFacets(CombinedObjectParameters.create().withQuery("Sundsvall").withGender("Kvinna"));
		service.getFacets(CombinedObjectParameters.create().withQuery("Sundsvall").withYearFrom(1900));

		verify(repositoryMock, times(3)).countByType(any());
	}

	@Test
	void keyHoldsTheFiltersOnly() {
		final var parameters = CombinedObjectParameters.create().withQuery("Nordin").withTopographyId(List.of(4)).withPage(3);

		assertThat(CombinedObjectFacetService.key(parameters))
			.containsExactly("Nordin", null, null, null, List.of(4), null, null, null, null, null, null, null);
	}
}
