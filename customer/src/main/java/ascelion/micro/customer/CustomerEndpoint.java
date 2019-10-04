package ascelion.micro.customer;

import ascelion.micro.customer.api.Customer;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpointBase;

@Endpoint("customers")
public class CustomerEndpoint extends EntityEndpointBase<Customer, CustomerRepo, CustomerRequest> {
	public CustomerEndpoint(CustomerRepo repo) {
		super(repo);
	}
}
