package ascelion.micro.products;

import ascelion.micro.endpoint.Endpoint;
import ascelion.micro.endpoint.EntityEndpoint;

@Endpoint("products")
public class ProductsController extends EntityEndpoint<Product, ProductRequest> {
	public ProductsController(ProductsRepository repo) {
		super(repo);
	}
}
