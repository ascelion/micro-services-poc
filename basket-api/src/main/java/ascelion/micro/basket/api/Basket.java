package ascelion.micro.basket.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ascelion.micro.shared.model.AbstractEntity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsExclude;

@Entity
@Table(name = "baskets")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonDeserialize(converter = Basket.RelinkItems.class)
public class Basket extends AbstractEntity<Basket> {
	static class RelinkItems extends StdConverter<Basket, Basket> {
		@Override
		public Basket convert(Basket basket) {
			return basket.reLink();
		}
	}

	public enum Status {
		CONSTRUCT,
		ORDERING,
		WAITING_FOR_PAYMENT,
		SHIPPING,
		RETURNED,
		FINALIZED,
	}

	@NotNull
	@Setter
	private UUID customerId;

	@Enumerated(EnumType.STRING)
	@Default
	@Setter
	private Status status = Status.CONSTRUCT;

	@OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderBy("index")
	@EqualsExclude
	private List<BasketItem> items;

	public List<BasketItem> getItems() {
		return this.items != null ? unmodifiableList(this.items) : emptyList();
	}

	public Basket merge(@NonNull BasketItem... items) {
		if (this.items == null) {
			this.items = new ArrayList<>();
		}

		this.items.addAll(asList(items));

		return doMerge();
	}

	public Basket delItem(@NonNull UUID itemId) {
		if (this.items == null) {
			return this;
		}

		this.items.removeIf(item -> itemId.equals(item.getId()));

		return doMerge();
	}

	public Optional<BasketItem> getItem(@NonNull UUID itemId) {
		if (this.items == null) {
			return Optional.empty();
		}

		return this.items.stream().filter(item -> itemId.equals(item.getId())).findFirst();
	}

	@Override
	public boolean beq(Basket that) {
		return super.beq(that) &&
				beq(this.items, that.items);
	}

	private Basket doMerge() {
		final Collection<BasketItem> merged = this.items.stream()
				.collect(
						groupingBy(
								BasketItem::getProductId,
								LinkedHashMap::new, // preserve order
								reducing(this::mergeItems)))
				.values().stream()
				.map(Optional::get)
				.collect(toList());

		this.items.clear();
		this.items.addAll(merged);

		return reLink();
	}

	private Basket reLink() {
		for (int k = 0; k < this.items.size(); k++) {
			this.items.get(k).setBasket(this);
			this.items.get(k).setIndex(k);
		}

		return this;
	}

	private BasketItem mergeItems(BasketItem i1, BasketItem i2) {
		if (i1 == null) {
			return i2;
		}
		if (i2 == null) {
			return i1;
		}
		if (i1.getId() == null) {
			return i2.addQuantity(i1.getQuantity());
		}

		return i1.addQuantity(i2.getQuantity());
	}

	public Basket pruneEmptpyItems() {
		if (this.items != null) {
			this.items.removeIf(item -> {
				final BigDecimal q = item.getQuantity();
				final BigDecimal z = BigDecimal.ZERO.setScale(q.scale());

				return q.equals(z);
			});
		}

		return this;
	}
}
