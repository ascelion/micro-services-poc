package ascelion.micro.shared.model;

import java.beans.Introspector;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository<E extends AbstractEntity<E>> extends JpaRepository<E, UUID> {
	default E getById(UUID id) {
		return findById(id)
				.orElseThrow(() -> {
					final Class<?> type = resolveTypeArguments(getClass(), EntityRepository.class)[0];
					final String name = ofNullable(type.getAnnotation(Entity.class))
							.map(Entity::name)
							.filter(s -> s.length() > 0)
							.orElse(Introspector.decapitalize(type.getSimpleName()));

					return new EntityNotFoundException(format("Cannot find %s with id %s", name, id));
				});
	}
}
