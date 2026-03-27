package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.memories.integration.db.model.FilmEntity;

@CircuitBreaker(name = "filmRepository")
public interface FilmRepository extends JpaRepository<FilmEntity, Long>, JpaSpecificationExecutor<FilmEntity> {

}
