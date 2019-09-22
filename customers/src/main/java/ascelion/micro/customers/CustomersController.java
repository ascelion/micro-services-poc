package ascelion.micro.customers;

import ascelion.micro.endpoint.EntityEndpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers")
public class CustomersController extends EntityEndpoint<Customer, CustomerRequest> {
	public CustomersController(CustomersRepository repo) {
		super(repo);
	}
}
