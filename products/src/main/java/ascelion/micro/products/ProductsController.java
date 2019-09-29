package ascelion.micro.products;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpointBase;

@Endpoint("products")
public class ProductsController extends EntityEndpointBase<Product, ProductsRepository, ProductRequest> {
	public ProductsController(ProductsRepository repo) {
		super(repo);
	}
}
