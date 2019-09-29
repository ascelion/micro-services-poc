package ascelion.micro.reservations;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ascelion.micro.products.Product;
import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class Reservation extends AbstractEntity<Reservation> {
	@ManyToOne(optional = false)
	@NotNull
	private Product product;
	@NotNull
	private UUID ownerId;
	@NotNull
	private BigDecimal quantity;
}
