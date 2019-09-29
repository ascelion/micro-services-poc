package ascelion.micro.shared.model;

import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository<E extends AbstractEntity<E>> extends JpaRepository<E, UUID> {
	default E getById(UUID id) {
		return findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find entity with id " + id));
	}
}
