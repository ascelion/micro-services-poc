package ascelion.micro.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsExclude;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class Order extends AbstractEntity<Order> {
	public enum State {
		PENDING,
		APPROVED,
	}

	@NotNull
	private UUID customerId;
	@Builder.Default
	private State state = State.PENDING;
	@NotNull
	private UUID deliveryAddressId;
	private UUID billingAddressId;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderBy("ord")
	@EqualsExclude
	private List<OrderItem> items;

	public List<OrderItem> getItems() {
		if (this.items == null) {
			this.items = new ArrayList<>();
		}

		return this.items;
	}

	@PreUpdate
	@PrePersist
	void reorder() {
		if (this.items != null) {
			for (int o = 0; o < this.items.size(); o++) {
				this.items.get(o).setOrder(this);
				this.items.get(o).ord(o);
			}
		}
	}

	@Override
	public boolean beq(Order that) {
		return super.beq(that) &&
				beq(this.items, that.items);
	}
}
