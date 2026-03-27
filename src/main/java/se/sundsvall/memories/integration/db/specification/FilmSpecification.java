package se.sundsvall.memories.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.memories.integration.db.model.FilmEntity;

public interface FilmSpecification {

	static Specification<FilmEntity> withQuery(final String query) {
		final var pattern = "%" + query.toLowerCase() + "%";

		return (root, _, cb) -> cb.or(
			cb.like(cb.lower(root.get("doktitel")), pattern),
			cb.like(cb.lower(root.get("filnamn")), pattern),
			cb.like(cb.lower(root.get("filmOplats")), pattern),
			cb.like(cb.lower(root.get("kommentFilm")), pattern),
			cb.like(cb.lower(root.get("datum")), pattern));
	}
}
