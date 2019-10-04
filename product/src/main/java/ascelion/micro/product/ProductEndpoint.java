package ascelion.micro.product;

import ascelion.micro.product.api.Product;
import ascelion.micro.product.api.ProductRequest;
import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpointBase;

@Endpoint("products")
public class ProductEndpoint extends EntityEndpointBase<Product, ProductRepo, ProductRequest> {
	public ProductEndpoint(ProductRepo repo) {
		super(repo);
	}
}
