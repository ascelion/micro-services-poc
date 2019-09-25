package ascelion.micro.products;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpoint;

@Endpoint("products")
public class ProductsController extends EntityEndpoint<Product, ProductRequest> {
	public ProductsController(ProductsRepository repo) {
		super(repo);
	}
}
