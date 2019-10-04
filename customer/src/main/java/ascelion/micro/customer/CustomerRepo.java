package ascelion.micro.customer;

import ascelion.micro.customer.api.Customer;
import ascelion.micro.shared.model.EntityRepo;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends EntityRepo<Customer> {
}
