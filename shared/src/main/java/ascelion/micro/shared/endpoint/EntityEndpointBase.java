package ascelion.micro.shared.endpoint;

import java.util.UUID;

import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.shared.model.EntityRepository;

import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class EntityEndpointBase<T extends AbstractEntity<T>, R extends EntityRepository<T>, U>
		extends ViewEntityEndpointBase<T, R>
		implements EntityEndpoint<T, U> {

	private final Class<T> type = (Class<T>) resolveTypeArguments(getClass(), EntityEndpointBase.class)[0];

	public EntityEndpointBase(R repo) {
		super(repo);
	}

	@Override
	public T createEntity(U request) {
		return this.repo.save(this.bbm.create(this.type, request));
	}

	@Override
	public T updateEntity(UUID id, U request) {
		return this.repo.findById(id)
				.map(ent -> this.bbm.copyWithNulls(ent, request))
				.map(this.repo::save)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such entity"));
	}

	@Override
	public T patchEntity(UUID id, U patch) {
		return this.repo.findById(id)
				.map(ent -> this.bbm.copyWithoutNulls(ent, patch))
				.map(this.repo::save)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such entity"));
	}
}
