package ascelion.micro.products;

import java.util.Optional;

import javax.persistence.EntityManager;

import ascelion.micro.tests.JpaEntityIT;

import static ascelion.micro.tests.RandomUtils.randomAscii;
import static ascelion.micro.tests.RandomUtils.randomDecimal;
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
public class ProductEntityIT {
	@Autowired
	private EntityManager tem;

	@Autowired
	private ProductsRepository repo;

	/**
	 * Simple test that validates the table creation.
	 */
	@Test
	@Transactional
	public void validate_table_mappings() {
		final Product p1 = Product.builder()
				.name(randomAscii(10, 20))
				.description(randomAscii(10, 20))
				.price(randomDecimal(0, 100))
				.stock(randomDecimal(0, 100))
				.build();

		this.tem.persist(p1);
		this.tem.flush();
		this.tem.detach(p1);

		assertThat(p1.getId(), notNullValue());

		final Optional<Product> p2o = this.repo.findById(p1.getId());

		assertThat(p2o.isPresent(), is(true));

		final Product p2 = p2o.get();

		assertThat(p2, not(sameInstance(p1)));
		assertThat(p2.beq(p1), is(true));
	}
}
