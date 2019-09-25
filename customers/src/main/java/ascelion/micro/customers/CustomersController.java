package ascelion.micro.customers;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpoint;

@Endpoint("customers")
public class CustomersController extends EntityEndpoint<Customer, CustomerRequest> {
	public CustomersController(CustomersRepository repo) {
		super(repo);
	}
}
