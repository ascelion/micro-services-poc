package ascelion.micro.orders;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
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
public class OrderEntityIT {
	@Autowired
	private EntityManager tem;

	@Autowired
	private OrdersRepository repo;

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final Order o1 = Order.builder()
				.customerId(randomUUID())
				.deliveryAddressId(randomUUID())
				.billingAddressId(randomUUID())
				.build();
		final OrderItem i1 = OrderItem.builder()
				.productId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();
		final OrderItem i2 = OrderItem.builder()
				.productId(UUID.randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();

		o1.getItems().add(i1);
		o1.getItems().add(i2);

		this.tem.setFlushMode(FlushModeType.AUTO);

		this.tem.persist(o1);
		this.tem.flush();
		this.tem.detach(o1);

		assertThat(o1.getId(), notNullValue());

		final Optional<Order> o2o = this.repo.findById(o1.getId());

		assertThat(o2o.isPresent(), is(true));

		final Order o2 = o2o.get();

		assertThat(o2, not(sameInstance(o1)));

		assertThat(o2.getId(), notNullValue());
		assertThat(o2.beq(o1), is(true));
	}
}
