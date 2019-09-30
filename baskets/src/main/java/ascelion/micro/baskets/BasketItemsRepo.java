package ascelion.micro.baskets;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketItemsRepo extends EntityRepository<BasketItem> {

	default BasketItem getByProductId(UUID basketId, UUID productId) {
		return findByProductId(basketId, productId)
				.orElseThrow(() -> new EntityNotFoundException("no such item: " + productId));
	}

	@Query("" +
			"SELECT i FROM BasketItem i" +
			" WHERE i.productId = :productId " +
			"   AND i.basket.id = :basketId" +
			"")
	Optional<BasketItem> findByProductId(@Param("basketId") UUID basketId, @Param("productId") UUID productId);
}
