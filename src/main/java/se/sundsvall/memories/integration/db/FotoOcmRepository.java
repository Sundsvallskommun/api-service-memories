package se.sundsvall.memories.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.memories.integration.db.model.FotoOcmEntity;

@CircuitBreaker(name = "fotoOcmRepository")
public interface FotoOcmRepository extends JpaRepository<FotoOcmEntity, Integer> {

	List<FotoOcmEntity> findByPhotoIdOrderById(Integer photoId);
}
