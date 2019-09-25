package ascelion.micro.customers;

import javax.persistence.Column;
import javax.persistence.Entity;

import ascelion.micro.shared.model.AbstractEntity;

@Entity(name = "customers")
public class Customer extends AbstractEntity {
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
}
