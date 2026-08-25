package se.sundsvall.memories.service.util;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingAndSortingBase;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static java.util.function.Predicate.not;

/**
 * Builds the {@link Pageable} for a paged search. Every search ends up ordered by something unique, since an
 * ambiguously ordered query lets consecutive pages repeat one row and skip another.
 */
public final class Pageables {

	private Pageables() {}

	/**
	 * A page request ordered by what the caller asked for, ending in {@code idAttribute} so that the order is total.
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
	 * for none. Used by the tree listings, which default to the archive's own order.
	 *
	 * @param  parameters  the paging and sorting parameters of the request
	 * @param  fallback    the order to use when the caller asks for none
	 * @param  idAttribute the entity's id attribute, which is unique by definition
	 * @return             the page request to hand to the repository
	 */
	public static Pageable of(final AbstractParameterPagingAndSortingBase parameters, final Sort fallback, final String idAttribute) {
		final var requested = parameters.sort();
		final var order = Optional.of(requested)
			.filter(Sort::isSorted)
			.orElse(fallback);

		return PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), order.and(Sort.by(idAttribute)));
	}

	/**
	 * An unordered page request, for the combined object search: it orders itself from its specification, which Spring
	 * Data would override if the page request carried a sort. It appends the id tiebreak itself.
	 *
	 * @param  parameters the paging and sorting parameters of the request
	 * @return            the page request to hand to the repository
	 */
	public static Pageable unordered(final AbstractParameterPagingAndSortingBase parameters) {
		return PageRequest.of(parameters.getPage() - 1, parameters.getLimit());
	}

	/**
	 * The paging metadata for a page from {@link #unordered(AbstractParameterPagingAndSortingBase)}, which carries no
	 * sort to read the order back from. Reports the caller's own sort only, not relevance or the id tiebreak.
	 *
	 * @param  page       the page returned by the repository
	 * @param  parameters the paging and sorting parameters of the request
	 * @return            the metadata to put under {@code _meta}
	 */
	public static PagingAndSortingMetaData metaDataOf(final Page<?> page, final AbstractParameterPagingAndSortingBase parameters) {
		final var reported = Optional.ofNullable(parameters.getSortBy())
			.filter(not(List::isEmpty));

		return PagingAndSortingMetaData.create()
			.withPageData(page)
			.withSortBy(reported.orElse(null))
			.withSortDirection(reported
				.flatMap(_ -> parameters.sort().stream().findFirst().map(Sort.Order::getDirection))
				.orElse(null));
	}

	/**
	 * The paging metadata for a page ordered by {@link #of(AbstractParameterPagingAndSortingBase, Sort, String)},
	 * reporting the caller's order without the appended id tiebreak.
	 *
	 * @param  page        the page returned by the repository
	 * @param  idAttribute the id attribute the page request ends with
	 * @return             the metadata to put under {@code _meta}
	 */
	public static PagingAndSortingMetaData metaDataOf(final Page<?> page, final String idAttribute) {
		final var ordered = page.getSort().stream()
			.map(Sort.Order::getProperty)
			.toList();
		final var withoutTiebreaker = Optional.of(ordered)
			.filter(properties -> endsWithTiebreaker(properties, idAttribute))
			.map(properties -> properties.subList(0, properties.size() - 1))
			.orElse(ordered);
		final var reported = Optional.of(withoutTiebreaker).filter(not(List::isEmpty));

		return PagingAndSortingMetaData.create()
			.withPageData(page)
			.withSortBy(reported.orElse(null))
			.withSortDirection(reported
				.flatMap(_ -> page.getSort().stream().findFirst().map(Sort.Order::getDirection))
				.orElse(null));
	}

	/** Whether the last property is the appended tiebreak. A caller sorting on the id themselves still sees theirs. */
	private static boolean endsWithTiebreaker(final List<String> ordered, final String idAttribute) {
		return !ordered.isEmpty() && ordered.getLast().equals(idAttribute);
	}
}
