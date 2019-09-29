package ascelion.micro.baskets;

import java.util.Optional;
import java.util.UUID;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public interface BasketsRepository extends EntityRepository<Basket> {
	default Basket getByItemId(UUID id) {
		return findByItemId(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no such item: " + id));
	}

	Optional<Basket> findByItemId(UUID id);
}
