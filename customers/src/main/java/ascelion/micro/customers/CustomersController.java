package ascelion.micro.customers;

import ascelion.micro.endpoint.Endpoint;
import ascelion.micro.endpoint.EntityEndpoint;

@Endpoint("customers")
public class CustomersController extends EntityEndpoint<Customer, CustomerRequest> {
	public CustomersController(CustomersRepository repo) {
		super(repo);
	}
}
