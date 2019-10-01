package ascelion.micro.baskets;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ascelion.micro.mapper.BBField;
import ascelion.micro.mapper.BBMap;
import ascelion.micro.reservations.ReservationRequest;
import ascelion.micro.shared.model.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "basket_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@BBMap(to = ReservationRequest.class, bidi = false, fields = {
		@BBField(from = "basket.id", to = "ownerId")
})
public class BasketItem extends AbstractEntity<BasketItem> {
	@ManyToOne(optional = false)
	@JsonIgnore
	@Setter(AccessLevel.PACKAGE)
	private Basket basket;

	@NonNull
	private UUID productId;
	@NonNull
	@Builder.Default
	private BigDecimal quantity = BigDecimal.ZERO;

	private boolean expired;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private int ord;

	BasketItem ord(int ord) {
		this.ord = ord;

		return this;
	}

	BasketItem addQuantity(BigDecimal quantity) {
		this.quantity = this.quantity.add(quantity);

		return this;
	}
}
