package ascelion.micro.customer.api;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Address extends AbstractEntity<Address> {
	@NotNull
	private String country;
	private String region;
	@NotNull
	private String locality;
	@NotNull
	private String zip;
	@NotNull
	private String street;
	@NotNull
	private String number;
	@NotNull
	private String extra;
}
