package ascelion.micro.shared.endpoint;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

import javax.persistence.Transient;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.model.EntityRepo;

import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public abstract class ViewEntityEndpointBase<T extends AbstractEntity<T>, R extends EntityRepo<T>> implements ViewEntityEndpoint<T> {

	@SuppressWarnings("unchecked")
	protected final Class<T> type = (Class<T>) resolveTypeArguments(getClass(), ViewEntityEndpoint.class)[0];

	protected final R repo;

	@Autowired
	protected BeanToBeanMapper bbm;

	@Override
	public List<T> getEntities(String[] sortBy, Integer page, int size) {
		final var sort = sortBy != null && sortBy.length > 0 ? Sort.by(sortBy) : Sort.unsorted();

		if (page == null) {
			return this.repo.findAll(sort);
		}

		return this.repo.findAll(PageRequest.of(page, size, sort)).getContent();
	}

	@Override
	public List<T> searchEntities(Search<T> search) {
		var matcher = search.any ? ExampleMatcher.matchingAny() : ExampleMatcher.matchingAll();

		matcher = matcher.withIgnoreCase(true);

		for (Class<?> t = this.type; t != AbstractEntity.class; t = t.getSuperclass()) {
			for (final Field f : t.getDeclaredFields()) {
				if (f.getType() != String.class) {
					continue;
				}

				final int m = f.getModifiers();

				if (Modifier.isTransient(m)) {
					continue;
				}
				if (Modifier.isStatic(m)) {
					continue;
				}
				if (f.isAnnotationPresent(Transient.class)) {
					continue;
				}

				matcher = matcher.withMatcher(f.getName(), ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
			}
		}

		final var example = Example.of(search.probe, matcher);
		final var sort = search.sort != null && search.sort.length > 0 ? Sort.by(search.sort) : Sort.unsorted();

		if (search.page == null) {
			return this.repo.findAll(example, sort);
		}

		final var page = PageRequest.of(search.page, search.size, sort);

		return this.repo.findAll(example, page).getContent();
	}

	@Override
	public T getEntity(UUID id) {
		return this.repo.getById(id);
	}

}
