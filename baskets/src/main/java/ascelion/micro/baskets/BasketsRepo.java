package ascelion.micro.baskets;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface BasketsRepo extends EntityRepository<Basket> {
}
