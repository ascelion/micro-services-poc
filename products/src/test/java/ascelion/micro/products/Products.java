package ascelion.micro.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility to create a product.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Products {

	static public Map<UUID, Product> generate(int count) {
		return IntStream.range(1, count + 1)
				.mapToObj(Products::generateOne)
				.collect(toMap(Product::getId, UnaryOperator.identity()));
	}

	static public Product generateOne(int id) {
		return generateOne(id, true);
	}

	static public Product generateOne(int id, boolean genId) {
		return Product.builder()
				.id(genId ? UUID.randomUUID() : null)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.name("Product " + id)
				.description("Description of product " + id)
				.price(new BigDecimal(id))
				.build();
	}
}
