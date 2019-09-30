package ascelion.micro.customers;

import javax.enterprise.context.ApplicationScoped;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository(forEntity = Customer.class)
@ApplicationScoped
public interface CustomersRepository extends EntityRepository<Customer, Long> {
}
