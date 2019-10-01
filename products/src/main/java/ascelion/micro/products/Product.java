package ascelion.micro.products;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class Product extends AbstractEntity<Product> {
	@NotNull
	@Size(min = 1, max = 250, message = "{product.invalid.name}")
	private String name;

	@NotNull
	@Size(min = 10, message = "{product.invalid.description}")
	private String description;

	@NotNull
	@Min(value = 0, message = "{product.invalid.price}")
	@Column(scale = 2)
	private BigDecimal price;

	@NotNull
	@Min(value = 0, message = "{product.invalid.stock}")
	@Column(scale = 2)
	private BigDecimal stock;
}
