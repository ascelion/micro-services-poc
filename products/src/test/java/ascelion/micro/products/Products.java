package ascelion.micro.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility to create a product.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Products {

	static public List<Product> generate(int count) {
		return LongStream.range(1L, count + 1L)
				.mapToObj(Products::generateOne)
				.collect(toList());
	}

	static public Product generateOne(long id) {
		return generateOne(id, true);
	}

	static public Product generateOne(long id, boolean setId) {
		return Product.builder()
				.id(setId ? id : null)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.name("Product " + id)
				.description("Description of product " + id)
				.price(new BigDecimal(id))
				.build();
	}
}
