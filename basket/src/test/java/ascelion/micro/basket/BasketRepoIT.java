package ascelion.micro.basket;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import ascelion.micro.basket.api.Basket;
import ascelion.micro.basket.api.BasketItem;
import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomDecimal;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@JpaEntityIT
public class BasketRepoIT {
	@Autowired
	private EntityManager tem;
	@Autowired
	private BasketRepo repo;
	@Autowired
	private BasketItemRepo itmRepo;

	@Before
	public void setUp() {
		this.tem.setFlushMode(FlushModeType.AUTO);
	}

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final var o1 = Basket.builder()
				.customerId(randomUUID())
				.build();
		final var i1 = BasketItem.builder()
				.productId(randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();
		final var i2 = BasketItem.builder()
				.productId(UUID.randomUUID())
				.quantity(randomDecimal(10, 20))
				.build();

		o1.merge(i1, i2);

		this.tem.persist(o1);
		this.tem.flush();
		this.tem.detach(o1);

		assertThat(o1.getId(), notNullValue());

		final var o2o = this.repo.findById(o1.getId());

		assertThat(o2o.isPresent(), is(true));

		final var o2 = o2o.get();

		assertThat(o2, not(sameInstance(o1)));

		assertThat(o2.getId(), notNullValue());
		assertThat(o2.beq(o1), is(true));

		final var i1o = this.itmRepo.findById(i1.getId());

		assertThat(i1o.isPresent(), is(true));
		assertThat(i1.beq(i1o.get()), is(true));

		final var i2o = this.itmRepo.findByProductId(o1.getId(), i2.getProductId());

		assertThat(i2o.isPresent(), is(true));
		assertThat(i2.beq(i2o.get()), is(true));
	}
}
