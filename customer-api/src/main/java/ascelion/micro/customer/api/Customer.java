package ascelion.micro.customer.api;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
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
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Customer extends AbstractEntity<Customer> {
	@NotNull
	private String firstName;

	@NotNull
	private String lastName;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "cards", joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"))
	@EqualsExclude
	@Column(name = "number")
	private Set<CardNumber> cards;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinTable(name = "customers_addresses",
			joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "address_id", referencedColumnName = "id"))
	@OrderColumn(name = "ord")
	@EqualsExclude
	private List<Address> addresses;

	@Override
	public boolean beq(Customer that) {
		return super.beq(that)
				&& beq(this.addresses, that.addresses)
				&& Arrays.equals(this.cards.toArray(), that.cards.toArray());
	}
}
