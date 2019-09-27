package ascelion.micro.orders;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ascelion.micro.shared.model.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE, builderClassName = "Builder")
public class OrderItem extends AbstractEntity<OrderItem> {
	private UUID productId;
	private BigDecimal quantity;

	@ManyToOne(optional = false)
	@JsonIgnore
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.PACKAGE)
	private Order order;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private int ord;

	void ord(int ord) {
		this.ord = ord;
	}
}
