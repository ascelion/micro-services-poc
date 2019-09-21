package ascelion.micro.spring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import ascelion.micro.spring.model.Product;
import ascelion.micro.spring.model.Product.Builder;

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
		final Builder bld = Product.builder()
		        .name("Product " + id)
		        .description("Description of product " + id)
		        .currentPrice(new BigDecimal(id))
		        .lastUpdate(LocalDateTime.now());

		return (setId ? bld.id(id) : bld)
		        .build();
	}
}
