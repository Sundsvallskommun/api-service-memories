package se.sundsvall.memories.service.util;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import se.sundsvall.memories.api.model.PersonParameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

class PageablesTest {

	private static PersonParameters parameters(final List<String> sortBy, final Sort.Direction direction) {
		final var parameters = PersonParameters.create().withPage(1).withLimit(100);
		parameters.setSortBy(sortBy);
		parameters.setSortDirection(direction);
		return parameters;
	}

	/**
	 * Without a total order the database may return the rows of one page in any order it likes, which lets a row show
	 * up twice across two pages while another is never returned at all.
	 */
	@Test
	void ordersByTheIdWhenNothingIsRequested() {
		final var pageable = Pageables.of(parameters(null, null), "personId");

		assertThat(pageable.getSort()).containsExactly(Sort.Order.asc("personId"));
	}

	@Test
	void breaksTiesInTheRequestedOrderWithTheId() {
		final var pageable = Pageables.of(parameters(List.of("lastName"), DESC), "personId");

		assertThat(pageable.getSort()).containsExactly(Sort.Order.desc("lastName"), Sort.Order.asc("personId"));
	}

	@Test
	void fallsBackToTheGivenOrderBeforeTheId() {
		final var pageable = Pageables.of(parameters(null, null), Sort.by("sortOrder", "name"), "id");

		assertThat(pageable.getSort()).containsExactly(Sort.Order.asc("sortOrder"), Sort.Order.asc("name"), Sort.Order.asc("id"));
	}

	/**
	 * The tiebreaker is there to keep the pages from shifting, not because the caller asked to order by an id, so it
	 * stays out of the response.
	 */
	@Test
	void metaDataLeavesTheTiebreakerOut() {
		final var page = new PageImpl<>(List.of("a"), PageRequest.of(0, 100, Sort.by("personId")), 1);

		final var metaData = Pageables.metaDataOf(page, "personId");

		assertThat(metaData.getSortBy()).isNull();
		assertThat(metaData.getSortDirection()).isNull();
		assertThat(metaData.getTotalRecords()).isEqualTo(1);
	}

	@Test
	void metaDataReportsTheOrderTheCallerSees() {
		final var page = new PageImpl<>(List.of("a"), PageRequest.of(0, 100, Sort.by(DESC, "lastName", "personId")), 1);

		final var metaData = Pageables.metaDataOf(page, "personId");

		assertThat(metaData.getSortBy()).containsExactly("lastName");
		assertThat(metaData.getSortDirection()).isEqualTo(DESC);
	}

	/**
	 * A page ordered by something else entirely is reported as it is: only the tiebreaker this class appends is
	 * dropped, and it is recognised by name.
	 */
	@Test
	void metaDataLeavesAnUnrelatedOrderAlone() {
		final var page = new PageImpl<>(List.of("a"), PageRequest.of(0, 100, Sort.by("lastName", "firstName")), 1);

		final var metaData = Pageables.metaDataOf(page, "personId");

		assertThat(metaData.getSortBy()).containsExactly("lastName", "firstName");
	}
}
