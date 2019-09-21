package ascelion.micro.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.micro.model.AbstractEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends AbstractEntity {
	static class Builder {
		private Long id;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private String name;
		private String description;
		private BigDecimal price;

		Builder id(Long id) {
			this.id = id;

			return this;
		}

		Builder createdAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;

			return this;
		}

		Builder updatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;

			return this;
		}

		Builder name(String name) {
			this.name = name;

			return this;
		}

		Builder description(String description) {
			this.description = description;

			return this;
		}

		Builder price(BigDecimal price) {
			this.price = price;

			return this;
		}

		Product build() {
			return new Product(this.id, this.createdAt, this.updatedAt, this.name, this.description, this.price);
		}
	}

	static Builder builder() {
		return new Builder();
	}

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

	Product(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String description, BigDecimal price) {
		super(id, createdAt, updatedAt);

		this.name = name;
		this.description = description;
		this.price = price;
	}
}
