package ascelion.micro.products;

import ascelion.micro.endpoint.EntityEndpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductsController extends EntityEndpoint<Product, ProductRequest> {
	public ProductsController(ProductsRepository repo) {
		super(repo);
	}
}
