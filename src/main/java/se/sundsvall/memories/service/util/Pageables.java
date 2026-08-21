package se.sundsvall.memories.service.util;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

/**
 * Builds the {@link Pageable} for a paged search.
 *
 * <p>
 * Every search here ends up ordered by something unique. Without that the database is free to return the rows of an
 * unordered — or ambiguously ordered — query in any order it likes, and it does: two requests for consecutive pages can
 * then repeat a row and skip another, which is the sort of bug that only shows up as "a photo I saw on page 1 is gone
 * from page 2".
 */
public final class Pageables {

	private Pageables() {}

	/**
	 * A page request ordered by what the caller asked for, ending in {@code idAttribute} so that the order is total. A
	 * caller sorting on a name gets ties broken the same way on every page; a caller sorting on nothing at all gets the
	 * id order.
	 *
	 * @param  parameters  the paging and sorting parameters of the request
	 * @param  idAttribute the entity's id attribute, which is unique by definition
	 * @return             the page request to hand to the repository
	 */
	public static Pageable of(final AbstractParameterPagingAndSortingBase parameters, final String idAttribute) {
		return of(parameters, Sort.unsorted(), idAttribute);
	}

	/**
	 * As {@link #of(AbstractParameterPagingAndSortingBase, String)}, with an order to fall back to when the caller asks
	 * for none — for the tree listings, which read best in the order the archive itself sets.
	 *
	 * @param  parameters  the paging and sorting parameters of the request
	 * @param  fallback    the order to use when the caller asks for none
	 * @param  idAttribute the entity's id attribute, which is unique by definition
	 * @return             the page request to hand to the repository
	 */
	public static Pageable of(final AbstractParameterPagingAndSortingBase parameters, final Sort fallback, final String idAttribute) {
		final var requested = parameters.sort();
		final var order = requested.isSorted() ? requested : fallback;

		return PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), order.and(Sort.by(idAttribute)));
	}

	/**
	 * The paging metadata for a page ordered by {@link #of(AbstractParameterPagingAndSortingBase, Sort, String)},
	 * reporting the order without the id the tiebreaker added. The tiebreaker is there to keep the pages from shifting
	 * under the caller, not because they asked to order by an id, and a response saying otherwise would send a client
	 * off looking for a sort it never requested.
	 *
	 * @param  page        the page returned by the repository
	 * @param  idAttribute the id attribute the page request ends with
	 * @return             the metadata to put under {@code _meta}
	 */
	public static PagingAndSortingMetaData metaDataOf(final Page<?> page, final String idAttribute) {
		final var ordered = page.getSort().stream()
			.map(Sort.Order::getProperty)
			.toList();
		final var reported = endsWithTiebreaker(ordered, idAttribute) ? ordered.subList(0, ordered.size() - 1) : ordered;

		return PagingAndSortingMetaData.create()
			.withPageData(page)
			.withSortBy(reported.isEmpty() ? null : reported)
			.withSortDirection(reported.isEmpty() ? null : page.getSort().stream().findFirst().map(Sort.Order::getDirection).orElse(null));
	}

	/**
	 * Whether the last property is the tiebreaker this class appends. A caller who sorts on the id themselves still
	 * sees it, since their own property comes first and only the appended one is dropped.
	 */
	private static boolean endsWithTiebreaker(final List<String> ordered, final String idAttribute) {
		return !ordered.isEmpty() && ordered.getLast().equals(idAttribute);
	}
}
