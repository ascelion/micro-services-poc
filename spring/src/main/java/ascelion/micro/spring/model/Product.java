package ascelion.micro.spring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@EqualsAndHashCode(of = "id") // cannot use @Data, I prefer identity equality
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// the builder is used in tests
@Builder(access = AccessLevel.PACKAGE, builderClassName = "Builder")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	@Size(min = 1, max = 250, message = "{product.invalid.name}")
	private String name;

	@NotNull
	@Size(min = 10, message = "{product.invalid.description}")
	private String description;

	@NotNull
	@Min(value = 0, message = "{product.invalid.price}")
	@Column(name = "price", scale = 2)
	private BigDecimal currentPrice;

	@NotNull
	@Column(name = "updated")
	@Setter(AccessLevel.NONE)
	private LocalDateTime lastUpdate;

	@PrePersist
	@PreUpdate
	private void touch() {
		this.lastUpdate = LocalDateTime.now();
	}
}
