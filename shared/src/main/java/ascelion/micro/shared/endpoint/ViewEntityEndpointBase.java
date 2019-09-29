package ascelion.micro.shared.endpoint;

import java.util.List;
import java.util.UUID;

import ascelion.micro.mapper.BeanToBeanMapper;
import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.model.EntityRepository;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public abstract class ViewEntityEndpointBase<T extends AbstractEntity<T>, R extends EntityRepository<T>> implements ViewEntityEndpoint<T> {
	protected final R repo;

	@Autowired
	protected BeanToBeanMapper bbm;

	@Override
	public List<T> getEntities(Integer page, int size) {
		return ofNullable(page)
				.map(p -> PageRequest.of(p, size))
				.map(this.repo::findAll)
				.map(Page::getContent)
				.orElseGet(this.repo::findAll);
	}

	@Override
	public T getEntity(UUID id) {
		return this.repo.getById(id);
	}

}
