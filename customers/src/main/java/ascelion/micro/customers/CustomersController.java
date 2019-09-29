package ascelion.micro.customers;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpointBase;

@Endpoint("customers")
public class CustomersController extends EntityEndpointBase<Customer, CustomersRepository, CustomerRequest> {
	public CustomersController(CustomersRepository repo) {
		super(repo);
	}
}
