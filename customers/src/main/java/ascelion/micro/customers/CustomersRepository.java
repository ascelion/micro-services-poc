package ascelion.micro.customers;

import ascelion.micro.shared.model.EntityRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends EntityRepository<Customer> {
}
