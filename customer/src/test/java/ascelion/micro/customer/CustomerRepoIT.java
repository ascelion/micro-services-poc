package ascelion.micro.customer;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.customer.CustomerRepo;
import ascelion.micro.customer.api.CardNumber;
import ascelion.micro.customer.api.Customer;
import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomAscii;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@JpaEntityIT
public class CustomerRepoIT {
	@Autowired
	private EntityManager tem;

	@Autowired
	private CustomerRepo repo;

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final Customer c1 = Customer.builder()
				.firstName(randomAscii(10, 20))
				.lastName(randomAscii(10, 20))
				.cards(singleton(CardNumber.valueOf("AAAAAAAAAAAAA")))
				.build();

		this.tem.setFlushMode(FlushModeType.AUTO);

		this.tem.persist(c1);
		this.tem.flush();
		this.tem.detach(c1);

		assertThat(c1.getId(), notNullValue());

		final Optional<Customer> c2o = this.repo.findById(c1.getId());

		assertThat(c2o.isPresent(), is(true));

		final Customer c2 = c2o.get();

		assertThat(c2, not(sameInstance(c1)));

		assertThat(c2.getId(), notNullValue());
		assertThat(c2.beq(c1), is(true));
	}
}
