package se.sundsvall.memories.integration.db.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.memories.integration.db.specification.FilmSpecification.withQuery;

@ExtendWith(MockitoExtension.class)
class FilmSpecificationTest {

	@SuppressWarnings("rawtypes")
	@Mock
	private Root root;

	@SuppressWarnings("rawtypes")
	@Mock
	private CriteriaQuery criteriaQuery;

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@SuppressWarnings("rawtypes")
	@Mock
	private Expression expression;

	@Mock
	private Predicate predicate;

	@SuppressWarnings("unchecked")
	@Test
	void withQueryCreatesSpecification() {
		when(root.get(anyString())).thenReturn(root);
		when(criteriaBuilder.lower(any())).thenReturn(expression);
		when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);
		when(criteriaBuilder.or(any(Predicate[].class))).thenReturn(predicate);

		final var specification = withQuery("sundsvall");
		final var result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

		assertThat(result).isNotNull();
		verify(criteriaBuilder).or(any(Predicate[].class));
	}
}
