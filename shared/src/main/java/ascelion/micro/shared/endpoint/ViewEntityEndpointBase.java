package ascelion.micro.shared.endpoint;

import java.util.List;
import java.util.UUID;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.model.EntityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public abstract class ViewEntityEndpointBase<T extends AbstractEntity<T>, R extends EntityRepository<T>> implements ViewEntityEndpoint<T> {
	protected final R repo;

	@Autowired
	protected BeanToBeanMapper bbm;

	@Override
	public List<T> getEntities(String[] properties, Integer page, int size) {
		final Sort s = properties != null && properties.length > 0 ? Sort.by(properties) : Sort.unsorted();

		if (page == null) {
			return this.repo.findAll(s);
		} else {
			return this.repo.findAll(PageRequest.of(page, size, s)).getContent();
		}
	}

	@Override
	public T getEntity(UUID id) {
		return this.repo.getById(id);
	}

}
