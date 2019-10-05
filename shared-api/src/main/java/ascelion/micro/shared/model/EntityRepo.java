package ascelion.micro.shared.model;

import java.beans.Introspector;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepo<E extends AbstractEntity<E>> extends JpaRepository<E, UUID> {

	default E getById(String id) {
		return getById(UUID.fromString(id));
	}

	default E getById(UUID id) {
		return findById(id)
				.orElseThrow(() -> {
					final var type = resolveTypeArguments(getClass(), EntityRepo.class)[0];
					final var name = ofNullable(type.getAnnotation(Entity.class))
							.map(Entity::name)
							.filter(s -> s.length() > 0)
							.orElse(Introspector.decapitalize(type.getSimpleName()));

					return new EntityNotFoundException(format("Cannot find %s with id %s", name, id));
				});
	}
}
