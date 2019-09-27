package ascelion.micro.customers;

import javax.persistence.Entity;
import javax.persistence.Table;

import ascelion.micro.shared.model.AbstractEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE, builderClassName = "Builder")
public class Customer extends AbstractEntity<Customer> {
	private String firstName;
	private String lastName;
}
