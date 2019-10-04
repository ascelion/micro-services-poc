package ascelion.micro.product;

import java.math.BigDecimal;
import java.util.Optional;

import ascelion.micro.product.api.Product;
import ascelion.micro.shared.model.EntityRepo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends EntityRepo<Product> {
	default BigDecimal stockAvailability(@Param("product") Product product) {
		return queryStockAvailability(product).orElse(product.getStock());
	}

	@Query("" +
			"SELECT p.stock - sum(r.quantity) FROM Product p" +
			" JOIN Reservation r ON p = r.product" +
			" WHERE p = :product" +
			" GROUP by p" +
			"")
	Optional<BigDecimal> queryStockAvailability(@Param("product") Product product);
}
